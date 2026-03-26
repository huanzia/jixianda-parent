package com.jixianda.controller.user;

import com.jixianda.dto.OrdersPaymentDTO;
import com.jixianda.dto.OrdersSubmitDTO;
import com.jixianda.result.PageResult;
import com.jixianda.result.Result;
import com.jixianda.service.OrderService;
import com.jixianda.vo.OrderPaymentVO;
import com.jixianda.vo.OrderSubmitVO;
import com.jixianda.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Api(tags = "C端订单接口")
public class OrderController {
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    @ApiOperation(value = "普通下单", notes = "需要token(authentication)。warehouseId必传，必须与购物车仓库一致")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        log.info("用户下单: {}", ordersSubmitDTO);
        if (ordersSubmitDTO == null || ordersSubmitDTO.getWarehouseId() == null) {
            return Result.error("warehouseId 不能为空");
        }
        OrderSubmitVO orderSubmitVO = orderService.orderSubmit(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    @PostMapping("/seckill")
    @ApiOperation(value = "秒杀下单", notes = "需要token(authentication)。warehouseId、dishId、addressBookId必传")
    public Result<OrderSubmitVO> seckill(@RequestBody Map<String, Object> params) {
        Number warehouseIdNumber = (Number) params.get("warehouseId");
        Number dishIdNumber = (Number) params.get("dishId");
        Number numberNumber = (Number) params.get("number");
        Number addressBookIdNumber = (Number) params.get("addressBookId");
        Long warehouseId = warehouseIdNumber == null ? null : warehouseIdNumber.longValue();
        Long dishId = dishIdNumber == null ? null : dishIdNumber.longValue();
        Integer number = numberNumber == null ? 1 : numberNumber.intValue();
        Long addressBookId = addressBookIdNumber == null ? null : addressBookIdNumber.longValue();
        OrderSubmitVO orderSubmitVO = orderService.seckill(warehouseId, dishId, number, addressBookId);
        return Result.success(orderSubmitVO);
    }

    @PutMapping("/payment")
    @ApiOperation(value = "订单支付", notes = "需要token(authentication)")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付: {}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单: {}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    @GetMapping("/historyOrders")
    @ApiOperation(value = "历史订单查询", notes = "需要token(authentication)")
    public Result<PageResult> page(int page, int pageSize, Integer status) {
        PageResult pageResult = orderService.pageQuery4User(page, pageSize, status);
        return Result.success(pageResult);
    }

    @GetMapping("/orderDetail/{id}")
    @ApiOperation(value = "订单详情", notes = "需要token(authentication)")
    public Result<OrderVO> details(@PathVariable("id") Long id) {
        OrderVO orderVO = orderService.details(id);
        return Result.success(orderVO);
    }

    @PutMapping("/cancel/{id}")
    @ApiOperation(value = "取消订单", notes = "需要token(authentication)")
    public Result cancel(@PathVariable("id") Long id) throws Exception {
        orderService.userCancelById(id);
        return Result.success();
    }

    @PostMapping("/repetition/{id}")
    @ApiOperation(value = "再来一单", notes = "需要token(authentication)")
    public Result repetition(@PathVariable Long id) {
        orderService.repetition(id);
        return Result.success();
    }

    @GetMapping("/reminder/{id}")
    @ApiOperation(value = "催单", notes = "需要token(authentication)")
    public Result reminder(@PathVariable("id") Long id) {
        log.info("用户催单");
        orderService.reminder(id);
        return Result.success();
    }
}
