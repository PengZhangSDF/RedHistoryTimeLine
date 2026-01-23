package com.redhistory.controller;

/**
 * 事件控制器测试类
 * 
 * ========== 功能要求 ==========
 * 1. 测试EventsController的所有API接口
 * 2. 验证请求参数、响应格式
 * 3. 确保API功能正常
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由测试负责人维护
 * - 可以添加新的测试用例
 * - 禁止修改现有测试的核心逻辑
 * 
 * ========== 测试内容 ==========
 * - 获取所有事件
 * - 获取单个事件详情
 * - 事件过滤功能
 * - 错误处理
 */
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
public class EventControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void testGetAllEvents() throws Exception {
        // 测试正常获取所有事件
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        
        // 测试日期过滤
        mockMvc.perform(get("/api/events")
                .param("startDate", "2020-01-01")
                .param("endDate", "2020-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        
        // 测试类别过滤
        mockMvc.perform(get("/api/events")
                .param("category", "革命事件"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
    
    @Test
    public void testGetEventById() throws Exception {
        // 测试获取存在的事件
        mockMvc.perform(get("/api/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        
        // 测试获取不存在的事件（应返回200，因为控制器捕获了异常并返回错误信息）
        mockMvc.perform(get("/api/events/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }
}

