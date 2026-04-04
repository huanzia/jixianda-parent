package com.jixianda.user.interceptor;

import com.jixianda.context.BaseContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
/**
 * 用户上下文拦截器。
 * 这个类属于 MVC 拦截层，负责把请求头里的 userId 放到 BaseContext，
 * 让后续 controller、service 和 mapper 相关流程都能直接拿到当前登录用户身份。
 */
public class UserContextInterceptor implements HandlerInterceptor {

    @Override
    /**
     * 在请求进入 controller 前写入用户上下文。
     * 这一步把网关或前置拦截器传下来的 userId 变成线程级上下文，后续业务方法就不用重复解析请求头。
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userId = request.getHeader("userId");
        if (userId != null && !userId.trim().isEmpty()) {
            BaseContext.setCurrentId(Long.valueOf(userId));
        }
        return true;
    }

    @Override
    /**
     * 请求结束后清理上下文。
     * 这样可以防止线程复用时把上一个请求的用户身份带到下一个请求里。
     */
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        BaseContext.removeCurrentId();
    }
}
