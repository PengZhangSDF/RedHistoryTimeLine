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
        
        // 开发环境：允许所有来源（包括localhost和127.0.0.1）
        // 生产环境配置示例（需要根据实际部署情况修改）：
        // config.addAllowedOrigin("https://your-frontend-domain.com");
        // config.addAllowedOrigin("https://www.your-frontend-domain.com");
        config.addAllowedOriginPattern("*");
        
        // 允许的HTTP方法：GET、POST、PUT、DELETE、OPTIONS等
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("HEAD");
        
        // 允许的请求头（* 表示允许所有请求头）
        config.addAllowedHeader("*");
        
        // 允许携带凭证（如cookie、session）
        config.setAllowCredentials(true);
        
        // 预检请求的缓存时间（单位：秒）
        // 设置为1小时，减少不必要的预检请求
        config.setMaxAge(3600L);
        
        // 注册CORS配置到所有路径
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}

