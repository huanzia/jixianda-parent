package com.jixianda.config;

import com.jixianda.properties.AliOssProperties;
import com.jixianda.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author huanzi
 * @date 2025/12/14 21:32
 */
@Configuration
@Slf4j
public class OssConfiguration {
    /**
     * 配置阿里云OSS文件上传的存储空间
     */
    @Bean
    @ConditionalOnMissingBean
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties){
        log.info("开始创建阿里云文件上传工具类对象：{}",aliOssProperties);
        new AliOssUtil(aliOssProperties.getEndpoint(),
               aliOssProperties.getAccessKeyId(),
               aliOssProperties.getAccessKeySecret(),
               aliOssProperties.getBucketName());
        return new AliOssUtil(aliOssProperties.getEndpoint(), aliOssProperties.getAccessKeyId(), aliOssProperties.getAccessKeySecret(), aliOssProperties.getBucketName());

    }
}
