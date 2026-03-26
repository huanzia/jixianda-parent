package com.jixianda.controller.user;

import com.jixianda.dto.ShoppingCartDTO;
import com.jixianda.entity.ShoppingCart;
import com.jixianda.result.Result;
import com.jixianda.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user/shoppingCart")
@Api(tags = "C端购物车接口")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @ApiOperation(value = "添加购物车", notes = "需要token(authentication)。请求体需带 warehouseId，保持与当前选仓一致")
    @PostMapping("/add")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("购物车商品信息：{}", shoppingCartDTO);
        shoppingCartService.addShoppingCart(shoppingCartDTO);
        return Result.success();
    }

    @ApiOperation(value = "查询购物车", notes = "需要token(authentication)。返回当前登录用户在当前仓库的购物车数据")
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list(Long userId){
        log.info("查询购物车：{}", userId);
        List<ShoppingCart> shoppingCart = shoppingCartService.list(userId);
        return Result.success(shoppingCart);
    }

    @ApiOperation(value = "购物车商品减一/删除", notes = "需要token(authentication)。请求体需带 warehouseId，防止跨仓误操作")
    @PostMapping("/sub")
    public Result delete(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("删除购物车：{}", shoppingCartDTO);
        shoppingCartService.subshoppingCart(shoppingCartDTO);
        return Result.success();
    }

    @ApiOperation(value = "清空购物车", notes = "需要token(authentication)")
    @DeleteMapping("/clean")
    public Result cleanShoppingCart(Long userId){
        log.info("清空购物车 {}", userId);
        shoppingCartService.cleanShoppingCart(userId);
        return Result.success();
    }
}
