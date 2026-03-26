package com.jixianda.user;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = "com.jixianda")
@EnableDiscoveryClient
@EnableTransactionManagement
@MapperScan("com.jixianda.mapper")
@Slf4j
/**
 * 用户服务的 Spring Boot 启动入口。
 * 这个类负责把 user 模块启动起来，让它注册到 Nacos，并对外提供用户、地址簿和仓库相关能力。
 */
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
        log.info("jixianda-user started");
    }
}
