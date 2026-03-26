package com.jixianda.controller.admin;

import com.jixianda.context.BaseContext;
import com.jixianda.dto.StockDTO;
import com.jixianda.result.PageResult;
import com.jixianda.result.Result;
import com.jixianda.service.EmployeeService;
import com.jixianda.service.StockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/stock")
@Api(tags = "Stock Admin API")
@Slf4j
public class StockController {

    @Autowired
    private StockService stockService;
    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/page")
    @ApiOperation("仓库分页查询")
    public Result<PageResult> page(@RequestParam(required = false) Long warehouseId,
                                   @RequestParam(required = false) String name,
                                   @RequestParam Integer page,
                                   @RequestParam Integer pageSize) {
        Long employeeId = BaseContext.getCurrentId();
        if (employeeId != null) {
            Long currentWarehouseId = employeeService.getWarehouseIdById(employeeId);
            if (currentWarehouseId != null && currentWarehouseId != 0L) {
                warehouseId = currentWarehouseId;
            }
        }

        if (warehouseId == null) {
            return Result.error("warehouseId is required");
        }

        log.info("Stock page query: warehouseId={}, name={}, page={}, pageSize={}", warehouseId, name, page, pageSize);
        PageResult pageResult = stockService.pageQuery(warehouseId, name, page, pageSize);
        return Result.success(pageResult);
    }

    @PutMapping
    @ApiOperation("更新仓库")
    public Result<String> update(@RequestBody StockDTO stockDTO) {
        log.info("Update stock: {}", stockDTO);
        stockService.updateStock(stockDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("更新仓库状态")
    public Result<String> updateStatus(@PathVariable Integer status,
                                       @RequestParam(required = false) Long id,
                                       @RequestParam(required = false) Long warehouseId,
                                       @RequestParam(required = false) Long dishId) {
        StockDTO stockDTO = new StockDTO();
        stockDTO.setStatus(status);
        stockDTO.setId(id);
        stockDTO.setWarehouseId(warehouseId);
        stockDTO.setDishId(dishId);
        log.info("Update stock sale status: {}", stockDTO);
        stockService.updateStatus(stockDTO);
        return Result.success();
    }
}
