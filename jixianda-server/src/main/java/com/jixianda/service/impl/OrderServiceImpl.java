package com.jixianda.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.xiaoymin.knife4j.core.util.CollectionUtils;
import com.jixianda.constant.MessageConstant;
import com.jixianda.context.BaseContext;
import com.jixianda.dto.OrdersCancelDTO;
import com.jixianda.dto.OrdersConfirmDTO;
import com.jixianda.dto.OrderMessageDTO;
import com.jixianda.dto.OrdersPageQueryDTO;
import com.jixianda.dto.OrdersPaymentDTO;
import com.jixianda.dto.OrdersRejectionDTO;
import com.jixianda.dto.OrdersSubmitDTO;
import com.jixianda.entity.Dish;
import com.jixianda.entity.OrderDetail;
import com.jixianda.entity.Orders;
import com.jixianda.entity.ShoppingCart;
import com.jixianda.entity.WarehouseSku;
import com.jixianda.exception.OrderBusinessException;
import com.jixianda.exception.ShoppingCartBusinessException;
import com.jixianda.exception.UserNotLoginException;
import com.jixianda.mapper.DishMapper;
import com.jixianda.mapper.OrderDetailMapper;
import com.jixianda.mapper.OrderMapper;
import com.jixianda.mapper.ShoppingCartMapper;
import com.jixianda.mapper.WarehouseSkuMapper;
import com.jixianda.result.PageResult;
import com.jixianda.server.config.RabbitMQConfig;
import com.jixianda.service.OrderService;
import com.jixianda.utils.WeChatPayUtil;
import com.jixianda.vo.OrderPaymentVO;
import com.jixianda.vo.OrderStatisticsVO;
import com.jixianda.vo.OrderSubmitVO;
import com.jixianda.vo.OrderVO;
import com.jixianda.websocket.WebSocketServer;
import io.seata.spring.annotation.GlobalTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
/**
 * 订单业务实现类。
 * 这个类属于 service 实现层，是 server 模块交易主链路的核心，
 * 负责把购物车、地址、仓库、库存、支付、MQ 和 Redis 串起来，完成下单、支付、取消和超时处理。
 */
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    private static final String SECKILL_STOCK_KEY_PREFIX = "seckill:stock:";

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private WarehouseSkuMapper warehouseSkuMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private WebSocketServer webSocketServer;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private DefaultRedisScript<Long> deductStockScript;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${jixianda.mock-pay:false}")
    private boolean mockPay;

    /**
     * 提交订单主流程。
     * 这里不是直接落单，而是先校验地址、仓和购物车，再预扣库存并把订单创建消息发到 MQ，
     * 这样可以把“创建订单”和“持久化订单明细”解耦，同时保留分布式事务边界。
     */
    @Override
    @GlobalTransactional(name = "jixianda-order-submit-tx", rollbackFor = Exception.class, timeoutMills = 120000)
    public OrderSubmitVO orderSubmit(OrdersSubmitDTO ordersSubmitDTO) {
        if (ordersSubmitDTO == null || ordersSubmitDTO.getAddressBookId() == null) {
            throw new ShoppingCartBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        Long warehouseId = ordersSubmitDTO.getWarehouseId();
        if (warehouseId == null) {
            throw new OrderBusinessException("warehouse id is required");
        }

        ShoppingCart shoppingCart = new ShoppingCart();
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        if (shoppingCartList == null || shoppingCartList.isEmpty()) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        // 先校验购物车是否属于同一个仓，再做库存预扣，避免跨仓商品混单。
        for (ShoppingCart cart : shoppingCartList) {
            if (cart.getWarehouseId() != null && !warehouseId.equals(cart.getWarehouseId())) {
                throw new OrderBusinessException("cross-warehouse cart is not allowed");
            }
            Long skuId = cart.getDishId() != null ? cart.getDishId() : cart.getSetmealId();
            if (skuId == null) {
                throw new OrderBusinessException("shopping cart item is invalid");
            }
            int count = cart.getNumber() == null ? 0 : cart.getNumber();
            int affected = warehouseSkuMapper.deductStock(warehouseId, skuId, count);
            if (affected <= 0) {
                WarehouseSku sku = warehouseSkuMapper.selectByWarehouseAndDishId(warehouseId, skuId);
                if (sku == null) {
                    throw new OrderBusinessException("item is not in this warehouse");
                }
                // 库存不足时直接中断，下单链路必须先预留出可卖库存。
                throw new OrderBusinessException("insufficient stock");
            }
        }

        String orderNumber = nextOrderNumber();
        LocalDateTime orderTime = LocalDateTime.now();
        String remark = ordersSubmitDTO.getRemark() == null ? "" : ordersSubmitDTO.getRemark();
        Integer deliveryStatus = ordersSubmitDTO.getDeliveryStatus() == null ? 0 : ordersSubmitDTO.getDeliveryStatus();
        Integer tablewareNumber = ordersSubmitDTO.getTablewareNumber() == null ? 0 : ordersSubmitDTO.getTablewareNumber();
        Integer tablewareStatus = ordersSubmitDTO.getTablewareStatus() == null ? 0 : ordersSubmitDTO.getTablewareStatus();
        Integer packAmount = ordersSubmitDTO.getPackAmount() == null ? 0 : ordersSubmitDTO.getPackAmount();

        // 订单主表和明细表真正入库由消费者完成，这里只发送创建消息，降低主线程耦合度。
        OrderMessageDTO orderMessageDTO = OrderMessageDTO.builder()
                .userId(userId)
                .orderNumber(orderNumber)
                .addressBookId(ordersSubmitDTO.getAddressBookId())
                .warehouseId(warehouseId)
                .payMethod(ordersSubmitDTO.getPayMethod())
                .totalAmount(ordersSubmitDTO.getAmount())
                .remark(remark)
                .estimatedDeliveryTime(ordersSubmitDTO.getEstimatedDeliveryTime())
                .deliveryStatus(deliveryStatus)
                .tablewareNumber(tablewareNumber)
                .tablewareStatus(tablewareStatus)
                .packAmount(packAmount)
                .orderTime(orderTime)
                .shoppingCartList(shoppingCartList)
                .build();

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.ORDER_CREATE_ROUTING_KEY,
                orderMessageDTO
        );

        return OrderSubmitVO.builder()
                .id(null)
                .orderNumber(orderNumber)
                .orderAmount(ordersSubmitDTO.getAmount())
                .orderTime(orderTime)
                .processStatus("pending payment")
                .build();
    }

    /**
     * 秒杀下单。
     * 这条链路和普通下单不同，库存先在 Redis 里做原子预扣，再异步创建订单，适合高并发场景。
     */
    @Override
    public OrderSubmitVO seckill(Long warehouseId, Long dishId, Integer number, Long addressBookId) {
        if (warehouseId == null) {
            throw new OrderBusinessException("warehouse id is required");
        }
        if (dishId == null) {
            throw new OrderBusinessException("dish id is required");
        }
        if (addressBookId == null) {
            throw new OrderBusinessException("addressBookId is required");
        }

        int buyNumber = (number == null || number < 1) ? 1 : number;
        String stockKey = buildSeckillStockKey(warehouseId, dishId);
        preloadSeckillStockIfAbsent(warehouseId, dishId, stockKey);
        for (int i = 0; i < buyNumber; i++) {
            // Lua 脚本保证 Redis 预扣库存的原子性，避免并发下超卖。
            Long result = stringRedisTemplate.execute(deductStockScript, java.util.Collections.singletonList(stockKey));
            if (result == null || result == 0L) {
                throw new OrderBusinessException("insufficient stock");
            }
        }

        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            throw new UserNotLoginException(MessageConstant.USER_NOT_LOGIN);
        }

        String orderNumber = nextOrderNumber();
        LocalDateTime orderTime = LocalDateTime.now();
        BigDecimal totalAmount = BigDecimal.ZERO;

        try {
            Dish dish = dishMapper.getById(dishId);
            if (dish != null && dish.getPrice() != null) {
                totalAmount = dish.getPrice().multiply(BigDecimal.valueOf(buyNumber));
            }

            // 秒杀订单同样通过 MQ 异步创建，主线程只负责抢到库存后的消息投递。
            OrderMessageDTO messageDTO = OrderMessageDTO.builder()
                    .seckill(true)
                    .userId(userId)
                    .orderNumber(orderNumber)
                    .dishId(dishId)
                    .number(buyNumber)
                    .addressBookId(addressBookId)
                    .warehouseId(warehouseId)
                    .orderTime(orderTime)
                    .payMethod(1)
                    .totalAmount(totalAmount)
                    .remark("SECKILL")
                    .deliveryStatus(1)
                    .tablewareNumber(1)
                    .tablewareStatus(1)
                    .packAmount(0)
                    .build();

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.SECKILL_ROUTING_KEY,
                messageDTO
            );
        } catch (Exception e) {
            stringRedisTemplate.opsForValue().increment(stockKey, buyNumber);
            throw new OrderBusinessException("seckill order failed");
        }

        return OrderSubmitVO.builder()
                .orderNumber(orderNumber)
                .orderAmount(totalAmount)
                .orderTime(orderTime)
                .processStatus("pending payment")
                .build();
    }

    /**
     * 如果 Redis 中还没有秒杀库存键，就先从数据库回填一次。
     * 这样可以让秒杀链路在第一次访问时自动完成 Redis 库存初始化。
     */
    private void preloadSeckillStockIfAbsent(Long warehouseId, Long dishId, String stockKey) {
        Boolean exists = stringRedisTemplate.hasKey(stockKey);
        if (Boolean.TRUE.equals(exists)) {
            return;
        }
        com.jixianda.entity.WarehouseSku warehouseSku = warehouseSkuMapper.selectByWarehouseAndDishId(warehouseId, dishId);
        int stock = 0;
        if (warehouseSku != null && warehouseSku.getStock() != null && warehouseSku.getStock() > 0) {
            stock = warehouseSku.getStock();
        }
        stringRedisTemplate.opsForValue().setIfAbsent(stockKey, String.valueOf(stock));
    }

    /**
     * 构造秒杀库存键。
     * Redis 里的库存键必须带仓和商品维度，才能保证不同仓的秒杀库存互不干扰。
     */
    private String buildSeckillStockKey(Long warehouseId, Long dishId) {
        return SECKILL_STOCK_KEY_PREFIX + warehouseId + ":" + dishId;
    }

    /**
     * 生成订单号。
     * 订单号需要足够唯一且便于排查，所以使用时间戳加随机数的组合。
     */
    private String nextOrderNumber() {
        long ts = System.currentTimeMillis();
        int rand = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return ts + String.valueOf(rand);
    }

    /**
     * 发起支付前的订单校验。
     * 这里主要确认订单存在且仍处于待支付状态，再返回支付参数给前端继续走微信支付或 mock 支付流程。
     */
    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        Orders ordersDB = orderMapper.getByNumber(ordersPaymentDTO.getOrderNumber());

        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        if (ordersDB.getStatus() != Orders.PENDING_PAYMENT) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        if (mockPay) {
            log.info("[pay-dev] mock payment enabled, orderNumber={}", ordersPaymentDTO.getOrderNumber());
            paySuccess(ordersPaymentDTO.getOrderNumber());
            return OrderPaymentVO.builder()
                    .nonceStr("mock")
                    .paySign("mock")
                    .packageStr("mock")
                    .build();
        }

        return OrderPaymentVO.builder()
                .nonceStr("real")
                .paySign("real")
                .packageStr("real")
                .build();
    }

    /**
     * 支付成功后的状态推进。
     * 这个方法会把订单从待支付推进到已支付，并释放之前锁定的库存，表示库存已经正式确认。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void paySuccess(String outTradeNo) {
        if (outTradeNo == null || outTradeNo.trim().isEmpty()) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        int affected = orderMapper.updateStatusByNumberAndStatus(
                outTradeNo,
                Orders.PENDING_PAYMENT,
                Orders.CONFIRMED,
                Orders.PAID,
                LocalDateTime.now()
        );
        log.info("[order-status-update] orderNumber={}, affected={}", outTradeNo, affected);
        if (affected <= 0) {
            return;
        }
        // 支付完成后，原先锁在订单上的库存不再需要保留在 lock_stock 中。
        releaseOrderLockStock(ordersDB);
    }

    /**
     * 用户端分页查询订单。
     * 这个方法服务于“我的订单”页面，需要返回订单主信息以及每单对应的明细列表。
     */
    @Override
    public PageResult pageQuery4User(int pageNum, int pageSize, Integer status) {
        PageHelper.startPage(pageNum, pageSize);

        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        ordersPageQueryDTO.setStatus(status);

        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);
        List<OrderVO> list = new ArrayList<>();

        if (page != null && page.getTotal() > 0) {
            for (Orders orders : page) {
                Long orderId = orders.getId();
                List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderId);
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetails);
                list.add(orderVO);
            }
        }
        return new PageResult(page.getTotal(), list);
    }

    /**
     * 查询订单详情。
     * 这个方法通常由订单详情页使用，需要同时返回订单主表和明细表信息。
     */
    @Override
    public OrderVO details(Long id) {
        Orders orders = orderMapper.getById(id);
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());

        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }

    /**
     * 用户主动取消订单。
     * 取消后如果订单已经支付，还要先退款，再把订单状态改为取消，并把库存回滚回去。
     */
    @Override
    public void userCancelById(Long id) throws Exception {
        Orders ordersDB = orderMapper.getById(id);

        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        if (ordersDB.getStatus() > 2) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());

        if (ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            weChatPayUtil.refund(
                    ordersDB.getNumber(),
                    ordersDB.getNumber(),
                    new BigDecimal(0.01),
                    new BigDecimal(0.01));
            orders.setPayStatus(Orders.REFUND);
        }

        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("user cancelled");
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
        restoreOrderStock(ordersDB);
    }

    /**
     * 再来一单。
     * 这个方法会把历史订单明细重新转成购物车项，方便用户快速复购。
     */
    @Override
    public void repetition(Long id) {
        Long userId = BaseContext.getCurrentId();
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);

        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(x -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(x, shoppingCart, "id");
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCart;
        }).collect(Collectors.toList());

        shoppingCartMapper.insertBatch(shoppingCartList);
    }

    /**
     * 条件分页查询订单。
     * 这个方法更多服务于管理端检索和运营筛选，需要把订单列表按条件查出来再组装展示文案。
     */
    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);
        List<OrderVO> orderVOList = getOrderVOList(page);
        return new PageResult(page.getTotal(), orderVOList);
    }

    /**
     * 把订单列表组装成前端可展示的 VO。
     * 这里额外拼接订单商品描述，是为了让管理端列表不用再逐条展开明细页。
     */
    private List<OrderVO> getOrderVOList(Page<Orders> page) {
        List<OrderVO> orderVOList = new ArrayList<>();
        List<Orders> ordersList = page.getResult();
        if (!CollectionUtils.isEmpty(ordersList)) {
            for (Orders orders : ordersList) {
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                String orderDishes = getOrderDishesStr(orders);
                orderVO.setOrderDishes(orderDishes);
                orderVOList.add(orderVO);
            }
        }
        return orderVOList;
    }

    /**
     * 拼出订单里的商品摘要字符串。
     * 通过明细表把每个商品名称和数量串起来，便于列表页快速浏览订单内容。
     */
    private String getOrderDishesStr(Orders orders) {
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());
        List<String> orderDishList = orderDetailList.stream()
                .map(x -> x.getName() + "*" + x.getNumber() + ";")
                .collect(Collectors.toList());
        return String.join("", orderDishList);
    }

    /**
     * 订单状态统计。
     * 这个方法服务于后台看板，只统计关键状态数量，不返回明细数据。
     */
    @Override
    public OrderStatisticsVO statistics() {
        Integer toBeConfirmed = orderMapper.countStatus(Orders.TO_BE_CONFIRMED);
        Integer confirmed = orderMapper.countStatus(Orders.CONFIRMED);
        Integer deliveryInProgress = orderMapper.countStatus(Orders.DELIVERY_IN_PROGRESS);

        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);
        return orderStatisticsVO;
    }

    /**
     * 确认订单。
     * 这个动作通常对应商家后台人工确认或自动接单，把订单状态推进到已确认。
     */
    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = Orders.builder()
                .id(ordersConfirmDTO.getId())
                .status(Orders.CONFIRMED)
                .build();
        orderMapper.update(orders);
    }

    /**
     * 商家拒单。
     * 如果订单已支付，需要先退款，再取消订单并回滚库存，防止货款和库存状态不一致。
     */
    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception {
        Orders ordersDB = orderMapper.getById(ordersRejectionDTO.getId());
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Integer payStatus = ordersDB.getPayStatus();
        if (payStatus == Orders.PAID) {
            String refund = weChatPayUtil.refund(
                    ordersDB.getNumber(),
                    ordersDB.getNumber(),
                    new BigDecimal(0.01),
                    new BigDecimal(0.01));
            log.info("refund result: {}", refund);
        }

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
        restoreOrderStock(ordersDB);
    }

    /**
     * 商家取消订单。
     * 这条链路和拒单类似，核心也是先处理支付状态，再把库存恢复回可售状态。
     */
    @Override
    public void cancel(OrdersCancelDTO ordersCancelDTO) throws Exception {
        Orders ordersDB = orderMapper.getById(ordersCancelDTO.getId());
        Integer payStatus = ordersDB.getPayStatus();
        if (payStatus == Orders.PAID) {
            String refund = weChatPayUtil.refund(
                    ordersDB.getNumber(),
                    ordersDB.getNumber(),
                    new BigDecimal(0.01),
                    new BigDecimal(0.01));
            log.info("refund result: {}", refund);
        }

        Orders orders = new Orders();
        orders.setId(ordersCancelDTO.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
        restoreOrderStock(ordersDB);
    }

    /**
     * 回滚订单占用的库存。
     * 订单取消、拒单或超时时都要走这里，把数据库中的 stock 和 lock_stock 重新恢复。
     */
    private void restoreOrderStock(Orders ordersDB) {
        if (ordersDB == null || ordersDB.getId() == null || ordersDB.getWarehouseId() == null) {
            return;
        }
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(ordersDB.getId());
        if (orderDetails == null || orderDetails.isEmpty()) {
            return;
        }
        for (OrderDetail orderDetail : orderDetails) {
            Long skuId = orderDetail.getDishId() != null ? orderDetail.getDishId() : orderDetail.getSetmealId();
            if (skuId == null) {
                continue;
            }
            int count = orderDetail.getNumber() == null ? 0 : orderDetail.getNumber();
            if (count <= 0) {
                continue;
            }
            int affected = warehouseSkuMapper.restoreStock(ordersDB.getWarehouseId(), skuId, count);
            if (affected <= 0) {
                throw new OrderBusinessException("stock restore failed");
            }
            String seckillStockKey = buildSeckillStockKey(ordersDB.getWarehouseId(), skuId);
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(seckillStockKey))) {
                stringRedisTemplate.opsForValue().increment(seckillStockKey, count);
            }
        }
    }
    
    /**
     * 释放订单锁定库存。
     * 支付成功后不再需要 lock_stock，因此把锁定量从锁库字段里扣掉即可。
     */
    private void releaseOrderLockStock(Orders ordersDB) {
        if (ordersDB == null || ordersDB.getId() == null || ordersDB.getWarehouseId() == null) {
            return;
        }
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(ordersDB.getId());
        if (orderDetails == null || orderDetails.isEmpty()) {
            return;
        }
        for (OrderDetail orderDetail : orderDetails) {
            Long skuId = orderDetail.getDishId() != null ? orderDetail.getDishId() : orderDetail.getSetmealId();
            if (skuId == null) {
                continue;
            }
            int count = orderDetail.getNumber() == null ? 0 : orderDetail.getNumber();
            if (count <= 0) {
                continue;
            }
            int affected = warehouseSkuMapper.releaseLockStock(ordersDB.getWarehouseId(), skuId, count);
            if (affected <= 0) {
                throw new OrderBusinessException("release lock stock failed");
            }
        }
    }

    /**
     * 发货。
     * 这个方法把订单从已确认推进到配送中，属于商家履约阶段。
     */
    @Override
    public void delivery(Long id) {
        Orders ordersDB = orderMapper.getById(id);
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.update(orders);
    }

    /**
     * 完成订单。
     * 配送完成后把订单置为已完成，并记录送达时间，供历史订单和统计使用。
     */
    @Override
    public void complete(Long id) {
        Orders ordersDB = orderMapper.getById(id);
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());
        orders.setStatus(Orders.COMPLETED);
        orders.setDeliveryTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
     * 订单催单。
     * 这个方法通常由前端催单按钮触发，通过 WebSocket 广播给在线终端，提醒商家关注该订单。
     */
    @Override
    public void reminder(Long id) {
        Orders ordersDB = orderMapper.getById(id);
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("type", 2);
        map.put("orderId", id);
        map.put("content", "order number: " + ordersDB.getNumber());
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }
}
