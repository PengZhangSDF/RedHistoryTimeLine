package com.redhistory.service;

/**
 * 事件业务逻辑层
 * 
 * ========== 功能要求 ==========
 * 1. 处理事件相关的业务逻辑
 * 2. 关联查询人物、地点、媒体等数据（如需要）
 * 3. 数据过滤和排序
 * 4. 调用Mapper层进行数据库操作
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由事件功能负责人维护
 * - 禁止直接操作数据库（必须通过Mapper）
 * - 禁止调用其他Service（除非是关联查询，需团队讨论）
 * - 可以优化业务逻辑和性能
 * - 可以添加缓存机制
 * - 其他开发者如需修改，需提交PR
 * 
 * ========== 接口调用要求 ==========
 * - 必须调用 EventMapper 进行数据库操作
 * - 不直接调用其他Mapper（除非是关联查询）
 * - 可以调用其他Service进行关联查询（需团队讨论）
 * 
 * ========== 函数关联要求 ==========
 * - 被 EventController 调用
 * - 调用 EventMapper（数据访问层）
 * - 不直接操作数据库
 * - 不直接返回数据库对象（可以转换为DTO，当前直接返回Model）
 */
import com.redhistory.mapper.EventMapper;
import com.redhistory.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {
    
    @Autowired
    private EventMapper eventMapper;
    
    /**
     * 获取事件列表（支持筛选）
     * 功能要求：
     * - 支持按时间范围筛选
     * - 支持按地点筛选
     * - 返回排序后的事件列表
     * 
     * 修改限制：
     * - 禁止修改Mapper调用方式
     * - 可以优化筛选逻辑
     * - 可以添加缓存
     */
    public List<Event> getEventList(String startDate, String endDate, String locationId) {
        // 功能要求：必须调用Mapper层
        // 修改限制：禁止在Service中编写SQL
        return eventMapper.selectEventList(startDate, endDate, locationId);
    }
    
    /**
     * 根据ID获取事件详情
     * 功能要求：
     * - 根据事件ID查询完整事件信息
     * - 如果事件不存在，返回null
     * 
     * 修改限制：
     * - 禁止修改Mapper调用方式
     * - 可以添加关联查询（如关联人物、地点、媒体）
     */
    public Event getEventById(String id) {
        // 功能要求：必须调用Mapper层
        // 修改限制：禁止直接查询数据库
        return eventMapper.selectEventById(id);
    }
    
    /**
     * 根据地点ID获取事件列表
     * 功能要求：
     * - 获取指定地点发生的所有事件
     * - 按时间排序
     * 
     * 修改限制：
     * - 禁止修改Mapper调用方式
     */
    public List<Event> getEventsByLocation(String locationId) {
        // 功能要求：必须调用Mapper层
        return eventMapper.selectEventsByLocation(locationId);
    }
}

