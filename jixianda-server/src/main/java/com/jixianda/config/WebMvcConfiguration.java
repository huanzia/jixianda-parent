package com.jixianda.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.jixianda.interceptor.JwtTokenAdminInterceptor;
import com.jixianda.interceptor.JwtTokenUserInterceptor;
import com.jixianda.json.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ParameterType;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableKnife4j
@EnableSwagger2
@Slf4j
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;

    @Autowired
    private JwtTokenUserInterceptor jwtTokenUserInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("Registering custom interceptors");
        registry.addInterceptor(jwtTokenAdminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/employee/login");
        registry.addInterceptor(jwtTokenUserInterceptor)
                .addPathPatterns("/user/**")
                .excludePathPatterns("/user/user/login")
                .excludePathPatterns("/user/shop/status");
    }

    @Bean
    public Docket adminDocket() {
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("极鲜达-管理端接口")
                .version("2.0")
                .description("管理端/B端联调接口文档，登录后请在请求头使用 token")
                .build();

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("管理端接口")
                .apiInfo(apiInfo)
                .globalOperationParameters(buildGlobalHeaders())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.jixianda.controller.admin"))
                .paths(PathSelectors.regex("^(?!/error).*$"))
                .build();
    }

    @Bean
    public Docket cEndDocket() {
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("极鲜达-C端接口")
                .version("2.0")
                .description("C端联调接口文档，登录后请在请求头使用 authentication")
                .build();

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("C端业务接口")
                .apiInfo(apiInfo)
                .globalOperationParameters(buildGlobalHeaders())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.jixianda.controller.user"))
                .paths(PathSelectors.regex("^(?!/error).*$"))
                .build();
    }

    private List<Parameter> buildGlobalHeaders() {
        List<Parameter> headers = new ArrayList<>();
        headers.add(new ParameterBuilder()
                .name("authentication")
                .description("用户登录token（C端接口常用）")
                .modelRef(new springfox.documentation.schema.ModelRef("string"))
                .parameterType(ParameterType.HEADER.toString())
                .required(false)
                .build());
        headers.add(new ParameterBuilder()
                .name("token")
                .description("管理端token（管理端接口常用）")
                .modelRef(new springfox.documentation.schema.ModelRef("string"))
                .parameterType(ParameterType.HEADER.toString())
                .required(false)
                .build());
        return headers;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(new JacksonObjectMapper());
        converters.add(0, converter);
    }
}
