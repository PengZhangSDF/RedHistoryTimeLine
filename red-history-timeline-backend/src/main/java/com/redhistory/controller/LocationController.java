package com.redhistory.controller;

/**
 * 地点控制器
 * 
 * ========== 功能要求 ==========
 * 1. 处理与历史地点相关的HTTP请求
 * 2. 提供RESTful API接口
 * 3. 参数验证和响应格式化
 * 4. 调用Service层处理业务逻辑
 * 
 * ========== API接口列表 ==========
 * - GET /api/locations - 获取地点列表
 *   * 返回：{ code: 200, data: List<Location>, total: number }
 * 
 * - GET /api/locations/{id} - 获取地点详情
 *   * 参数：id(路径参数)
 *   * 返回：{ code: 200, data: Location }
 * 
 * - GET /api/locations/event/{eventId} - 根据事件ID获取关联地点
 *   * 参数：eventId(路径参数)
 *   * 返回：{ code: 200, data: List<Location>, total: number }
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由地点功能负责人维护
 * - 禁止修改接口路径和HTTP方法
 * - 禁止修改参数格式和返回值格式
 * - 禁止在Controller中添加业务逻辑（必须在Service层）
 * - 可以优化参数验证和错误处理
 * - 其他开发者如需添加新接口，需先确认需求并提交PR
 * 
 * ========== 接口调用要求 ==========
 * - 必须调用 LocationService 处理业务逻辑
 * - 必须使用 ResponseUtil 格式化响应
 * - 必须处理异常情况（返回错误响应）
 * - 响应格式统一：{ code: 200, msg: 'success', data: {...}, total: number }
 * 
 * ========== 函数关联要求 ==========
 * - 被前端 locationApi.js 调用
 * - 调用 LocationService（业务逻辑层）
 * - 调用 ResponseUtil（响应格式化）
 * - 不直接调用Mapper（通过Service层）
 * - 不直接操作数据库
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
    
    /**
     * 获取地点列表
     * 功能要求：
     * - 返回所有地点数据
     * - 包含坐标信息
     * 
     * 修改限制：
     * - 禁止修改参数格式
     * - 禁止修改返回值格式
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getLocationList() {
        
        try {
            // 功能要求：必须调用Service层
            // 修改限制：禁止在Controller中编写业务逻辑
            List<Location> locations = locationService.getAllLocations();
            
            // 数据验证：过滤无效坐标的地点，只返回坐标有效的地点
            List<Location> validLocations = new java.util.ArrayList<>();
            for (Location location : locations) {
                if (ResponseUtil.isValidCoordinate(location.getLongitude(), location.getLatitude())) {
                    validLocations.add(location);
                }
            }
            
            return ResponseEntity.ok(ResponseUtil.success(validLocations, validLocations.size()));
        } catch (Exception e) {
            // 功能要求：统一错误处理
            // 修改限制：禁止直接返回异常信息给前端
            return ResponseEntity.ok(ResponseUtil.error("获取地点列表失败：" + e.getMessage()));
        }
    }
    
    /**
     * 获取地点详情
     * 功能要求：
     * - 根据地点ID获取完整地点信息
     * - 如果地点不存在，返回错误响应
     * 
     * 修改限制：
     * - 禁止修改参数格式
     * - 禁止修改返回值格式
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getLocationDetail(@PathVariable String id) {
        try {
            // 参数验证：ID不能为空
            if (!ResponseUtil.isNotEmpty(id)) {
                return ResponseEntity.ok(ResponseUtil.error("地点ID不能为空"));
            }
            
            Location location = locationService.getLocationById(id);
            if (location != null) {
                // 验证坐标有效性
                if (!ResponseUtil.isValidCoordinate(location.getLongitude(), location.getLatitude())) {
                    return ResponseEntity.ok(ResponseUtil.error("地点坐标格式错误"));
                }
                return ResponseEntity.ok(ResponseUtil.success(location));
            } else {
                return ResponseEntity.ok(ResponseUtil.error("地点未找到"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(ResponseUtil.error("获取地点详情失败：" + e.getMessage()));
        }
    }
    
    /**
     * 根据事件ID获取关联地点列表
     * 功能要求：
     * - 获取指定事件关联的地点
     * 
     * 修改限制：
     * - 禁止修改参数格式
     * - 禁止修改返回值格式
     */
    @GetMapping("/event/{eventId}")
    public ResponseEntity<Map<String, Object>> getLocationsByEvent(@PathVariable String eventId) {
        try {
            // 参数验证：eventId不能为空
            if (!ResponseUtil.isNotEmpty(eventId)) {
                return ResponseEntity.ok(ResponseUtil.error("事件ID不能为空"));
            }
            
            List<Location> locations = locationService.getLocationsByEvent(eventId);
            
            // 数据验证：过滤无效坐标的地点，只返回坐标有效的地点
            List<Location> validLocations = new java.util.ArrayList<>();
            for (Location location : locations) {
                if (ResponseUtil.isValidCoordinate(location.getLongitude(), location.getLatitude())) {
                    validLocations.add(location);
                }
            }
            
            return ResponseEntity.ok(ResponseUtil.success(validLocations, validLocations.size()));
        } catch (Exception e) {
            return ResponseEntity.ok(ResponseUtil.error("获取地点列表失败：" + e.getMessage()));
        }
    }
    
    /**
     * 新增地点
     * 功能要求：
     * - 接收地点对象并保存
     * - 返回保存结果
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addLocation(@RequestBody Location location) {
        try {
            int result = locationService.addLocation(location);
            if (result > 0) {
                return ResponseEntity.ok(ResponseUtil.success("添加地点成功"));
            } else {
                return ResponseEntity.ok(ResponseUtil.error("添加地点失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(ResponseUtil.error("添加地点失败：" + e.getMessage()));
        }
    }
    
    /**
     * 更新地点
     * 功能要求：
     * - 根据ID更新地点信息
     * - 返回更新结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateLocation(@PathVariable String id, @RequestBody Location location) {
        try {
            location.setId(id); // 确保ID一致
            int result = locationService.updateLocation(location);
            if (result > 0) {
                return ResponseEntity.ok(ResponseUtil.success("更新地点成功"));
            } else {
                return ResponseEntity.ok(ResponseUtil.error("更新地点失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(ResponseUtil.error("更新地点失败：" + e.getMessage()));
        }
    }
    
    /**
     * 删除地点
     * 功能要求：
     * - 根据ID删除地点
     * - 返回删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteLocation(@PathVariable String id) {
        try {
            int result = locationService.deleteLocation(id);
            if (result > 0) {
                return ResponseEntity.ok(ResponseUtil.success("删除地点成功"));
            } else {
                return ResponseEntity.ok(ResponseUtil.error("删除地点失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(ResponseUtil.error("删除地点失败：" + e.getMessage()));
        }
    }
}