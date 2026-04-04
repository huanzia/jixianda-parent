package com.jixianda.controller.admin;

import com.jixianda.dto.WarehousePageQueryDTO;
import com.jixianda.entity.Warehouse;
import com.jixianda.result.PageResult;
import com.jixianda.result.Result;
import com.jixianda.service.WarehouseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/warehouse")
@Api(tags = "货仓管理接口")
@Slf4j
public class WarehouseController {

    @Autowired
    private WarehouseService warehouseService;

    @PostMapping
    @ApiOperation("新增货仓")
    public Result<String> save(@RequestBody Warehouse warehouse) {
        log.info("新增货仓: {}", warehouse);
        warehouseService.save(warehouse);
        return Result.success();
    }

    @PutMapping
    @ApiOperation("修改货仓")
    public Result<String> update(@RequestBody Warehouse warehouse) {
        log.info("修改货仓: {}", warehouse);
        warehouseService.update(warehouse);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("删除货仓")
    public Result<String> delete(@RequestParam List<Long> ids) {
        log.info("删除货仓: {}", ids);
        warehouseService.deleteBatch(ids);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("货仓分页查询")
    public Result<PageResult> page(WarehousePageQueryDTO warehousePageQueryDTO) {
        log.info("货仓分页查询: {}", warehousePageQueryDTO);
        PageResult pageResult = warehouseService.pageQuery(warehousePageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    @ApiOperation("根据ID查询货仓")
    public Result<Warehouse> getById(@PathVariable Long id) {
        log.info("根据ID查询货仓: id={}", id);
        return Result.success(warehouseService.getById(id));
    }

    @GetMapping("/list")
    @ApiOperation("查询货仓列表")
    public Result<List<Warehouse>> list(@RequestParam(required = false) Integer status) {
        log.info("查询货仓列表: status={}", status);
        return Result.success(warehouseService.list(status));
    }

    @PostMapping("/status/{status}")
    @ApiOperation("启用/禁用货仓")
    public Result<String> startOrStop(@PathVariable Integer status, @RequestParam Long id) {
        log.info("启用/禁用货仓: status={}, id={}", status, id);
        warehouseService.startOrStop(status, id);
        return Result.success();
    }
}
