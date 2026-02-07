package com.redhistory.service;

/**
 * 媒体资源业务逻辑层
 * 
 * ========== 功能要求 ==========
 * 1. 处理媒体资源相关的业务逻辑
 * 2. 管理图片、视频、音乐等资源的链接
 * 3. 支持按类型筛选媒体资源
 * 4. 调用Mapper层进行数据库操作
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由媒体功能负责人维护
 * - 禁止直接操作数据库
 * - 可以优化业务逻辑
 * 
 * ========== 接口调用要求 ==========
 * - 必须调用 MediaMapper 进行数据库操作
 * 
 * ========== 函数关联要求 ==========
 * - 被 MediaController 调用
 * - 调用 MediaMapper
 */
import com.redhistory.mapper.MediaMapper;
import com.redhistory.model.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MediaService {
    
    @Autowired
    private MediaMapper mediaMapper;
    
    /**
     * 根据事件ID获取关联媒体资源（支持类型过滤）
     * 功能要求：
     * - 查询事件关联的所有媒体资源
     * - 支持按类型筛选（image/video/audio）
     * - 如果type为null，返回所有类型
     * 
     * 修改限制：
     * - 禁止修改Mapper调用方式
     * - 可以优化筛选逻辑
     */
    @Cacheable(value = "mediaByEvent", key = "#eventId + '_' + #type")
    public List<Media> getMediaByEvent(String eventId, String type) {
        // 功能要求：必须调用Mapper层
        // 修改限制：禁止在Service中编写SQL
        return mediaMapper.selectMediaByEvent(eventId, type);
    }
    
    /**
     * 新增媒体
     * 功能要求：新增媒体记录
     */
    public int addMedia(Media media) {
        return mediaMapper.insertMedia(media);
    }
    
    /**
     * 更新媒体
     * 功能要求：根据ID更新媒体记录
     */
    public int updateMedia(Media media) {
        return mediaMapper.updateMedia(media);
    }
    
    /**
     * 删除媒体
     * 功能要求：根据ID删除媒体记录
     */
    public int deleteMedia(String id) {
        return mediaMapper.deleteMedia(id);
    }
    
    /**
     * 根据ID获取媒体详情
     * 功能要求：
     * - 根据媒体ID查询完整的媒体信息
     * - 实现缓存机制以提高查询性能
     * - 处理媒体ID不存在的情况
     */
    @Cacheable(value = "media", key = "#id")
    public Media getMediaById(String id) {
        Media media = mediaMapper.selectMediaById(id);
        if (media == null) {
            throw new RuntimeException("媒体资源不存在");
        }
        return media;
    }
}

