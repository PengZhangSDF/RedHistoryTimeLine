package com.redhistory.controller;

/**
 * 事件控制器
 * 
 * ========== 功能要求 ==========
 * 1. 处理与历史事件相关的HTTP请求
 * 2. 提供RESTful API接口
 * 3. 参数验证和响应格式化
 * 4. 调用Service层处理业务逻辑
 * 
 * ========== API接口列表 ==========
 * - GET /api/events - 获取事件列表（支持时间/地点筛选）
 *   * 参数：startDate(可选), endDate(可选), locationId(可选)
 *   * 返回：{ code: 200, data: List<Event>, total: number }
 * 
 * - GET /api/events/{id} - 获取事件详情
 *   * 参数：id(路径参数)
 *   * 返回：{ code: 200, data: Event }
 * 
 * - GET /api/events/location/{locationId} - 根据地点获取事件列表
 *   * 参数：locationId(路径参数)
 *   * 返回：{ code: 200, data: List<Event>, total: number }
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由事件功能负责人维护
 * - 禁止修改接口路径和HTTP方法
 * - 禁止修改参数格式和返回值格式
 * - 禁止在Controller中添加业务逻辑（必须在Service层）
 * - 可以优化参数验证和错误处理
 * - 其他开发者如需添加新接口，需先确认需求并提交PR
 * 
 * ========== 接口调用要求 ==========
 * - 必须调用 EventService 处理业务逻辑
 * - 必须使用 ResponseUtil 格式化响应
 * - 必须处理异常情况（返回错误响应）
 * - 响应格式统一：{ code: 200, msg: 'success', data: {...}, total: number }
 * 
 * ========== 函数关联要求 ==========
 * - 被前端 eventApi.js 调用
 * - 调用 EventService（业务逻辑层）
 * - 调用 ResponseUtil（响应格式化）
 * - 不直接调用Mapper（通过Service层）
 * - 不直接操作数据库
 */
import com.redhistory.model.Event;
import com.redhistory.service.EventService;
import com.redhistory.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {
    
    @Autowired
    private EventService eventService;
    
    /**
     * 获取事件列表
     * 功能要求：
     * - 支持按时间范围筛选（startDate, endDate）
     * - 支持按地点筛选（locationId）
     * - 返回事件列表和总数
     * 
     * 修改限制：
     * - 禁止修改参数格式
     * - 禁止修改返回值格式
     * - 可以添加新的筛选参数（需团队讨论）
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getEventList(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String locationId) {
        
        try {
            // 功能要求：必须调用Service层
            // 修改限制：禁止在Controller中编写业务逻辑
            List<Event> events = eventService.getEventList(startDate, endDate, locationId);
            return ResponseEntity.ok(ResponseUtil.success(events, events.size()));
        } catch (Exception e) {
            // 功能要求：统一错误处理
            // 修改限制：禁止直接返回异常信息给前端
            return ResponseEntity.ok(ResponseUtil.error("获取事件列表失败：" + e.getMessage()));
        }
    }
    
    /**
     * 获取事件详情
     * 功能要求：
     * - 根据事件ID获取完整事件信息
     * - 如果事件不存在，返回错误响应
     * 
     * 修改限制：
     * - 禁止修改参数格式
     * - 禁止修改返回值格式
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getEventDetail(@PathVariable String id) {
        try {
            Event event = eventService.getEventById(id);
            if (event != null) {
                return ResponseEntity.ok(ResponseUtil.success(event));
            } else {
                return ResponseEntity.ok(ResponseUtil.error("事件未找到"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(ResponseUtil.error("获取事件详情失败：" + e.getMessage()));
        }
    }
    
    /**
     * 根据地点ID获取事件列表
     * 功能要求：
     * - 获取指定地点发生的所有事件
     * - 按时间排序
     * 
     * 修改限制：
     * - 禁止修改参数格式
     * - 禁止修改返回值格式
     */
    @GetMapping("/location/{locationId}")
    public ResponseEntity<Map<String, Object>> getEventsByLocation(@PathVariable String locationId) {
        try {
            List<Event> events = eventService.getEventsByLocation(locationId);
            return ResponseEntity.ok(ResponseUtil.success(events, events.size()));
        } catch (Exception e) {
            return ResponseEntity.ok(ResponseUtil.error("获取事件列表失败：" + e.getMessage()));
        }
    }
    
    /**
     * 新增事件
     * 功能要求：
     * - 接收事件对象并保存
     * - 返回保存结果
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addEvent(@RequestBody Event event) {
        try {
            int result = eventService.addEvent(event);
            if (result > 0) {
                return ResponseEntity.ok(ResponseUtil.success("添加事件成功"));
            } else {
                return ResponseEntity.ok(ResponseUtil.error("添加事件失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(ResponseUtil.error("添加事件失败：" + e.getMessage()));
        }
    }
    
    /**
     * 更新事件
     * 功能要求：
     * - 根据ID更新事件信息
     * - 返回更新结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateEvent(@PathVariable String id, @RequestBody Event event) {
        try {
            event.setId(id); // 确保ID一致
            int result = eventService.updateEvent(event);
            if (result > 0) {
                return ResponseEntity.ok(ResponseUtil.success("更新事件成功"));
            } else {
                return ResponseEntity.ok(ResponseUtil.error("更新事件失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(ResponseUtil.error("更新事件失败：" + e.getMessage()));
        }
    }
    
    /**
     * 删除事件
     * 功能要求：
     * - 根据ID删除事件
     * - 返回删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteEvent(@PathVariable String id) {
        try {
            int result = eventService.deleteEvent(id);
            if (result > 0) {
                return ResponseEntity.ok(ResponseUtil.success("删除事件成功"));
            } else {
                return ResponseEntity.ok(ResponseUtil.error("删除事件失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(ResponseUtil.error("删除事件失败：" + e.getMessage()));
        }
    }
}

