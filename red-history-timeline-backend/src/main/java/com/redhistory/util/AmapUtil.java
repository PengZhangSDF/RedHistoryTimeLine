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
import org.springframework.web.client.RestTemplate;
import com.redhistory.config.AmapConfig;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class AmapUtil {
    
    @Autowired(required = false)
    private AmapConfig amapConfig;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
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
     */
    public Double[] geocodeAddress(String address) {
        if (amapConfig == null || amapConfig.getApiKey() == null || amapConfig.getApiKey().isEmpty()) {
            return null;
        }
        
        try {
            String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
            String url = "https://restapi.amap.com/v3/geocode/geo?key=" + amapConfig.getApiKey() + "&address=" + encodedAddress;
            
            Map<?, ?> response = restTemplate.getForObject(url, Map.class);
            if (response != null && "1".equals(response.get("status"))) {
                Object geocodes = response.get("geocodes");
                if (geocodes instanceof java.util.List) {
                    java.util.List<?> geocodesList = (java.util.List<?>) geocodes;
                    if (!geocodesList.isEmpty()) {
                        Object geocode = geocodesList.get(0);
                        if (geocode instanceof Map) {
                            Map<?, ?> geocodeMap = (Map<?, ?>) geocode;
                            Object locationObj = geocodeMap.get("location");
                            if (locationObj != null) {
                                String location = locationObj.toString();
                                String[] coords = location.split(",");
                                if (coords.length == 2) {
                                    return new Double[]{Double.parseDouble(coords[0]), Double.parseDouble(coords[1])};
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
     */
    public String reverseGeocode(Double longitude, Double latitude) {
        if (amapConfig == null || amapConfig.getApiKey() == null || amapConfig.getApiKey().isEmpty()) {
            return null;
        }
        
        try {
            String url = "https://restapi.amap.com/v3/geocode/regeo?key=" + amapConfig.getApiKey() + "&location=" + longitude + "," + latitude;
            
            Map<?, ?> response = restTemplate.getForObject(url, Map.class);
            if (response != null && "1".equals(response.get("status"))) {
                Object regeocode = response.get("regeocode");
                if (regeocode instanceof Map) {
                    Map<?, ?> regeocodeMap = (Map<?, ?>) regeocode;
                    Object formattedAddressObj = regeocodeMap.get("formatted_address");
                    if (formattedAddressObj != null) {
                        return formattedAddressObj.toString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

