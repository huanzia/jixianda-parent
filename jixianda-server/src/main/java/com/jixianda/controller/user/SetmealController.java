package com.jixianda.controller.user;

import com.jixianda.result.Result;
import com.jixianda.service.SetmealService;
import com.jixianda.vo.DishItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController("userSetmealController")
@RequestMapping("/user/setmeal")
@Api(tags = "C端套餐接口")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @GetMapping("/dish/{id}")
    @ApiOperation("根据套餐ID查询包含商品")
    public Result<List<DishItemVO>> dishList(@PathVariable("id") Long id) {
        List<DishItemVO> list = setmealService.getDishItemById(id);
        return Result.success(list);
    }

    @GetMapping("/list")
    @ApiOperation("根据分类查询套餐列表")
    @Cacheable(cacheNames = "setmealCache", key = "#categoryId")
    public Result<List<com.jixianda.entity.Setmeal>> list(Long categoryId) {
        // Retail mode: hide setmeal list for user side.
        log.info("setmeal list disabled in retail mode, categoryId={}", categoryId);
        return Result.success(Collections.emptyList());
    }
}
