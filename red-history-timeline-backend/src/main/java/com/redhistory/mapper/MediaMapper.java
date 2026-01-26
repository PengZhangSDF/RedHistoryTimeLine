package com.redhistory.mapper;

/**
 * 媒体资源数据访问层（MyBatis Mapper接口）
 * 
 * ========== 功能要求 ==========
 * 1. 定义媒体资源相关的数据库操作方法
 * 2. 与MediaMapper.xml中的SQL语句对应
 * 3. 提供媒体资源查询功能（支持类型过滤）
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由数据访问层负责人维护
 * - 禁止修改方法签名
 * - 禁止在接口中添加业务逻辑
 * 
 * ========== 函数关联要求 ==========
 * - 被 MediaService 调用
 * - 对应 MediaMapper.xml 中的SQL语句
 * - 操作 media 表
 */
import com.redhistory.model.Media;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MediaMapper {
    /**
     * 根据事件ID查询关联媒体资源（支持类型过滤）
     * 功能要求：
     * - 查询事件关联的所有媒体资源
     * - 如果type不为null，按类型筛选
     * - 按类型和ID排序
     */
    List<Media> selectMediaByEvent(
        @Param("eventId") String eventId,
        @Param("type") String type
    );
    
    /**
     * 插入媒体
     * 功能要求：新增媒体记录
     */
    int insertMedia(Media media);
    
    /**
     * 更新媒体
     * 功能要求：根据ID更新媒体记录
     */
    int updateMedia(Media media);
    
    /**
     * 删除媒体
     * 功能要求：根据ID删除媒体记录
     */
    int deleteMedia(@Param("id") String id);
    
    /**
     * 根据ID查询媒体详情
     * 功能要求：根据媒体ID查询完整的媒体信息
     */
    Media selectMediaById(@Param("id") String id);
}

