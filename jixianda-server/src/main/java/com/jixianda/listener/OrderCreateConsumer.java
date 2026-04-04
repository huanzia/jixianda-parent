package com.jixianda.listener;

import com.jixianda.config.RabbitMQConfiguration;
import com.jixianda.server.config.RabbitMQConfig;
import com.jixianda.dto.OrderMessageDTO;
import com.jixianda.entity.AddressBook;
import com.jixianda.entity.Dish;
import com.jixianda.entity.OrderDetail;
import com.jixianda.entity.Orders;
import com.jixianda.entity.ShoppingCart;
import com.jixianda.entity.User;
import com.jixianda.exception.OrderBusinessException;
import com.jixianda.mapper.DishMapper;
import com.jixianda.mapper.OrderDetailMapper;
import com.jixianda.mapper.OrderMapper;
import com.jixianda.mapper.ShoppingCartMapper;
import com.jixianda.mapper.WarehouseSkuMapper;
import com.jixianda.result.Result;
import com.jixianda.client.UserClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional(rollbackFor = Exception.class)
/**
 * 订单创建消息消费者。
 * 这个类属于 MQ 消费层，负责在消息到达后真正创建订单主表和明细表，并清理购物车，
 * 通过异步消费把下单主线程和数据库写入解耦，提升交易链路的响应速度和稳定性。
 */
public class OrderCreateConsumer {

    // 订单主表持久化入口，负责保存订单级别状态与金额信息。
    private final OrderMapper orderMapper;
    // 订单明细持久化入口，负责保存每个购买项的快照。
    private final OrderDetailMapper orderDetailMapper;
    // 购物车持久化入口，订单创建成功后需要清理已下单购物车项。
    private final ShoppingCartMapper shoppingCartMapper;
    // 远程查询用户/地址信息，补全订单落库时必需的收货快照。
    private final UserClient userClient;
    // 秒杀场景下查询菜品基础信息，用于生成明细和金额。
    private final DishMapper dishMapper;
    // 秒杀消费时同步扣减数据库库存，和 Redis 预扣形成双层保护。
    private final WarehouseSkuMapper warehouseSkuMapper;
    // 用于把已创建订单投递到延迟队列，进入超时取消链路。
    private final RabbitTemplate rabbitTemplate;

