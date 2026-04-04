package com.jixianda.controller.admin;

import com.jixianda.context.BaseContext;
import com.jixianda.dto.OrdersCancelDTO;
import com.jixianda.dto.OrdersConfirmDTO;
import com.jixianda.dto.OrdersPageQueryDTO;
import com.jixianda.dto.OrdersRejectionDTO;
import com.jixianda.result.PageResult;
import com.jixianda.result.Result;
import com.jixianda.service.EmployeeService;
import com.jixianda.service.OrderService;
import com.jixianda.vo.OrderStatisticsVO;
import com.jixianda.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Slf4j
@Api(tags = "??????")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/conditionSearch")
    @ApiOperation("????")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        Long employeeId = BaseContext.getCurrentId();
        if (employeeId != null) {
            Long warehouseId = employeeService.getWarehouseIdById(employeeId);
            if (warehouseId != null && warehouseId != 0L) {
                ordersPageQueryDTO.setWarehouseId(warehouseId);
            }
        }
        log.info("????: {}", ordersPageQueryDTO);
        PageResult pageResult = orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/statistics")
    @ApiOperation("??????????")
    public Result<OrderStatisticsVO> statistics() {
        log.info("??????????");
        OrderStatisticsVO orderStatisticsVO = orderService.statistics();
        return Result.success(orderStatisticsVO);
    }

    @GetMapping("/details/{id}")
    @ApiOperation("??????")
    public Result<OrderVO> details(@PathVariable("id") Long id) {
        log.info("??????: id={}", id);
        OrderVO orderVO = orderService.details(id);
        return Result.success(orderVO);
    }

    @PutMapping("/confirm")
    @ApiOperation("??")
    public Result<String> confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        log.info("??: {}", ordersConfirmDTO);
        orderService.confirm(ordersConfirmDTO);
        return Result.success();
    }

    @PutMapping("/rejection")
    @ApiOperation("??")
    public Result<String> rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO) throws Exception {
        log.info("??: {}", ordersRejectionDTO);
        orderService.rejection(ordersRejectionDTO);
        return Result.success();
    }

    @PutMapping("/cancel")
    @ApiOperation("????")
    public Result<String> cancel(@RequestBody OrdersCancelDTO ordersCancelDTO) throws Exception {
        log.info("????: {}", ordersCancelDTO);
        orderService.cancel(ordersCancelDTO);
        return Result.success();
    }

    @PutMapping("/delivery/{id}")
    @ApiOperation("????")
    public Result<String> delivery(@PathVariable("id") Long id) {
        log.info("????: id={}", id);
        orderService.delivery(id);
        return Result.success();
    }

    @PutMapping("/complete/{id}")
    @ApiOperation("????")
    public Result<String> complete(@PathVariable("id") Long id) {
        log.info("????: id={}", id);
        orderService.complete(id);
        return Result.success();
    }
}
