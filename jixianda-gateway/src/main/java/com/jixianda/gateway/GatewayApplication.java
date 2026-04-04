package com.jixianda.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.jixianda")
/**
 * 网关服务的 Spring Boot 启动入口。
 * 这个类只负责把网关应用启动起来，让路由、鉴权和限流配置在运行时生效。
 */
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
