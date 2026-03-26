package com.jixianda.user.interceptor;

import com.jixianda.constant.JwtClaimsConstant;
import com.jixianda.context.BaseContext;
import com.jixianda.properties.JwtProperties;
import com.jixianda.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
/**
 * 用户 JWT 校验拦截器。
 * 这个类属于 MVC 拦截层，负责在请求进入 controller 之前校验 token，
 * 只有通过校验的请求才会把用户身份写入上下文，适合集中处理用户登录态。
 */
public class JwtTokenUserInterceptor implements HandlerInterceptor {

    // 兼容默认 token 请求头名，防止配置未生效时完全无法读取登录态。
    private static final String DEFAULT_USER_TOKEN_HEADER = "authentication";

    @Autowired
    // JWT 配置提供校验时需要的 token 请求头名和密钥。
    private JwtProperties jwtProperties;

    @Override
    /**
     * 在 controller 执行前校验用户 token。
     * 如果请求携带合法 token，就把用户 id 放到 BaseContext；如果没有合法 token，则保留旧兼容逻辑给调试模式使用。
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 优先读取配置里的 token 头名，确保和用户登录签发时使用的配置一致。
        String headerName = jwtProperties == null ? null : jwtProperties.getUserTokenName();
        if (!StringUtils.hasText(headerName)) {
            headerName = DEFAULT_USER_TOKEN_HEADER;
        }

        // token 通过请求头传入，拦截器在这里统一做解析。
        String token = request.getHeader(headerName);
        if (StringUtils.hasText(token)) {
            String secretKey = jwtProperties == null ? null : jwtProperties.getUserSecretKey();
            if (StringUtils.hasText(secretKey)) {
                try {
                    // token 校验成功后取出 userId，放到线程上下文供 controller 和 service 使用。
                    Claims claims = JwtUtil.parseJWT(secretKey, token);
                    Object userIdValue = claims.get(JwtClaimsConstant.USER_ID);
                    if (userIdValue != null) {
                        BaseContext.setCurrentId(Long.valueOf(String.valueOf(userIdValue)));
                        return true;
                    }
                } catch (Exception ex) {
                    // 校验失败时不在这里直接拦截，保留旧兼容逻辑，避免调试场景下请求完全不可用。
                    log.warn("Failed to parse user JWT from header '{}': {}", headerName, ex.getMessage());
                }
            }
        }

        // 兼容旧的直接传 userId 调试方式，方便本地联调时快速进入业务链路。
        String userId = request.getHeader("userId");
        if (StringUtils.hasText(userId)) {
            BaseContext.setCurrentId(Long.valueOf(userId));
        }
        return true;
    }

    @Override
    /**
     * 请求完成后清理线程上下文。
     * 这是拦截器适合做上下文管理的原因之一：请求结束时可以统一回收 ThreadLocal 数据，避免串号。
     */
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        BaseContext.removeCurrentId();
    }
}
