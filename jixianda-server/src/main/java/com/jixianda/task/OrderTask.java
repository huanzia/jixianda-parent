package com.jixianda.task;

import com.jixianda.entity.OrderDetail;
import com.jixianda.entity.Orders;
import com.jixianda.mapper.OrderDetailMapper;
import com.jixianda.mapper.OrderMapper;
import com.jixianda.mapper.WarehouseSkuMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private WarehouseSkuMapper warehouseSkuMapper;

    /**
     * Cancel timeout orders and restore locked warehouse stock.
     */
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 * * * * ? ")
    public void processTimeoutOrder() {
        log.info("Process timeout orders: {}", LocalDateTime.now());

        LocalDateTime timeoutLine = LocalDateTime.now().plusMinutes(-15);
        List<Orders> timeoutOrders = orderMapper.getByStatusAndOrderTimeOut(Orders.PENDING_PAYMENT, timeoutLine);
        if (timeoutOrders == null || timeoutOrders.isEmpty()) {
            return;
        }

        for (Orders order : timeoutOrders) {
            order.setStatus(Orders.CANCELLED);
            order.setCancelReason("order timeout, auto cancel");
            order.setCancelTime(LocalDateTime.now());
            orderMapper.update(order);
            restoreOrderStock(order);
        }
    }

    /**
     * Auto complete long-running delivery orders.
     */
    @Scheduled(cron = "0 0 1 * * ? ")
    public void processDeliveryOrder() {
        log.info("Process timeout delivery orders: {}", LocalDateTime.now());
        LocalDateTime timeoutLine = LocalDateTime.now().plusMinutes(-60);
        List<Orders> timeoutOrders = orderMapper.getByStatusAndOrderTimeOut(Orders.DELIVERY_IN_PROGRESS, timeoutLine);
        if (timeoutOrders == null || timeoutOrders.isEmpty()) {
            return;
        }

        for (Orders order : timeoutOrders) {
            order.setStatus(Orders.COMPLETED);
            order.setCancelReason("delivery timeout, auto complete");
            order.setCancelTime(LocalDateTime.now());
            orderMapper.update(order);
        }
    }

    private void restoreOrderStock(Orders order) {
        if (order == null || order.getId() == null || order.getWarehouseId() == null) {
            return;
        }

        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(order.getId());
        if (orderDetails == null || orderDetails.isEmpty()) {
            return;
        }

        for (OrderDetail detail : orderDetails) {
            Long skuId = detail.getDishId() != null ? detail.getDishId() : detail.getSetmealId();
            if (skuId == null) {
                continue;
            }
            Integer count = detail.getNumber();
            if (count == null || count <= 0) {
                continue;
            }
            int affected = warehouseSkuMapper.restoreStock(order.getWarehouseId(), skuId, count);
            if (affected <= 0) {
                throw new IllegalStateException("restore stock failed for orderId=" + order.getId() + ", skuId=" + skuId);
            }
        }
    }
}
