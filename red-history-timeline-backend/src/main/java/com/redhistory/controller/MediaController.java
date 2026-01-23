package com.redhistory.controller;

/**
 * 媒体控制器
 * 
 * ========== 功能要求 ==========
 * 1. 处理与媒体资源相关的HTTP请求
 * 2. 提供RESTful API接口
 * 3. 参数验证和响应格式化
 * 4. 调用Service层处理业务逻辑
 * 
 * ========== API接口列表 ==========
 * - GET /api/media/event/{eventId} - 根据事件ID获取关联媒体资源
 *   * 参数：eventId(路径参数), type(可选，媒体类型)
 *   * 返回：{ code: 200, data: List<Media>, total: number }
 * 
 * - GET /api/media/{id} - 获取媒体详情
 *   * 参数：id(路径参数)
 *   * 返回：{ code: 200, data: Media }
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由媒体功能负责人维护
 * - 禁止修改接口路径和HTTP方法
 * - 禁止修改参数格式和返回值格式
 * - 禁止在Controller中添加业务逻辑（必须在Service层）
 * - 可以优化参数验证和错误处理
 * - 其他开发者如需添加新接口，需先确认需求并提交PR
 * 
 * ========== 接口调用要求 ==========
 * - 必须调用 MediaService 处理业务逻辑
 * - 必须使用 ResponseUtil 格式化响应
 * - 必须处理异常情况（返回错误响应）
 * - 响应格式统一：{ code: 200, msg: 'success', data: {...}, total: number }
 * 
 * ========== 函数关联要求 ==========
 * - 被前端 mediaApi.js 调用
 * - 调用 MediaService（业务逻辑层）
 * - 调用 ResponseUtil（响应格式化）
 * - 不直接调用Mapper（通过Service层）
 * - 不直接操作数据库
 */
import com.redhistory.model.Media;
import com.redhistory.service.MediaService;
import com.redhistory.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/media")
@CrossOrigin(origins = "*")
public class MediaController {
    
    @Autowired
    private MediaService mediaService;
    
    /**
     * 根据事件ID获取关联媒体资源
     * 功能要求：
     * - 查询事件关联的所有媒体资源
     * - 支持按类型筛选（image/video/audio）
     * - 如果type为null，返回所有类型
     * 
     * 修改限制：
     * - 禁止修改参数格式
     * - 禁止修改返回值格式
     */
    @GetMapping("/event/{eventId}")
    public ResponseEntity<Map<String, Object>> getMediaByEvent(
            @PathVariable String eventId,
            @RequestParam(required = false) String type) {
        
        try {
            // 功能要求：必须调用Service层
            // 修改限制：禁止在Controller中编写业务逻辑
            List<Media> mediaList = mediaService.getMediaByEvent(eventId, type);
            return ResponseEntity.ok(ResponseUtil.success(mediaList, mediaList.size()));
        } catch (Exception e) {
            // 功能要求：统一错误处理
            // 修改限制：禁止直接返回异常信息给前端
            return ResponseEntity.ok(ResponseUtil.error("获取媒体资源失败：" + e.getMessage()));
        }
    }
    
    /**
     * 获取媒体详情
     * 功能要求：
     * - 根据媒体ID获取完整媒体信息
     * - 如果媒体不存在，返回错误响应
     * 
     * 修改限制：
     * - 禁止修改参数格式
     * - 禁止修改返回值格式
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getMediaDetail(@PathVariable String id) {
        try {
            // 参数验证
            if (id == null || id.trim().isEmpty()) {
                return ResponseEntity.ok(ResponseUtil.error("媒体ID不能为空"));
            }
            
            Media media = mediaService.getMediaById(id);
            return ResponseEntity.ok(ResponseUtil.success(media));
        } catch (Exception e) {
            return ResponseEntity.ok(ResponseUtil.error("获取媒体详情失败：" + e.getMessage()));
        }
    }
    
    /**
     * 新增媒体
     * 功能要求：
     * - 接收媒体对象并保存
     * - 返回保存结果
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addMedia(@RequestBody Media media) {
        try {
            int result = mediaService.addMedia(media);
            if (result > 0) {
                return ResponseEntity.ok(ResponseUtil.success("添加媒体成功"));
            } else {
                return ResponseEntity.ok(ResponseUtil.error("添加媒体失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(ResponseUtil.error("添加媒体失败：" + e.getMessage()));
        }
    }
    
    /**
     * 更新媒体
     * 功能要求：
     * - 根据ID更新媒体信息
     * - 返回更新结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateMedia(@PathVariable String id, @RequestBody Media media) {
        try {
            media.setId(id); // 确保ID一致
            int result = mediaService.updateMedia(media);
            if (result > 0) {
                return ResponseEntity.ok(ResponseUtil.success("更新媒体成功"));
            } else {
                return ResponseEntity.ok(ResponseUtil.error("更新媒体失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(ResponseUtil.error("更新媒体失败：" + e.getMessage()));
        }
    }
    
    /**
     * 删除媒体
     * 功能要求：
     * - 根据ID删除媒体
     * - 返回删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteMedia(@PathVariable String id) {
        try {
            int result = mediaService.deleteMedia(id);
            if (result > 0) {
                return ResponseEntity.ok(ResponseUtil.success("删除媒体成功"));
            } else {
                return ResponseEntity.ok(ResponseUtil.error("删除媒体失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(ResponseUtil.error("删除媒体失败：" + e.getMessage()));
        }
    }
}