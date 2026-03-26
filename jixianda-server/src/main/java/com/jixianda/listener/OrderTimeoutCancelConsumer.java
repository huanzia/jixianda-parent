package com.jixianda.listener;

import com.jixianda.config.RabbitMQConfiguration;
import com.jixianda.dto.OrderMessageDTO;
import com.jixianda.entity.OrderDetail;
import com.jixianda.entity.Orders;
import com.jixianda.mapper.OrderDetailMapper;
import com.jixianda.mapper.OrderMapper;
import com.jixianda.mapper.WarehouseSkuMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
@Slf4j
@Component
@Transactional(rollbackFor = Exception.class)
/**
 * 订单超时取消消息消费者。
 * 这个类属于 MQ 消费层，负责处理延迟队列中的超时消息，
 * 在订单未支付且状态仍然有效时执行取消，并把库存回滚回可售状态。
 */
public class OrderTimeoutCancelConsumer {

    // 秒杀库存 Redis key 前缀，用于超时取消时同步回补 Redis 库存。
    private static final String SECKILL_STOCK_KEY_PREFIX = "seckill:stock:";

    // 订单主表查询与状态更新入口。
    private final OrderMapper orderMapper;
    // 订单明细查询入口，用于拿到每个商品的回滚数量。
    private final OrderDetailMapper orderDetailMapper;
    // 仓库存回滚入口，负责把 lock_stock 还原到 stock。
    private final WarehouseSkuMapper warehouseSkuMapper;
    // 秒杀 Redis 库存回补入口，保证 DB/Redis 库存一致。
    private final StringRedisTemplate stringRedisTemplate;

    public OrderTimeoutCancelConsumer(OrderMapper orderMapper,
                                      OrderDetailMapper orderDetailMapper,
                                      WarehouseSkuMapper warehouseSkuMapper,
                                      StringRedisTemplate stringRedisTemplate) {
        this.orderMapper = orderMapper;
        this.orderDetailMapper = orderDetailMapper;
        this.warehouseSkuMapper = warehouseSkuMapper;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 消费超时取消消息。
     * 延迟队列到期后会进入这里，先检查订单状态再取消，避免订单在用户刚支付后被错误关闭。
     */
    @RabbitListener(queues = RabbitMQConfiguration.ORDER_TIMEOUT_QUEUE)
    public void processTimeoutOrder(OrderMessageDTO message) {
        // 第一步：消息结构兜底校验，缺少订单号无法定位具体订单，直接忽略。
        if (message == null || message.getOrderNumber() == null) {
            return;
        }

        // 第二步：按订单号查当前真实状态，不能只相信延迟消息里的快照状态。
        Orders order = orderMapper.getByNumber(message.getOrderNumber());
        // 只有仍然处于待支付状态的订单才会被取消，防止重复消费或状态已经推进后再次回滚。
        if (order == null || !Orders.PENDING_PAYMENT.equals(order.getStatus())) {
            return;
        }

        // 第三步：执行超时取消，把订单显式推进到“已取消”，为后续补偿动作提供状态依据。
        Orders toUpdate = new Orders();
        toUpdate.setId(order.getId());
        toUpdate.setStatus(Orders.CANCELLED);
        toUpdate.setCancelReason("超时未支付");
        toUpdate.setCancelTime(LocalDateTime.now());
        orderMapper.update(toUpdate);

        // 超时取消后必须把 lock_stock 回滚回 stock，才能让这部分库存重新可售。
        int rollbackCount = rollbackStock(order);
        log.info("[库存回滚] 订单 {} 超时未支付，已取消！MySQL与Redis库存 (+{}) 已回滚！", message.getOrderNumber(), rollbackCount);
    }

    /**
     * 回滚超时订单占用的库存。
     * 这里同时回滚数据库库存和 Redis 秒杀库存，保证不同库存介质的状态一致。
     */
    private int rollbackStock(Orders order) {
        // 没有仓信息或订单 id 时无法确定回滚目标，直接返回 0 保持幂等安全。
        if (order.getWarehouseId() == null || order.getId() == null) {
            return 0;
        }
        int rollbackCount = 0;

        // 按订单明细逐项回滚，保证多商品订单每一项都能精确恢复。
        java.util.List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(order.getId());
        if (orderDetails == null || orderDetails.isEmpty()) {
            return 0;
        }
        for (OrderDetail orderDetail : orderDetails) {
            // 统一提取 SKU 标识，兼容菜品和套餐两类明细。
            Long skuId = orderDetail.getDishId() != null ? orderDetail.getDishId() : orderDetail.getSetmealId();
            if (skuId == null) {
                continue;
            }
            int number = normalizeNumber(orderDetail.getNumber());

            // 数据库回滚成功后才回补 Redis，避免 Redis 先回补导致跨介质不一致。
            int affected = warehouseSkuMapper.restoreStock(order.getWarehouseId(), skuId, number);
            if (affected <= 0) {
                continue;
            }
            // 如果该 SKU 也在秒杀 Redis 库存里存在，就同步加回去，避免 Redis 和数据库库存不一致。
            String stockKey = SECKILL_STOCK_KEY_PREFIX + order.getWarehouseId() + ":" + skuId;
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(stockKey))) {
                stringRedisTemplate.opsForValue().increment(stockKey, number);
            }
            rollbackCount += number;
        }
        return rollbackCount;
    }

    /**
     * 数量兜底处理。
     * 历史脏数据或空值场景下统一按 1 处理，避免回滚数量出现 0 或负数。
     */
    private int normalizeNumber(Integer number) {
        return (number == null || number < 1) ? 1 : number;
    }
}
