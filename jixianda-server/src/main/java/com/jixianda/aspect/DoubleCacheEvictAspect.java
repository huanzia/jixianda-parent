package com.jixianda.aspect;

import com.github.benmanes.caffeine.cache.Cache;
import com.jixianda.annotation.DoubleCacheEvict;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Aspect
@Slf4j
@Component
public class DoubleCacheEvictAspect {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ApplicationContext applicationContext;

    public DoubleCacheEvictAspect(RedisTemplate<String, Object> redisTemplate, ApplicationContext applicationContext) {
        this.redisTemplate = redisTemplate;
        this.applicationContext = applicationContext;
    }

    @Pointcut("@annotation(com.jixianda.annotation.DoubleCacheEvict)")
    public void doubleCacheEvictPointCut() {
    }

    @Before("doubleCacheEvictPointCut()")
    public void beforeEvict(JoinPoint joinPoint) {
        DoubleCacheEvict config = getAnnotation(joinPoint);
        evictCaches(config.cacheName(), config.key(), "[\\u5ef6\\u8fdf\\u53cc\\u5220] \\u7b2c\\u4e00\\u6b21\\u6e05\\u7406");
    }

    @AfterReturning("doubleCacheEvictPointCut()")
    public void afterReturningEvict(JoinPoint joinPoint) {
        DoubleCacheEvict config = getAnnotation(joinPoint);
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            evictCaches(config.cacheName(), config.key(), "[\\u5ef6\\u8fdf\\u53cc\\u5220] \\u6267\\u884c\\u7b2c\\u4e8c\\u6b21\\u6e05\\u7406");
        });
    }

    private DoubleCacheEvict getAnnotation(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod().getAnnotation(DoubleCacheEvict.class);
    }

    private void evictCaches(String cacheName, String keyPattern, String logPrefix) {
        if (keyPattern.contains("*")) {
            Set<String> keys = redisTemplate.keys(keyPattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } else {
            redisTemplate.delete(keyPattern);
        }

        Cache<String, Object> localCache = applicationContext.getBean(cacheName, Cache.class);
        clearLocalCache(localCache, keyPattern);
        log.info("{} Redis/Caffeine cleared, cacheName={}, keyPattern={}", logPrefix, cacheName, keyPattern);
    }

    private void clearLocalCache(Cache<String, Object> localCache, String keyPattern) {
        if (!keyPattern.contains("*")) {
            localCache.invalidate(keyPattern);
            return;
        }
        String prefix = keyPattern.substring(0, keyPattern.indexOf('*'));
        for (String key : localCache.asMap().keySet()) {
            if (key != null && key.startsWith(prefix)) {
                localCache.invalidate(key);
            }
        }
    }
}
