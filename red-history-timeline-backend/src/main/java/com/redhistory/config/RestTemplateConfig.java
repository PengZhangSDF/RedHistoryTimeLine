package com.redhistory.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * RestTemplate 配置
 *
 * 用途：
 * - 为 {@link com.redhistory.util.AmapUtil} 等需要发起 HTTP 请求的工具类提供 RestTemplate Bean。
 *
 * 说明：
 * - 这是一个通用 HTTP 客户端配置，不绑定具体业务。
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(10))
                .build();
    }
}



