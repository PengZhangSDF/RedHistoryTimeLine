package com.redhistory.mapper;

/**
 * 事件数据访问层（MyBatis Mapper接口）
 * 
 * ========== 功能要求 ==========
 * 1. 定义事件相关的数据库操作方法
 * 2. 与EventMapper.xml中的SQL语句对应
 * 3. 提供事件数据的增删改查功能
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由数据访问层负责人维护
 * - 禁止修改方法签名（参数和返回值）
 * - 禁止在接口中添加业务逻辑
 * - 如需添加新方法，需先确认SQL已编写
 * - 其他开发者如需添加方法，需提交PR
 * 
 * ========== 接口调用要求 ==========
 * - 方法名必须与EventMapper.xml中的SQL ID对应
 * - 参数使用@Param注解标注
 * - 返回值类型必须与XML中的resultType对应
 * 
 * ========== 函数关联要求 ==========
 * - 被 EventService 调用
 * - 对应 EventMapper.xml 中的SQL语句
 * - 操作 events 表
 * 
 * ========== SQL映射文件 ==========
 * - 对应的XML文件：src/main/resources/mybatis/EventMapper.xml
 * - 方法名必须与XML中的SQL ID一致
 */
import com.redhistory.model.Event;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EventMapper {
    /**
     * 查询事件列表（支持筛选）
     * 功能要求：
     * - 支持按时间范围筛选（startDate, endDate）
     * - 支持按地点筛选（locationId）
     * - 按日期排序
     * 
     * 修改限制：
     * - 禁止修改方法签名
     * - SQL语句在EventMapper.xml中定义
     */
    List<Event> selectEventList(
        @Param("startDate") String startDate,
        @Param("endDate") String endDate,
        @Param("locationId") String locationId
    );
    
    /**
     * 根据ID查询事件详情
     * 功能要求：根据事件ID查询完整事件信息
     * 
     * 修改限制：
     * - 禁止修改方法签名
     * - SQL语句在EventMapper.xml中定义
     */
    Event selectEventById(@Param("id") String id);
    
    /**
     * 根据地点ID查询事件列表
     * 功能要求：查询指定地点发生的所有事件，按时间排序
     * 
     * 修改限制：
     * - 禁止修改方法签名
     * - SQL语句在EventMapper.xml中定义
     */
    List<Event> selectEventsByLocation(@Param("locationId") String locationId);
}

