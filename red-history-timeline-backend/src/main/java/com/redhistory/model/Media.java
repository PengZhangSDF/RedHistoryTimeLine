package com.redhistory.model;

/**
 * 媒体资源实体类
 * 
 * ========== 功能要求 ==========
 * 1. 定义媒体资源的数据结构
 * 2. 对应数据库中的media表
 * 3. 包含图片、视频、音乐等资源的链接
 * 
 * ========== 字段说明 ==========
 * - id: 媒体资源唯一标识（主键）
 * - type: 媒体类型（image/video/audio）
 * - url: 媒体资源链接（相对路径或绝对路径）
 * - title: 媒体标题（可选）
 * - eventId: 关联事件ID（外键，关联events表）
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由数据模型负责人维护
 * - 禁止修改字段名称（除非数据库表结构变更）
 * - 禁止修改getter/setter方法
 * 
 * ========== 函数关联要求 ==========
 * - 被 MediaService 使用
 * - 被 MediaMapper 使用
 * - 被 MediaController 返回给前端
 * - 对应数据库表：media
 */
public class Media {
    private String id;
    private String type; // image, video, audio
    private String url;
    private String title;
    private String eventId;
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
}

