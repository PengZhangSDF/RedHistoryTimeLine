package com.redhistory.controller;

/**
 * 地点控制器
 * 
 * ========== 功能要求 ==========
 * 1. 处理与历史地点相关的HTTP请求
 * 2. 提供地点坐标、关联事件等接口
 * 3. 用于地图标记和定位功能
 * 
 * ========== API接口列表 ==========
 * - GET /api/locations - 获取所有地点列表（包含坐标）
 *   * 返回：{ code: 200, data: List<Location>, total: number }
 * 
 * - GET /api/locations/{id} - 获取地点详情（包含高德坐标）
 *   * 参数：id(路径参数)
 *   * 返回：{ code: 200, data: Location }
 * 
 * - GET /api/locations/event/{eventId} - 根据事件ID获取关联地点
 *   * 参数：eventId(路径参数)
 *   * 返回：{ code: 200, data: List<Location>, total: number }
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由地图功能负责人维护
 * - 禁止修改接口路径和HTTP方法
 * - 禁止修改参数格式和返回值格式
 * - 禁止在Controller中添加业务逻辑
 * 
 * ========== 接口调用要求 ==========
 * - 必须调用 LocationService 处理业务逻辑
 * - 必须使用 ResponseUtil 格式化响应
 * - 地点数据必须包含坐标字段（longitude, latitude）
 * 
 * ========== 函数关联要求 ==========
 * - 被前端 locationApi.js 调用
 * - 调用 LocationService
 * - 调用 ResponseUtil
 */
import com.redhistory.model.Location;
import com.redhistory.service.LocationService;
import com.redhistory.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/locations")
@CrossOrigin(origins = "*")
public class LocationController {
    
    @Autowired
    private LocationService locationService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllLocations() {
        try {
            List<Location> locations = locationService.getAllLocations();
            return ResponseEntity.ok(ResponseUtil.success(locations, locations.size()));
        } catch (Exception e) {
            return ResponseEntity.ok(ResponseUtil.error("获取地点列表失败：" + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getLocationDetail(@PathVariable String id) {
        try {
            Location location = locationService.getLocationById(id);
            if (location != null) {
                return ResponseEntity.ok(ResponseUtil.success(location));
            } else {
                return ResponseEntity.ok(ResponseUtil.error("地点未找到"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(ResponseUtil.error("获取地点详情失败：" + e.getMessage()));
        }
    }
    
    @GetMapping("/event/{eventId}")
    public ResponseEntity<Map<String, Object>> getLocationsByEvent(@PathVariable String eventId) {
        try {
            List<Location> locations = locationService.getLocationsByEvent(eventId);
            return ResponseEntity.ok(ResponseUtil.success(locations, locations.size()));
        } catch (Exception e) {
            return ResponseEntity.ok(ResponseUtil.error("获取地点列表失败：" + e.getMessage()));
        }
    }
}

