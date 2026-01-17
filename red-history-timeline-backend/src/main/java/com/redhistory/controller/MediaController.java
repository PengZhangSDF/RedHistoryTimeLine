package com.redhistory.controller;

/**
 * 媒体资源控制器
 * 
 * ========== 功能要求 ==========
 * 1. 处理与媒体资源相关的HTTP请求
 * 2. 提供图片、视频、音乐等媒体资源的链接
 * 3. 用于详情页的多媒体播放功能
 * 
 * ========== API接口列表 ==========
 * - GET /api/media/event/{eventId} - 根据事件ID获取关联媒体资源
 *   * 参数：eventId(路径参数), type(查询参数，可选：image/video/audio)
 *   * 返回：{ code: 200, data: List<Media>, total: number }
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由媒体功能负责人维护
 * - 禁止修改接口路径和HTTP方法
 * - 禁止修改参数格式和返回值格式
 * - 禁止在Controller中添加业务逻辑
 * 
 * ========== 接口调用要求 ==========
 * - 必须调用 MediaService 处理业务逻辑
 * - 必须使用 ResponseUtil 格式化响应
 * - 媒体数据格式：{ id, type: 'image'|'video'|'audio', url, title, eventId }
 * 
 * ========== 函数关联要求 ==========
 * - 被前端 mediaApi.js 调用
 * - 调用 MediaService
 * - 调用 ResponseUtil
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
    
    @GetMapping("/event/{eventId}")
    public ResponseEntity<Map<String, Object>> getMediaByEvent(
            @PathVariable String eventId,
            @RequestParam(required = false) String type) {
        
        try {
            List<Media> mediaList = mediaService.getMediaByEvent(eventId, type);
            return ResponseEntity.ok(ResponseUtil.success(mediaList, mediaList.size()));
        } catch (Exception e) {
            return ResponseEntity.ok(ResponseUtil.error("获取媒体资源失败：" + e.getMessage()));
        }
    }
}

