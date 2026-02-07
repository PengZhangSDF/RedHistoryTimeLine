package com.redhistory.exception;

/**
 * 全局异常处理器
 * 
 * ========== 功能要求 ==========
 * 1. 统一处理系统中可能出现的各类异常
 * 2. 捕获并转换异常为标准化的错误响应格式
 * 3. 避免在各个控制器方法中重复编写异常处理代码
 * 4. 提高代码的可维护性和复用性
 * 
 * ========== 异常类型处理 ==========
 * - 媒体资源不存在异常：RuntimeException（自定义消息）
 * - 数据库操作异常：SQLException
 * - 其他通用异常：Exception
 * 
 * ========== 响应格式 ==========
 * { "code": 错误码, "msg": "错误信息", "data": null, "total": 0 }
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由异常处理负责人维护
 * - 可以添加新的异常处理类型
 * - 禁止修改响应格式
 */
import com.redhistory.util.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 处理运行时异常（如媒体资源不存在）
     * 功能要求：处理RuntimeException类型的异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.ok(ResponseUtil.error(e.getMessage()));
    }
    
    /**
     * 处理数据库操作异常
     * 功能要求：处理SQL执行过程中的异常
     */
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Map<String, Object>> handleSQLException(SQLException e) {
        return ResponseEntity.ok(ResponseUtil.error("数据库操作失败：" + e.getMessage()));
    }
    
    /**
     * 处理通用异常
     * 功能要求：处理其他未明确指定的异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        return ResponseEntity.ok(ResponseUtil.error("系统错误：" + e.getMessage()));
    }
}
