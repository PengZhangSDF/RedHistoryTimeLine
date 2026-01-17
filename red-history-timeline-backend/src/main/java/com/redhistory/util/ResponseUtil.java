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
}