    public OrderCreateConsumer(OrderMapper orderMapper,
                               OrderDetailMapper orderDetailMapper,
                               ShoppingCartMapper shoppingCartMapper,
                               UserClient userClient,
                               DishMapper dishMapper,
                               WarehouseSkuMapper warehouseSkuMapper,
                               RabbitTemplate rabbitTemplate) {
        this.orderMapper = orderMapper;
        this.orderDetailMapper = orderDetailMapper;
        this.shoppingCartMapper = shoppingCartMapper;
        this.userClient = userClient;
        this.dishMapper = dishMapper;
        this.warehouseSkuMapper = warehouseSkuMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 消费订单创建消息。
     * 普通下单和秒杀下单都会走这里，消费端负责真正创建订单、写入明细、清空购物车，
     * 并在结束后投递延迟取消消息。MQ 可能重复投递，所以这里先按订单号做幂等检查。
     */
    @RabbitListener(queues = {RabbitMQConfig.ORDER_CREATE_QUEUE, RabbitMQConfig.SECKILL_QUEUE})
    public void handleOrderCreate(OrderMessageDTO message) {
        // 第一道保护：消息体为空直接拒绝处理，避免后续空指针和脏数据入库。
        if (message == null) {
            throw new OrderBusinessException("order message is null");
        }

        // 这里把消息对象显式命名为订单消息，后续分支都以“消费快照”为唯一输入。
        OrderMessageDTO orderMessageDTO = message;
        log.info("[MQ post-receive check] received message userId={}", orderMessageDTO.getUserId());

        // 幂等处理：如果订单号已经存在，说明这条消息可能被重复消费，直接忽略即可。
        if (message.getOrderNumber() != null && orderMapper.getByNumber(message.getOrderNumber()) != null) {
            return;
        }

        // 秒杀单和普通单共享同一个监听入口，但创建策略不同，先分流到秒杀专用处理逻辑。
        if (Boolean.TRUE.equals(message.getSeckill())) {
            handleSeckillCreate(message);
            return;
        }

        // 普通单依赖“购物车快照”创建明细，快照为空说明消息不完整，必须终止。
        if (message.getShoppingCartList() == null || message.getShoppingCartList().isEmpty()) {
            throw new OrderBusinessException("shopping cart snapshot is empty");
        }

        // 消费端必须再次校验 userId，防止上游消息构造异常导致越权或脏订单。
        Long userId = orderMessageDTO.getUserId();
        if (userId == null) {
            throw new OrderBusinessException("user id is null in order message");
        }

        // 创建订单时必须先查收货地址，后续订单详情页和配送流程都要依赖这份信息。
        AddressBook addressBook = fetchAddressBook(message.getAddressBookId(), userId);
        if (addressBook == null) {
            throw new OrderBusinessException("address not found when creating order");
        }

        // 主表组装阶段：把消息快照中的交易字段固化到订单主表，形成“订单聚合根”。
        Orders orders = new Orders();
        User user = fetchUser(userId);
        Integer payMethod = message.getPayMethod() == null ? 1 : message.getPayMethod();
        BigDecimal totalAmount = message.getTotalAmount() == null ? BigDecimal.ZERO : message.getTotalAmount();
        String remark = message.getRemark() == null ? "" : message.getRemark();
        Integer deliveryStatus = message.getDeliveryStatus() == null ? 0 : message.getDeliveryStatus();
        Integer tablewareNumber = message.getTablewareNumber() == null ? 0 : message.getTablewareNumber();
        Integer tablewareStatus = message.getTablewareStatus() == null ? 0 : message.getTablewareStatus();
        int packAmount = message.getPackAmount() == null ? 0 : message.getPackAmount();
        LocalDateTime estimatedDeliveryTime = message.getEstimatedDeliveryTime();

        orders.setNumber(message.getOrderNumber());
        orders.setOrderTime(message.getOrderTime());
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setPayStatus(Orders.UN_PAID);
        orders.setUserId(orderMessageDTO.getUserId());
        orders.setAddressBookId(message.getAddressBookId());
        orders.setWarehouseId(message.getWarehouseId());
        orders.setPayMethod(payMethod);
        orders.setAmount(totalAmount);
        orders.setRemark(remark);
        orders.setEstimatedDeliveryTime(estimatedDeliveryTime);
        orders.setDeliveryStatus(deliveryStatus);
        orders.setTablewareNumber(tablewareNumber);
        orders.setTablewareStatus(tablewareStatus);
        orders.setPackAmount(packAmount);
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setAddress(addressBook.getDetail());
        if (user != null) {
            orders.setUserName(user.getName());
        }

        // 先落主表拿到 orderId，后续明细要通过 orderId 建立一对多关联。
        orderMapper.insert(orders);

        // 订单主表和明细表分开存，主表承载订单聚合信息，明细表承载每个商品的购买快照。
        List<OrderDetail> orderDetailList = new ArrayList<OrderDetail>();
        // 明细组装阶段：把购物车快照逐条固化成订单明细，避免后续商品信息变化影响历史订单。
        for (ShoppingCart cart : message.getShoppingCartList()) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetailList);

        // 订单创建成功后清空购物车，表示这批商品已经转入订单快照。
        shoppingCartMapper.deleteAllByUserId(userId);
        // 发送延迟消息进入超时取消链路，给用户预留支付窗口。
        rabbitTemplate.convertAndSend(RabbitMQConfiguration.ORDER_DELAY_QUEUE, message);
        log.info("[延迟队列] 订单已创建，开始 1 分钟倒计时检查支付状态，订单号：{}", message.getOrderNumber());
        log.info("Order created async, orderNumber={}", message.getOrderNumber());
    }

