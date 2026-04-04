package com.jixianda.controller.user;

import com.jixianda.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "店铺相关接口")
@RestController("userShopController")
@RequestMapping("/user/shop")
public class ShopController {

    public static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation("设置店铺营业状态")
    @PutMapping("/{status}")
    public Result setStatus(@PathVariable Integer status) {
        redisTemplate.opsForValue().set(KEY, status);
        return Result.success();
    }

    @GetMapping("/status")
    @ApiOperation("获取店铺营业状态")
    public Result<Integer> getStatus(Integer status) {
        // 多仓模型下强制营业，避免旧单店“打烊”逻辑阻断流程
        return Result.success(1);
    }
}
