package com.jixianda.gateway.filter;

import com.jixianda.constant.JwtClaimsConstant;
import com.jixianda.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
/**
 * 网关层的全局鉴权过滤器。
 * 这个类属于网关过滤层，负责在请求进入下游服务之前统一做 token 校验，
 * 并把解析出的用户身份信息透传给后面的业务服务，减少各服务重复鉴权。
 */
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    // 请求头里携带的用户令牌字段名，网关从这里读取登录态信息。
    private static final String USER_TOKEN_HEADER = "authentication";
    // JWT 校验使用的密钥，必须和发 token 的一端保持一致。
    private static final String USER_SECRET_KEY = "itheima";
    // 用于判断当前路径是否在白名单里的通配符匹配器。
    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    // 这里列出所有不需要登录也能访问的路径，避免登录、文档和静态资源被误拦截。
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/user/user/login",
            "/user/warehouse/nearest",
            "/admin/employee/login",
            "/doc.html",
            "/webjars/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/v2/api-docs/**",
            "/**/v2/api-docs",
            "/**/v2/api-docs/**",
            "/server-docs/**",
            "/user-docs/**",
            "/swagger-ui.html",
            "/favicon.ico"
    );

    @Override
    /**
     * 执行网关鉴权逻辑。
     * 先判断当前请求是否属于白名单，白名单请求直接放行；
     * 非白名单请求必须携带 token，解析成功后把 userId 写入请求头再转发给下游服务。
     */
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        // 白名单路径直接放行，保证登录、文档和静态资源可以正常访问。
        if (isWhitePath(path)) {
            return chain.filter(exchange);
        }

        // token 由前端放在 authentication 请求头里，网关统一从这里读取。
        String token = exchange.getRequest().getHeaders().getFirst(USER_TOKEN_HEADER);
        if (!StringUtils.hasText(token)) {
            // 没有 token 说明请求没有登录态，直接返回 401。
            return unauthorized(exchange);
        }

        try {
            // 解析 token，拿到其中的用户信息，后续服务可以直接使用。
            Claims claims = JwtUtil.parseJWT(USER_SECRET_KEY, token);
            Object userId = claims.get(JwtClaimsConstant.USER_ID);
            if (userId == null) {
                // token 虽然存在，但没有用户标识，视为无效登录态。
                return unauthorized(exchange);
            }

            // 把解析出的 userId 继续向下游传递，避免业务服务重复解析 JWT。
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("userId", String.valueOf(userId))
                    .build();
            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (Exception e) {
            log.warn("gateway jwt parse failed, path={}, message={}", path, e.getMessage());
            return unauthorized(exchange);
        }
    }

    /**
     * 判断当前路径是否命中白名单。
     * 这里支持通配符匹配，方便把文档页、静态资源和登录接口统一放行。
     */
    private boolean isWhitePath(String path) {
        for (String whitePath : WHITE_LIST) {
            if (ANT_PATH_MATCHER.match(whitePath, path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 返回未授权响应。
     * 统一使用 401，告诉调用方当前请求缺少合法登录态，需要先完成认证。
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
