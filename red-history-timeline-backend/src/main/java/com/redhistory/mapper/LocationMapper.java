package com.redhistory.mapper;

/**
 * 地点数据访问层（MyBatis Mapper接口）
 * 
 * ========== 功能要求 ==========
 * 1. 定义地点相关的数据库操作方法
 * 2. 与LocationMapper.xml中的SQL语句对应
 * 3. 提供地点坐标查询功能
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由数据访问层负责人维护
 * - 禁止修改方法签名
 * - 禁止在接口中添加业务逻辑
 * 
 * ========== 函数关联要求 ==========
 * - 被 LocationService 调用
 * - 对应 LocationMapper.xml 中的SQL语句
 * - 操作 locations 表
 */
import com.redhistory.model.Location;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LocationMapper {
    /**
     * 查询所有地点列表（包含坐标）
     * 功能要求：返回所有地点数据，用于首页地图标记
     */
    List<Location> selectAllLocations();
    
    /**
     * 根据ID查询地点详情（包含高德坐标）
     * 功能要求：返回地点完整信息，包括longitude和latitude
     */
    Location selectLocationById(@Param("id") String id);
    
    /**
     * 根据事件ID查询关联地点列表
     * 功能要求：通过events表的location_id关联查询
     */
    List<Location> selectLocationsByEvent(@Param("eventId") String eventId);
    
    /**
     * 插入地点
     * 功能要求：新增地点记录
     */
    int insertLocation(Location location);
    
    /**
     * 更新地点
     * 功能要求：根据ID更新地点记录
     */
    int updateLocation(Location location);
    
    /**
     * 删除地点
     * 功能要求：根据ID删除地点记录
     */
    int deleteLocation(@Param("id") String id);
}

