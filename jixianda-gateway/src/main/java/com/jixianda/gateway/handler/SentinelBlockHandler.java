package com.jixianda.gateway.handler;

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Configuration
/**
 * 网关层的 Sentinel 限流拦截处理配置。
 * 这个类属于网关防护层，负责在流量超过阈值时统一返回结果，
 * 并且对 Swagger 文档相关请求做特殊放行，避免限流影响接口调试和文档查看。
 */
public class SentinelBlockHandler {

    @PostConstruct
    /**
     * 初始化网关限流规则和被限流后的统一响应逻辑。
     * 这里在应用启动后完成规则加载，确保 Sentinel 直接接管网关入口流量。
     */
    public void initSentinelGatewayHandler() {
        // 先加载网关限流规则，再注册被拦截后的回调，保证限流生效时能返回统一结果。
        initGatewayRules();
        GatewayCallbackManager.setBlockHandler((exchange, t) -> {
            String path = exchange.getRequest().getURI().getPath();
            // 文档页和 API 文档接口需要单独放行，避免限流时 Swagger 无法正常展示接口信息。
            if (isDocPath(path)) {
                Map<String, Object> bypassBody = new HashMap<String, Object>();
                bypassBody.put("code", 200);
                bypassBody.put("msg", "文档路径限流已放行");
                return ServerResponse.status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(bypassBody));
            }

            // 普通业务请求被限流时，统一返回 429，提示调用方稍后重试。
            Map<String, Object> body = new HashMap<String, Object>();
            body.put("code", 429);
            body.put("msg", "系统繁忙，请稍后再试");
            return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(body));
        });
    }

    /**
     * 初始化网关限流规则。
     * 这里按路由名配置阈值，表示某个网关路由在单位时间内允许通过的请求上限。
     */
    private void initGatewayRules() {
        Set<GatewayFlowRule> rules = new HashSet<GatewayFlowRule>();
        rules.add(new GatewayFlowRule("jixianda-server-route")
                .setCount(2000)
                .setIntervalSec(1));
        GatewayRuleManager.loadRules(rules);
    }

    /**
     * 判断当前请求是否属于文档相关路径。
     * Swagger 文档和静态资源依赖这些路径，单独识别后可以在限流时保留可用性。
     */
    private boolean isDocPath(String path) {
        if (path != null && path.contains("api-docs")) {
            return true;
        }
        return "/doc.html".equals(path)
                || "/favicon.ico".equals(path)
                || path.startsWith("/webjars/")
                || path.startsWith("/swagger-resources/")
                || path.startsWith("/v2/api-docs")
                || path.endsWith("/v2/api-docs");
    }
}
