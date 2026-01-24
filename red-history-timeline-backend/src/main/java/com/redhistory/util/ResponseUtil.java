package com.redhistory.util;

/**
 * 统一响应格式工具类
 * 
 * ========== 功能要求 ==========
 * 1. 统一所有API接口的响应格式
 * 2. 包含成功/失败状态、数据、消息等信息
 * 3. 便于前端统一处理响应
 * 
 * ========== 响应格式 ==========
 * 成功响应：
 * {
 *   "code": 200,
 *   "msg": "success",
 *   "data": {...},
 *   "total": 0  // 可选，列表数据时使用
 * }
 * 
 * 错误响应：
 * {
 *   "code": 500,
 *   "msg": "错误信息",
 *   "data": null
 * }
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由工具类负责人维护
 * - 禁止修改响应格式结构（code、msg、data、total）
 * - 禁止修改方法签名
 * - 如需添加新的响应方法，需团队讨论
 * - 其他开发者禁止修改此文件
 * 
 * ========== 接口调用要求 ==========
 * - 不调用任何API
 * - 纯工具类，无副作用
 * 
 * ========== 函数关联要求 ==========
 * - 被所有Controller使用
 * - 不依赖其他类
 * 
 * ========== 使用示例 ==========
 * // 成功响应
 * return ResponseEntity.ok(ResponseUtil.success(data));
 * return ResponseEntity.ok(ResponseUtil.success(data, total));
 * 
 * // 错误响应
 * return ResponseEntity.ok(ResponseUtil.error("错误信息"));
 */
import java.util.HashMap;
import java.util.Map;

public class ResponseUtil {
    
    /**
     * 成功响应（带数据）
     * 功能要求：返回成功响应，包含数据
     * 
     * 修改限制：
     * - 禁止修改响应格式
     * - 禁止修改方法签名
     */
    public static Map<String, Object> success(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("msg", "success");
        response.put("data", data);
        return response;
    }
    
    /**
     * 成功响应（带数据和总数）
     * 功能要求：返回成功响应，包含数据和总数（用于列表数据）
     * 
     * 修改限制：
     * - 禁止修改响应格式
     * - 禁止修改方法签名
     */
    public static Map<String, Object> success(Object data, Integer total) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("msg", "success");
        response.put("data", data);
        response.put("total", total);
        return response;
    }
    
    /**
     * 错误响应
     * 功能要求：返回错误响应，包含错误信息
     * 
     * 修改限制：
     * - 禁止修改响应格式
     * - 禁止修改方法签名
     */
    public static Map<String, Object> error(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 500);
        response.put("msg", message);
        response.put("data", null);
        return response;
    }
    
    /**
     * ========== 参数验证工具方法 ==========
     * 以下方法用于全局数据校验
     */
    
    /**
     * 验证字符串是否为空
     * 功能要求：检查字符串是否为null或空白
     * 
     * 用法：
     * if (!ResponseUtil.isNotEmpty(id)) {
     *     return ResponseEntity.ok(ResponseUtil.error("ID不能为空"));
     * }
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    /**
     * 验证日期格式（YYYY-MM-DD）
     * 功能要求：验证日期格式是否符合YYYY-MM-DD规范
     * 
     * 用法：
     * if (!ResponseUtil.isValidDate(startDate)) {
     *     return ResponseEntity.ok(ResponseUtil.error("日期格式错误，请使用YYYY-MM-DD"));
     * }
     */
    public static boolean isValidDate(String dateStr) {
        if (!isNotEmpty(dateStr)) {
            return false;
        }
        
        // 检查格式：YYYY-MM-DD
        if (!dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return false;
        }
        
        try {
            // 验证日期的有效性（月份1-12，日期1-31等）
            String[] parts = dateStr.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);
            
            // 基本范围检查
            if (month < 1 || month > 12) {
                return false;
            }
            if (day < 1 || day > 31) {
                return false;
            }
            if (year < 1 || year > 9999) {
                return false;
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 验证坐标格式
     * 功能要求：验证经纬度是否在有效范围内
     * - 经度范围：-180 ~ 180
     * - 纬度范围：-90 ~ 90
     * 
     * 用法：
     * if (!ResponseUtil.isValidCoordinate(longitude, latitude)) {
     *     return ResponseEntity.ok(ResponseUtil.error("坐标格式错误"));
     * }
     */
    public static boolean isValidCoordinate(Double longitude, Double latitude) {
        if (longitude == null || latitude == null) {
            return false;
        }
        
        // 验证经度范围：-180 ~ 180
        if (longitude < -180.0 || longitude > 180.0) {
            return false;
        }
        
        // 验证纬度范围：-90 ~ 90
        if (latitude < -90.0 || latitude > 90.0) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 验证媒体类型
     * 功能要求：验证媒体类型是否为有效类型（image/video/audio）
     * 
     * 用法：
     * if (mediaType != null && !ResponseUtil.isValidMediaType(mediaType)) {
     *     return ResponseEntity.ok(ResponseUtil.error("媒体类型错误，只支持image/video/audio"));
     * }
     */
    public static boolean isValidMediaType(String type) {
        if (!isNotEmpty(type)) {
            return false;
        }
        
        return type.equalsIgnoreCase("image") || 
               type.equalsIgnoreCase("video") || 
               type.equalsIgnoreCase("audio");
    }
    
    /**
     * 验证时间范围的有效性
     * 功能要求：检查startDate是否不晚于endDate
     * 
     * 用法：
     * if (!ResponseUtil.isValidDateRange(startDate, endDate)) {
     *     return ResponseEntity.ok(ResponseUtil.error("开始日期不能晚于结束日期"));
     * }
     */
    public static boolean isValidDateRange(String startDate, String endDate) {
        // 如果其中一个为空，则认为有效（单边条件查询）
        if (!isNotEmpty(startDate) || !isNotEmpty(endDate)) {
            return true;
        }
        
        // 都不为空时，比较大小
        return startDate.compareTo(endDate) <= 0;
    }
}

