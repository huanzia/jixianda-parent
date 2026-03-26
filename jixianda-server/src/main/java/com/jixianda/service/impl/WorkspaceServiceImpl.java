package com.jixianda.service.impl;

import com.jixianda.constant.StatusConstant;
import com.jixianda.entity.Orders;
import com.jixianda.client.UserClient;
import com.jixianda.mapper.DishMapper;
import com.jixianda.mapper.OrderMapper;
import com.jixianda.mapper.SetmealMapper;
import com.jixianda.result.Result;
import com.jixianda.service.WorkspaceService;
import com.jixianda.vo.BusinessDataVO;
import com.jixianda.vo.DishOverViewVO;
import com.jixianda.vo.OrderOverViewVO;
import com.jixianda.vo.SetmealOverViewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class WorkspaceServiceImpl implements WorkspaceService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserClient userClient;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    @Qualifier("businessTaskExecutor")
    private Executor businessTaskExecutor;

    
    public BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end) {
        CompletableFuture<Integer> totalOrderFuture = CompletableFuture.supplyAsync(() -> {
            Map map = new HashMap();
            map.put("begin", begin);
            map.put("end", end);
            return orderMapper.countByMap(map);
        }, businessTaskExecutor);

        CompletableFuture<Map<String, Object>> turnoverFuture = CompletableFuture.supplyAsync(() -> {
            Map map = new HashMap();
            map.put("begin", begin);
            map.put("end", end);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            Integer validOrderCount = orderMapper.countByMap(map);
            Map<String, Object> result = new HashMap();
            result.put("turnover", turnover == null ? 0.0 : turnover);
            result.put("validOrderCount", validOrderCount == null ? 0 : validOrderCount);
            return result;
        }, businessTaskExecutor);

        CompletableFuture<Integer> newUsersFuture = CompletableFuture.supplyAsync(
                () -> fetchUserCount(begin, end),
                businessTaskExecutor
        );

        CompletableFuture.allOf(totalOrderFuture, turnoverFuture, newUsersFuture).join();

        Integer totalOrderCount = totalOrderFuture.join();
        Map<String, Object> turnoverResult = turnoverFuture.join();
        Double turnover = (Double) turnoverResult.get("turnover");
        Integer validOrderCount = (Integer) turnoverResult.get("validOrderCount");
        Integer newUsers = newUsersFuture.join();

        double unitPrice = 0.0;
        double orderCompletionRate = 0.0;
        if (totalOrderCount != null && totalOrderCount > 0 && validOrderCount != null && validOrderCount > 0) {
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
            unitPrice = turnover / validOrderCount;
        }

        return BusinessDataVO.builder()
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers)
                .build();
    }


    
    public OrderOverViewVO getOrderOverView() {
        Map map = new HashMap();
        map.put("begin", LocalDateTime.now().with(LocalTime.MIN));
        map.put("status", Orders.TO_BE_CONFIRMED);
        Integer waitingOrders = orderMapper.countByMap(map);
        map.put("status", Orders.CONFIRMED);
        Integer deliveredOrders = orderMapper.countByMap(map);
        map.put("status", Orders.COMPLETED);
        Integer completedOrders = orderMapper.countByMap(map);
        map.put("status", Orders.CANCELLED);
        Integer cancelledOrders = orderMapper.countByMap(map);
        map.put("status", null);
        Integer allOrders = orderMapper.countByMap(map);

        return OrderOverViewVO.builder()
                .waitingOrders(waitingOrders)
                .deliveredOrders(deliveredOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .allOrders(allOrders)
                .build();
    }

    
    public DishOverViewVO getDishOverView() {
        Map map = new HashMap();
        map.put("status", StatusConstant.ENABLE);
        Integer sold = dishMapper.countByMap(map);

        map.put("status", StatusConstant.DISABLE);
        Integer discontinued = dishMapper.countByMap(map);

        return DishOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }

    
    public SetmealOverViewVO getSetmealOverView() {
        Map map = new HashMap();
        map.put("status", StatusConstant.ENABLE);
        Integer sold = setmealMapper.countByMap(map);

        map.put("status", StatusConstant.DISABLE);
        Integer discontinued = setmealMapper.countByMap(map);

        return SetmealOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }

    private Integer fetchUserCount(LocalDateTime begin, LocalDateTime end) {
        Result<Integer> result = userClient.countUserByTime(begin, end);
        if (result == null || result.getCode() == null || result.getCode() != 1 || result.getData() == null) {
            return 0;
        }
        return result.getData();
    }
}
