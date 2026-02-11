package com.redhistory.controller;

import com.redhistory.model.Material;
import com.redhistory.service.MaterialService;
import com.redhistory.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 事件相关资料控制器
 *
 * ========== 功能要求 ==========
 * 1. 提供根据事件 ID 获取相关资料的 REST 接口
 * 2. 为前端详情页“相关资料”模块提供数据
 *
 * ========== 接口列表 ==========
 * - GET /api/materials/event/{eventId}
 *   * 返回：{ code: 200, data: List<Material>, total: number }
 */
@RestController
@RequestMapping("/api/materials")
@CrossOrigin(origins = "*")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    /**
     * 根据事件 ID 获取相关资料
     */
    @GetMapping("/event/{eventId}")
    public ResponseEntity<Map<String, Object>> getMaterialsByEvent(@PathVariable String eventId) {
        try {
            if (!ResponseUtil.isNotEmpty(eventId)) {
                return ResponseEntity.ok(ResponseUtil.error("事件ID不能为空"));
            }
            List<Material> list = materialService.getMaterialsByEvent(eventId);
            return ResponseEntity.ok(ResponseUtil.success(list, list.size()));
        } catch (Exception e) {
            return ResponseEntity.ok(ResponseUtil.error("获取相关资料失败：" + e.getMessage()));
        }
    }
}

