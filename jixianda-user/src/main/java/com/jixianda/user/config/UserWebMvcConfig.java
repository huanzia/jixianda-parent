package com.jixianda.user.config;

import com.jixianda.user.interceptor.JwtTokenUserInterceptor;
import com.jixianda.user.interceptor.UserContextInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
/**
 * user 服务的 Spring MVC 配置。
 * 这个类属于配置层，负责把拦截器挂到请求进入 controller 之前的链路上，
 * 这样 controller 里的业务方法就能直接读取当前用户上下文，而不用自己反复解析 token。
 */
public class UserWebMvcConfig implements WebMvcConfigurer {

    @Autowired
    // 负责把请求里的 userId 放到线程上下文中，供后续业务层读取。
    private UserContextInterceptor userContextInterceptor;
    @Autowired
    // 负责校验用户 token，并在通过时提前写入当前用户上下文。
    private JwtTokenUserInterceptor jwtTokenUserInterceptor;

    @Override
    /**
     * 注册 MVC 拦截器。
     * 请求先经过 interceptor，再进入 controller，这样 user 服务可以在统一入口处完成鉴权和上下文传递。
     */
    public void addInterceptors(InterceptorRegistry registry) {
        // 地址簿接口需要先完成 token 校验，确保用户只能操作自己的地址。
        registry.addInterceptor(jwtTokenUserInterceptor)
                .addPathPatterns("/user/addressBook/**");
        // 其他 user 接口统一挂上下文拦截器，让业务层可以直接从 BaseContext 获取当前用户。
        registry.addInterceptor(userContextInterceptor)
                .addPathPatterns("/user/**")
                .excludePathPatterns("/user/user/login");
    }
}
