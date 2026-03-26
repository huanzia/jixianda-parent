package com.jixianda.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
/**
 * Redis 配置。
 * 这个类属于基础设施配置层，负责把 RedisTemplate 和秒杀扣减脚本装配成 Spring Bean，
 * 供订单主链路里的库存预扣、秒杀库存初始化和库存回滚直接使用。
 */
public class RedisConfiguration {

    @Bean
    /**
     * RedisTemplate 配置。
     * Redis 在项目里主要承担秒杀库存、临时状态和原子操作支撑，所以这里统一配置 key 序列化方式。
     */
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        log.info("Create RedisTemplate bean");
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    @Bean
    /**
     * 秒杀扣减脚本 Bean。
     * 订单主链路会通过这个脚本在 Redis 里原子扣减库存，避免并发抢购时出现超卖。
     */
    public DefaultRedisScript<Long> deductStockScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setLocation(new ClassPathResource("deduct_stock.lua"));
        redisScript.setResultType(Long.class);
        return redisScript;
    }
}
