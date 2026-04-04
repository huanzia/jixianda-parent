package com.jixianda.controller.user;

import com.jixianda.constant.JwtClaimsConstant;
import com.jixianda.dto.UserLoginDTO;
import com.jixianda.entity.User;
import com.jixianda.properties.JwtProperties;
import com.jixianda.result.Result;
import com.jixianda.service.UserService;
import com.jixianda.utils.JwtUtil;
import com.jixianda.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.time.LocalDateTime;
import java.util.HashMap;

@RestController
@RequestMapping("/user/user")
@Slf4j
@Api(tags = "C端用户接口")
/**
 * 这个类属于接口层，负责处理用户登录、基础信息查询和统计类请求，
 * 并与网关配合接收上游转发过来的用户链路访问。
 */
public class UserController {

    @Autowired
    // JWT 配置用于生成登录 token，保证与网关鉴权读取的密钥和头名称一致。
    private JwtProperties jwtProperties;
    @Autowired
    // 用户业务由 service 层处理，控制器只负责参数接入和响应封装。
    private UserService userService;


    /**
     * 用户微信登录入口。
     * 前端登录页或小程序登录链路会调用这里，服务端根据 code 完成用户登录并返回 token。
     *
     * @param userLoginDTO 登录入参，核心是微信 code
     * @return 包含用户基础信息和 token 的登录结果
     */
    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("用户微信登录 code={}", userLoginDTO.getCode());
        User user = userService.wxlogin(userLoginDTO);

        HashMap<String, Object> claims = new HashMap<String, Object>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(token)
                .build();
        return Result.success(userLoginVO);
    }


    /**
     * 按用户 id 查询用户信息。
     *
     * @param id 用户主键
     * @return 用户对象
     */
    @GetMapping("/{id}")
    @ApiIgnore
    public Result<User> getUserById(@PathVariable("id") Long id) {
        return Result.success(userService.getById(id));
    }


    /**
     * 按时间范围统计用户数量。
     *
     * @param begin 起始时间，可为空
     * @param end 结束时间，可为空
     * @return 统计结果
     */
    @GetMapping("/count")
    @ApiIgnore
    public Result<Integer> countUserByTime(
            @RequestParam(value = "begin", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime begin,
            @RequestParam(value = "end", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end) {
        return Result.success(userService.countByTime(begin, end));
    }
}
