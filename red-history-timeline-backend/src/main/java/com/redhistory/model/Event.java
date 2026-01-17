package com.redhistory.model;

/**
 * 事件实体类
 * 
 * ========== 功能要求 ==========
 * 1. 定义历史事件的数据结构
 * 2. 对应数据库中的events表
 * 3. 在Controller、Service、Mapper之间传递数据
 * 
 * ========== 字段说明 ==========
 * - id: 事件唯一标识（主键）
 * - title: 事件标题
 * - date: 事件日期（格式：YYYY-MM-DD）
 * - category: 事件类别（如：军事冲突、政治事件等）
 * - description: 事件描述
 * - background: 历史背景
 * - process: 发展经过（可选）
 * - impact: 历史影响
 * - locationId: 关联地点ID（外键，关联locations表）
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由数据模型负责人维护
 * - 禁止修改字段名称（除非数据库表结构变更）
 * - 禁止修改getter/setter方法
 * - 如需添加新字段，需先确认数据库表已更新
 * - 其他开发者如需添加字段，需提交PR
 * 
 * ========== 接口调用要求 ==========
 * - 不调用任何API
 * - 纯数据模型，无业务逻辑
 * 
 * ========== 函数关联要求 ==========
 * - 被 EventService 使用（业务逻辑层）
 * - 被 EventMapper 使用（数据访问层）
 * - 被 EventController 返回给前端
 * - 对应数据库表：events
 */
public class Event {
    private String id;
    private String title;
    private String date;
    private String category;
    private String description;
    private String background;
    private String process; // 可选字段
    private String impact;
    private String locationId; // 关联地点ID
    
    // Getter和Setter方法
    // 功能要求：提供所有字段的getter和setter
    // 修改限制：禁止修改方法名称和返回类型
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getBackground() { return background; }
    public void setBackground(String background) { this.background = background; }
    
    public String getProcess() { return process; }
    public void setProcess(String process) { this.process = process; }
    
    public String getImpact() { return impact; }
    public void setImpact(String impact) { this.impact = impact; }
    
    public String getLocationId() { return locationId; }
    public void setLocationId(String locationId) { this.locationId = locationId; }
}

