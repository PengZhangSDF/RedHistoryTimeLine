package com.redhistory.config;

/**
 * 高德地图API配置类（可选）
 * 
 * ========== 功能要求 ==========
 * 1. 配置高德地图API Key
 * 2. 提供高德API调用的配置信息
 * 3. 如果后端需要调用高德API解析地址，可以在这里配置
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由地图功能负责人维护
 * - 如果只在前端使用高德地图，此配置类可以省略
 * - 如果后端需要解析地址获取坐标，需要配置此类和AmapUtil工具类
 * - 禁止在代码中硬编码API Key
 * - 其他开发者禁止修改此文件
 * 
 * ========== 接口调用要求 ==========
 * - 不直接调用高德API
 * - 提供配置信息给AmapUtil使用
 * 
 * ========== 函数关联要求 ==========
 * - 被 AmapUtil 引用（如果后端需要解析地址）
 * - 从application.yml读取配置
 * - 如果不需要后端解析地址，可以删除此文件
 */
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmapConfig {
    
    /**
     * 高德地图API Key
     * 功能要求：从application.yml读取配置
     * 配置路径：amap.key
     * 
     * 修改限制：
     * - 禁止硬编码API Key
     * - 必须在application.yml中配置
     */
    @Value("${amap.key:}")
    private String apiKey;
    
    public String getApiKey() {
        return apiKey;
    }
}

