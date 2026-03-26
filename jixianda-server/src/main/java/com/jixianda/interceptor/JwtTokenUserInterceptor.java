package com.jixianda.interceptor;

import com.jixianda.constant.JwtClaimsConstant;
import com.jixianda.context.BaseContext;
import com.jixianda.properties.JwtProperties;
import com.jixianda.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {

    private static final String DEFAULT_USER_TOKEN_HEADER = "authentication";

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        String headerName = jwtProperties == null ? null : jwtProperties.getUserTokenName();
        if (headerName == null || headerName.trim().isEmpty()) {
            headerName = DEFAULT_USER_TOKEN_HEADER;
        }

        String token = request.getHeader(headerName);
        if (token == null || token.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        String secretKey = jwtProperties == null ? null : jwtProperties.getUserSecretKey();
        if (secretKey == null || secretKey.trim().isEmpty()) {
            log.error("User JWT secret key is missing. Check jwt properties prefix and config.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        try {
            Claims claims = JwtUtil.parseJWT(secretKey, token);
            Object userIdValue = claims.get(JwtClaimsConstant.USER_ID);
            if (userIdValue == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
            Long userId = Long.valueOf(String.valueOf(userIdValue));
            BaseContext.setCurrentId(userId);
            return true;
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }
}
