package com.jixianda.user.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ParameterType;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
@EnableKnife4j
/**
 * user 服务的 Swagger 配置。
 * 这个类属于配置层，负责把 user 服务自己的接口文档单独暴露出来，
 * 再配合网关的 Swagger 聚合配置，让前端可以通过网关统一查看用户、地址簿和仓库相关接口。
 */
public class SwaggerConfiguration {

    @Bean
    /**
     * 构建 user 服务自己的 Swagger 文档分组。
     * 这里指定扫描范围、全局请求头和文档元信息，方便前端和测试人员直接查看 user 模块接口。
     */
    public Docket userDocket() {
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("Jixianda MiniApp User APIs")
                .version("2.0")
                .description("Use header 'authentication' as primary JWT token. 'userId' is compatibility/debug fallback only.")
                .build();

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("MiniApp User APIs")
                .apiInfo(apiInfo)
                .globalOperationParameters(buildGlobalHeaders())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.jixianda.controller.user"))
                .paths(PathSelectors.regex("^(?!/error).*$"))
                .build();
    }

    /**
     * 构建 Swagger 全局请求头参数。
     * user 服务很多接口都依赖 authentication 头传递登录态，这里统一加到文档里，方便调试时直接填写。
     */
    private List<Parameter> buildGlobalHeaders() {
        List<Parameter> headers = new ArrayList<>();
        headers.add(new ParameterBuilder()
                .name("authentication")
                .description("Primary JWT token header for user APIs.")
                .modelRef(new ModelRef("string"))
                .parameterType(ParameterType.HEADER.toString())
                .required(false)
                .build());
        headers.add(new ParameterBuilder()
                .name("userId")
                .description("Compatibility/debug fallback header. Prefer authentication.")
                .modelRef(new ModelRef("string"))
                .parameterType(ParameterType.HEADER.toString())
                .required(false)
                .build());
        return headers;
    }
}
