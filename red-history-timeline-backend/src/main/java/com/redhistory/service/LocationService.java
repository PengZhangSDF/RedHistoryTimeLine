package com.redhistory.service;

/**
 * 地点业务逻辑层
 * 
 * ========== 功能要求 ==========
 * 1. 处理地点相关的业务逻辑
 * 2. 关联高德坐标解析（可选，如果后端需要解析地址）
 * 3. 关联查询地点与事件的关系
 * 4. 调用Mapper层进行数据库操作
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由地图功能负责人维护
 * - 禁止直接操作数据库
 * - 可以调用AmapUtil解析地址（如果后端需要）
 * - 可以优化业务逻辑
 * 
 * ========== 接口调用要求 ==========
 * - 必须调用 LocationMapper 进行数据库操作
 * - 可选调用 AmapUtil 解析地址（如果坐标为空）
 * 
 * ========== 函数关联要求 ==========
 * - 被 LocationController 调用
 * - 调用 LocationMapper
 * - 可选调用 AmapUtil（高德工具类）
 */
import com.redhistory.mapper.LocationMapper;
import com.redhistory.model.Location;
import com.redhistory.util.AmapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {
    
    @Autowired
    private LocationMapper locationMapper;
    
    @Autowired(required = false)
    private AmapUtil amapUtil; // 可选：高德工具类（如果后端需要解析地址）
    
    /**
     * 获取所有地点列表（包含坐标）
     * 功能要求：返回所有地点数据，用于首页地图标记
     */
    @Cacheable(value = "locations")
    public List<Location> getAllLocations() {
        return locationMapper.selectAllLocations();
    }
    
    /**
     * 根据ID获取地点详情（包含高德坐标）
     * 功能要求：
     * - 查询地点完整信息
     * - 如果坐标为空，可以调用AmapUtil解析地址（可选）
     * 
     * 修改限制：
     * - 禁止修改Mapper调用方式
     * - 坐标解析逻辑可以优化
     */
    @Cacheable(value = "location", key = "#id")
    public Location getLocationById(String id) {
        Location location = locationMapper.selectLocationById(id);
        
        // 可选功能：如果坐标为空，使用高德API解析地址
        // 功能要求：如果后端需要解析地址，可以在此处调用AmapUtil
        // 修改限制：此功能为可选，如果不需要可以删除
        if (location != null && (location.getLongitude() == null || location.getLatitude() == null)) {
            // 如果地点有名称但没有坐标，可以调用AmapUtil解析
            if (location.getName() != null && amapUtil != null) {
                Double[] coordinates = amapUtil.geocodeAddress(location.getName());
                if (coordinates != null) {
                    location.setLongitude(coordinates[0]);
                    location.setLatitude(coordinates[1]);
                }
            }
        }
        
        return location;
    }
    
    /**
     * 根据事件ID获取关联地点列表
     * 功能要求：通过事件表查询事件发生地点
     */
    @Cacheable(value = "locationsByEvent", key = "#eventId")
    public List<Location> getLocationsByEvent(String eventId) {
        return locationMapper.selectLocationsByEvent(eventId);
    }
    
    /**
     * 新增地点
     * 功能要求：新增地点记录
     */
    public int addLocation(Location location) {
        return locationMapper.insertLocation(location);
    }
    
    /**
     * 更新地点
     * 功能要求：根据ID更新地点记录
     */
    public int updateLocation(Location location) {
        return locationMapper.updateLocation(location);
    }
    
    /**
     * 删除地点
     * 功能要求：根据ID删除地点记录
     */
    public int deleteLocation(String id) {
        return locationMapper.deleteLocation(id);
    }
}

