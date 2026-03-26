package com.jixianda;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableCaching
@EnableScheduling
@EnableFeignClients(basePackages = "com.jixianda.client")
@Slf4j
public class JixiandaApplication {

    public static void main(String[] args) {
        SpringApplication.run(JixiandaApplication.class, args);
        log.info("server started");
    }
}
