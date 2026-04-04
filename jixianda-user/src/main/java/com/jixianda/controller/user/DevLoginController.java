package com.jixianda.controller.user;

import com.jixianda.constant.JwtClaimsConstant;
import com.jixianda.entity.User;
import com.jixianda.properties.JwtProperties;
import com.jixianda.result.Result;
import com.jixianda.service.UserService;
import com.jixianda.utils.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/user")
@Profile("dev")
@Slf4j
@Api(tags = "C端用户接口")
/**
 * 开发环境登录控制器。
 * 这个类属于接口层，只在 dev 环境启用，目的是绕过真实微信登录链路，
 * 让前端联调、接口调试和本地开发时能够快速拿到可用 token，再通过网关进入 user 服务。
 */
public class DevLoginController {

    private final JwtProperties jwtProperties;
    // 直接复用用户服务读取测试用户，避免在开发环境手工构造登录态。
    private final UserService userService;

    /**
     * 注入 JWT 配置和用户服务。
     * 前者用于生成和线上一致格式的 token，后者用于获取一个真实可用的测试用户。
     */
    public DevLoginController(JwtProperties jwtProperties, UserService userService) {
        this.jwtProperties = jwtProperties;
        this.userService = userService;
    }

    @PostMapping("/devLogin")
    @ApiOperation(
            value = "开发环境快捷登录",
            notes = "仅开发环境可用。可选入参为 userId、phone；都不传时默认选择首个可用用户。返回的 token 需放到 authentication 请求头。"
    )
    /**
     * 开发环境模拟登录。
     * 前端或测试人员可以按 userId、手机号指定用户；如果都不传，就自动取一个可用用户。
     *
     * @param request 可选入参，支持 userId 和 phone，用于定位测试用户
     * @return 包含 token、用户 id 和基础身份信息的结果，供前端直接写入 authentication 请求头
     */
    public Result<Map<String, Object>> devLogin(@RequestBody(required = false) Map<String, Object> request) {
        Long userId = parseLong(request == null ? null : request.get("userId"));
        String phone = request == null ? null : trimToNull(String.valueOf(request.get("phone")));

        User user = null;
        if (userId != null) {
            user = userService.getById(userId);
        }
        if (user == null && phone != null) {
            user = userService.getByPhone(phone);
        }
        if (user == null) {
            // 兜底拿一个可用用户，保证本地联调不需要先准备特定账号。
            user = userService.getAnyUser();
        }
        if (user == null || user.getId() == null) {
            return Result.error("No available test user found");
        }

        HashMap<String, Object> claims = new HashMap<String, Object>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("id", user.getId());
        data.put("userId", user.getId());
        data.put("name", user.getName());
        data.put("openid", user.getOpenid());
        data.put("tokenHeader", "authentication");
        return Result.success(data);
    }

    /**
     * 把前端传来的 userId 参数转成 Long。
     * 这里兼容数字、字符串以及空值，避免开发接口因为参数格式不同而失败。
     */
    private Long parseLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        String text = trimToNull(String.valueOf(value));
        if (text == null) {
            return null;
        }
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException ex) {
            log.warn("Invalid devLogin userId: {}", value);
            return null;
        }
    }

    /**
     * 把字符串清洗成可用值。
     * 主要用于把空串和字符串 "null" 当成真正的空值处理，减少开发联调时的噪音参数。
     */
    private String trimToNull(String value) {
        if (!StringUtils.hasText(value) || "null".equalsIgnoreCase(value)) {
            return null;
        }
        return value.trim();
    }
}
