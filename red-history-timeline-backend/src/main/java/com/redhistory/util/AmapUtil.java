package com.redhistory.util;

/**
 * 高德地图工具类（可选）
 * 
 * ========== 功能要求 ==========
 * 1. 封装高德地图API的调用（如地理编码、逆地理编码）
 * 2. 在后端解析地址获取坐标
 * 3. 如果只在前端使用高德地图，此工具类可以省略
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由地图功能负责人维护
 * - 如果不需要后端解析地址，可以删除此文件
 * - 需要配置AmapConfig类
 * - 需要添加高德地图Java SDK依赖
 * - 其他开发者禁止修改此文件
 * 
 * ========== 接口调用要求 ==========
 * - 调用高德地图Web Service API
 * - 需要高德地图API Key
 * - 需要添加HTTP客户端依赖（如RestTemplate或OkHttp）
 * 
 * ========== 函数关联要求 ==========
 * - 被 LocationService 可选引用（如果后端需要解析地址）
 * - 引用 AmapConfig（获取API Key）
 * - 如果不需要后端解析，可以删除此文件
 * 
 * ========== 注意 ==========
 * - 当前实现为占位代码
 * - 如需使用，需要：
 *   1. 添加高德地图Java SDK依赖
 *   2. 实现geocodeAddress和reverseGeocode方法
 *   3. 在LocationService中调用
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.redhistory.config.AmapConfig;

@Component
public class AmapUtil {
    
    @Autowired(required = false)
    private AmapConfig amapConfig;
    
    /**
     * 地理编码：根据地址获取坐标
     * 功能要求：
     * - 调用高德地图地理编码API
     * - 将地址字符串转换为坐标
     * - 返回坐标数组 [longitude, latitude]
     * 
     * 修改限制：
     * - 禁止修改返回格式
     * - 需要实现高德API调用逻辑
     * 
     * 当前状态：占位方法，需要实现
     */
    public Double[] geocodeAddress(String address) {
        // TODO: 实现高德地图地理编码API调用
        // 需要：
        // 1. 添加高德地图Java SDK依赖
        // 2. 使用AmapConfig获取API Key
        // 3. 调用高德API
        // 4. 解析响应，返回坐标
        return null;
    }
    
    /**
     * 逆地理编码：根据坐标获取地址
     * 功能要求：
     * - 调用高德地图逆地理编码API
     * - 将坐标转换为地址字符串
     * - 返回地址信息
     * 
     * 修改限制：
     * - 禁止修改参数格式
     * - 需要实现高德API调用逻辑
     * 
     * 当前状态：占位方法，需要实现
     */
    public String reverseGeocode(Double longitude, Double latitude) {
        // TODO: 实现高德地图逆地理编码API调用
        return null;
    }
}

