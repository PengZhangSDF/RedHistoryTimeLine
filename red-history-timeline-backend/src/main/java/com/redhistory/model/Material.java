package com.redhistory.model;

/**
 * 事件相关资料实体类
 *
 * ========== 功能要求 ==========
 * 1. 定义事件相关资料的数据结构（如权威网站、百科、新闻等链接）
 * 2. 对应数据库中的 materials 表
 * 3. 被 Service、Mapper、Controller 在各层之间传递
 *
 * ========== 字段说明 ==========
 * - id: 资料唯一标识（主键）
 * - eventId: 关联事件ID（外键，关联 events 表）
 * - title: 资料标题
 * - url: 资料链接（HTTP(S) 地址）
 * - type: 资料类型（历史文献/新闻报道/官方网站/百科条目/其他等）
 *
 * ========== 修改范围限制 ==========
 * - 此文件由数据模型负责人维护
 * - 禁止修改字段名称（除非数据库表结构变更）
 * - 禁止修改 getter/setter 方法的签名
 *
 * ========== 函数关联要求 ==========
 * - 被 MaterialService 使用
 * - 被 MaterialMapper 使用
 * - 被 MaterialController 返回给前端
 * - 对应数据库表：materials
 */
public class Material {
    private String id;
    private String eventId;
    private String title;
    private String url;
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

