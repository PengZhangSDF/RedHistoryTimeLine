package com.redhistory.model;

/**
 * 地点实体类
 * 
 * ========== 功能要求 ==========
 * 1. 定义历史地点的数据结构
 * 2. 对应数据库中的locations表
 * 3. 包含高德地图坐标信息
 * 
 * ========== 字段说明 ==========
 * - id: 地点唯一标识（主键）
 * - name: 地点名称
 * - province: 省份
 * - longitude: 经度（高德坐标）
 * - latitude: 纬度（高德坐标）
 * - description: 地点描述（可选）
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由数据模型负责人维护
 * - 禁止修改字段名称（除非数据库表结构变更）
 * - 禁止修改getCoordinates()方法的返回格式
 * - 如需添加新字段，需先确认数据库表已更新
 * 
 * ========== 函数关联要求 ==========
 * - 被 LocationService 使用
 * - 被 LocationMapper 使用
 * - 被 LocationController 返回给前端
 * - 对应数据库表：locations
 * 
 * ========== 坐标格式说明 ==========
 * - 前端需要坐标数组格式：[longitude, latitude]
 * - getCoordinates()方法提供此格式
 * - 如果坐标为空，返回null
 */
public class Location {
    private String id;
    private String name;
    private String province;
    private Double longitude; // 经度
    private Double latitude;  // 纬度
    private String description;
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    /**
     * 获取坐标数组（用于前端地图标记）
     * 功能要求：
     * - 返回坐标数组格式：[longitude, latitude]
     * - 如果坐标为空，返回null
     * 
     * 修改限制：
     * - 禁止修改返回格式
     * - 禁止修改方法名称
     */
    public Double[] getCoordinates() {
        if (longitude != null && latitude != null) {
            return new Double[]{longitude, latitude};
        }
        return null;
    }
}