    /**
     * 处理秒杀订单创建。
     * 秒杀链路会先在 Redis 和数据库里都预占库存，再创建订单明细，防止高并发下超卖。
     */
    private void handleSeckillCreate(OrderMessageDTO orderMessageDTO) {
        System.out.println("[消费者监控] 领到消息了！内容是：" + orderMessageDTO);
        try {
            // 秒杀消息的关键字段校验：缺仓、缺商品都不能继续落库。
            if (orderMessageDTO.getDishId() == null) {
                throw new OrderBusinessException("dish id is null");
            }
            if (orderMessageDTO.getWarehouseId() == null) {
                throw new OrderBusinessException("warehouse id is null");
            }
            Long addressBookId = orderMessageDTO.getAddressBookId();
            if (addressBookId == null) {
                log.error("seckill message missing addressBookId, orderNumber={}", orderMessageDTO.getOrderNumber());
                // 地址缺失属于不可恢复消息，直接拒绝重回队列，避免死循环消费。
                throw new AmqpRejectAndDontRequeueException("addressBookId missing");
            }

            System.out.println("[消费者监控] 正在为地址 ID: " + addressBookId + " 查询详情...");
            AddressBook addressBook = fetchAddressBook(addressBookId, orderMessageDTO.getUserId());
            if (addressBook == null) {
                log.error("address not found for seckill, orderNumber={}, addressBookId={}", orderMessageDTO.getOrderNumber(), addressBookId);
                // 地址不存在通常是业务数据异常，重复消费也无法修复，直接拒绝重试。
                throw new AmqpRejectAndDontRequeueException("address not found");
            }

            // 秒杀购买数量兜底为 1，避免异常值导致库存处理错误。
            int number = (orderMessageDTO.getNumber() == null || orderMessageDTO.getNumber() < 1) ? 1 : orderMessageDTO.getNumber();
            Dish dish = dishMapper.getById(orderMessageDTO.getDishId());
            if (dish == null) {
                throw new OrderBusinessException("dish not found");
            }

            if (orderMessageDTO.getUserId() == null) {
                log.error("order message userId is null, skip remote user query");
                return;
            }
            // 秒杀订单也要补齐用户信息，方便后续订单详情和配送数据完整。
            User user = fetchUser(orderMessageDTO.getUserId());
            BigDecimal totalAmount = orderMessageDTO.getTotalAmount();
            if (totalAmount == null) {
                BigDecimal unitPrice = dish.getPrice() == null ? BigDecimal.ZERO : dish.getPrice();
                totalAmount = unitPrice.multiply(BigDecimal.valueOf(number));
            }

            // 秒杀主表落库：状态初始化为待支付，和普通订单保持一致的状态机入口。
            Orders orders = new Orders();
            orders.setNumber(orderMessageDTO.getOrderNumber());
            orders.setOrderTime(orderMessageDTO.getOrderTime());
            orders.setStatus(Orders.PENDING_PAYMENT);
            orders.setPayStatus(Orders.UN_PAID);
            orders.setUserId(orderMessageDTO.getUserId());
            orders.setAddressBookId(addressBookId);
            orders.setWarehouseId(orderMessageDTO.getWarehouseId());
            orders.setPayMethod(orderMessageDTO.getPayMethod() == null ? 1 : orderMessageDTO.getPayMethod());
            orders.setAmount(totalAmount);
            orders.setRemark(orderMessageDTO.getRemark() == null ? "SECKILL" : orderMessageDTO.getRemark());
            orders.setDeliveryStatus(1);
            orders.setPackAmount(0);
            orders.setTablewareNumber(1);
            orders.setTablewareStatus(1);
            orders.setAddress(addressBook.getDetail());
            orders.setPhone(addressBook.getPhone());
            orders.setConsignee(addressBook.getConsignee());
            if (user != null) {
                orders.setUserName(user.getName());
            }

            System.out.println("[消费者监控] 准备向数据库插入订单：" + orders.getNumber());
            orderMapper.insert(orders);

            // 秒杀明细通常只有一条，但仍走明细表，保证查询模型与普通订单一致。
            List<OrderDetail> orderDetailList = new ArrayList<OrderDetail>();
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orders.getId());
            orderDetail.setDishId(dish.getId());
            orderDetail.setName(dish.getName());
            orderDetail.setImage(dish.getImage());
            orderDetail.setNumber(number);
            orderDetail.setAmount(dish.getPrice());
            orderDetailList.add(orderDetail);
            orderDetailMapper.insertBatch(orderDetailList);

            // 秒杀链路里数据库库存也要同步扣减，和 Redis 预扣保持一致。
            int affected = warehouseSkuMapper.deductStock(orderMessageDTO.getWarehouseId(), orderMessageDTO.getDishId(), number);
            if (affected <= 0) {
                throw new OrderBusinessException("db stock not enough");
            }
            // 秒杀订单创建完同样进入延迟取消队列，给用户支付窗口。
            rabbitTemplate.convertAndSend(RabbitMQConfiguration.ORDER_DELAY_QUEUE, orderMessageDTO);
            log.info("[延迟队列] 订单已创建，开始 1 分钟倒计时检查支付状态，订单号：{}", orderMessageDTO.getOrderNumber());
        } catch (Exception e) {
            // 消费异常直接抛出，让事务回滚并交给 MQ 重试/拒绝策略处理，避免半成功数据。
            System.out.println("[消费者监控] ❌ 落库失败！报错原因：" + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 远程查询收货地址。
     * 这里通过 user 服务拿到地址信息，保证订单落库时使用的是已经校验过权限的地址数据。
     */
    private AddressBook fetchAddressBook(Long addressBookId, Long userId) {
        if (userId == null) {
            log.error("userId is null when fetching address, addressBookId={}", addressBookId);
            return null;
        }
        Result<AddressBook> result = userClient.getAddressBookById(addressBookId, userId);
        if (result == null || result.getCode() == null || result.getCode() != 1) {
            return null;
        }
        return result.getData();
    }

    /**
     * 远程查询用户信息。
     * 订单里会保留用户名称等快照字段，所以这里需要把用户基础信息查出来。
     */
    private User fetchUser(Long userId) {
        if (userId == null) {
            log.error("userId is null, skip remote user query");
            return null;
        }
        Result<User> result = userClient.getUserById(userId);
        if (result == null || result.getCode() == null || result.getCode() != 1) {
            return null;
        }
        return result.getData();
    }
}
