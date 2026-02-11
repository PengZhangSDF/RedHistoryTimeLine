package com.redhistory.mapper;

import com.redhistory.model.Material;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 事件相关资料数据访问层（MyBatis Mapper 接口）
 *
 * ========== 功能要求 ==========
 * 1. 定义 materials 表的基础 CRUD 操作
 * 2. 支持根据事件 ID 查询资料列表
 *
 * ========== 修改范围限制 ==========
 * - 此文件由数据访问层负责人维护
 * - 禁止在接口中编写业务逻辑
 *
 * ========== 函数关联要求 ==========
 * - 被 MaterialService 调用
 * - 对应 MaterialMapper.xml 中的 SQL 语句
 */
@Mapper
public interface MaterialMapper {

    /**
     * 根据事件 ID 查询相关资料列表
     */
    List<Material> selectMaterialsByEvent(@Param("eventId") String eventId);

    /**
     * 新增资料
     */
    int insertMaterial(Material material);

    /**
     * 更新资料
     */
    int updateMaterial(Material material);

    /**
     * 删除资料
     */
    int deleteMaterial(@Param("id") String id);

    /**
     * 根据 ID 查询单条资料
     */
    Material selectMaterialById(@Param("id") String id);
}

