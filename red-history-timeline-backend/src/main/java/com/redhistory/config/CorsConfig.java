package com.redhistory.config;

/**
 * CORS跨域配置类
 * 
 * ========== 功能要求 ==========
 * 1. 配置允许跨域请求的源、方法、头部
 * 2. 解决前端调用后端API时的跨域问题
 * 3. 生产环境应限制允许的源地址
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由项目负责人或运维人员维护
 * - 开发环境可以允许所有来源（*）
 * - 生产环境必须限制允许的源地址
 * - 禁止修改CORS的核心配置逻辑
 * - 其他开发者禁止修改此文件
 * 
 * ========== 接口调用要求 ==========
 * - 不调用任何API
 * - 配置类，在应用启动时生效
 * 
 * ========== 函数关联要求 ==========
 * - 被Spring Boot自动加载
 * - 影响所有Controller的跨域行为
 * - 不依赖其他类
 */
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    
    /**
     * CORS过滤器配置
     * 功能要求：
     * - 允许跨域请求
     * - 配置允许的源、方法、头部
     * 
     * 修改限制：
     * - 开发环境：可以允许所有来源（当前配置）
     * - 生产环境：必须限制允许的源地址
     * - 禁止修改允许的方法和头部（除非有特殊需求）
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // 功能要求：开发环境允许所有来源
        // 修改限制：生产环境必须修改为具体的前端地址
        // 例如：config.addAllowedOrigin("https://your-frontend-domain.com");
        config.addAllowedOriginPattern("*");
        
        // 功能要求：允许所有HTTP方法
        // 修改限制：禁止修改，除非有特殊需求
        config.addAllowedMethod("*");
        
        // 功能要求：允许所有请求头
        // 修改限制：禁止修改，除非有特殊需求
        config.addAllowedHeader("*");
        
        // 功能要求：允许携带凭证（如cookie）
        // 修改限制：禁止修改
        config.setAllowCredentials(true);
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}

