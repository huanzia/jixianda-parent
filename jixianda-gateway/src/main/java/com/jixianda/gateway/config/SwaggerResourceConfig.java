package com.jixianda.gateway.config;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;

@Component
@Primary
/**
 * 网关层的 Swagger 资源聚合配置。
 * 这个类负责把下游服务各自暴露的 API 文档地址统一注册到网关里，
 * 让前端或 Swagger UI 只通过网关入口就能看到多个服务的接口文档。
 */
public class SwaggerResourceConfig implements SwaggerResourcesProvider {

    @Override
    /**
     * 返回网关聚合后的 Swagger 资源列表。
     * 这里把 user 服务和 server 服务的文档路径统一暴露出去，
     * 方便 Swagger UI 通过网关转发后分别读取各服务的接口定义。
     */
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>();
        // 通过网关把两个业务服务的文档入口汇总起来，避免直接感知各服务地址。
        resources.add(swaggerResource("jixianda-server", "/server-docs/v2/api-docs", "2.0"));
        resources.add(swaggerResource("jixianda-user", "/user-docs/v2/api-docs", "2.0"));
        return resources;
    }

    /**
     * 构造单个 Swagger 资源描述。
     * 这个方法只是把名称、访问路径和版本封装成统一对象，供网关文档聚合使用。
     */
    private SwaggerResource swaggerResource(String name, String location, String version) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion(version);
        return swaggerResource;
    }
}
