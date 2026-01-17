package com.redhistory.model;

/**
 * 人物实体类
 * 
 * ========== 功能要求 ==========
 * 1. 定义历史人物的数据结构
 * 2. 对应数据库中的persons表
 * 3. 在Controller、Service、Mapper之间传递数据
 * 
 * ========== 字段说明 ==========
 * - id: 人物唯一标识（主键）
 * - name: 人物姓名
 * - role: 人物角色（如：军事将领、政治领袖等）
 * - description: 人物简介
 * - birthDate: 出生日期（格式：YYYY-MM-DD，可选）
 * - deathDate: 逝世日期（格式：YYYY-MM-DD，可选）
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由数据模型负责人维护
 * - 禁止修改字段名称（除非数据库表结构变更）
 * - 禁止修改getter/setter方法
 * 
 * ========== 函数关联要求 ==========
 * - 被 PersonService 使用
 * - 被 PersonMapper 使用
 * - 被 PersonController 返回给前端
 * - 对应数据库表：persons
 */
public class Person {
    private String id;
    private String name;
    private String role;
    private String description;
    private String birthDate;
    private String deathDate;
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }
    
    public String getDeathDate() { return deathDate; }
    public void setDeathDate(String deathDate) { this.deathDate = deathDate; }
}

