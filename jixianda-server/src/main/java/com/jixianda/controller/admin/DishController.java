package com.jixianda.controller.admin;

import com.jixianda.annotation.DoubleCacheEvict;
import com.jixianda.dto.DishDTO;
import com.jixianda.dto.DishPageQueryDTO;
import com.jixianda.entity.Dish;
import com.jixianda.result.PageResult;
import com.jixianda.result.Result;
import com.jixianda.service.DishService;
import com.jixianda.vo.DishVO;
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

@Api(tags = "商品管理接口")
@RestController
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @ApiOperation("新增商品")
    @PostMapping
    @DoubleCacheEvict(cacheName = "dishCache", key = "dish_*")
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("Create dish: {}", dishDTO);
        dishService.saveWithFlaver(dishDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("商品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("Dish page query: {}", dishPageQueryDTO);
        PageResult pageResult = dishService.PageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @ApiOperation("删除商品")
    @DoubleCacheEvict(cacheName = "dishCache", key = "dish_*")
    public Result deleteBatch(@RequestParam List<Long> ids) {
        log.info("Delete dishes: {}", ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据ID查询商品")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("Get dish by id: {}", id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    @ApiOperation("更新商品")
    @PutMapping
    @DoubleCacheEvict(cacheName = "dishCache", key = "dish_*")
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("Update dish: {}", dishDTO);
        dishService.updateWithFlavor(dishDTO);
        return Result.success();
    }

    @ApiOperation("禁用/启用 商品")
    @PostMapping("/status/{status}")
    @DoubleCacheEvict(cacheName = "dishCache", key = "dish_*")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("Enable/disable dish, status={}, id={}", status, id);
        dishService.startOrStop(status, id);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("根据分类查询商品")
    public Result<List<Dish>> list(Long categoryId) {
        log.info("List dishes by category, categoryId={}", categoryId);
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }
}
