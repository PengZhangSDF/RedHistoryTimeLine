package com.redhistory.service;

import com.redhistory.mapper.MaterialMapper;
import com.redhistory.model.Material;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 事件相关资料业务逻辑层
 *
 * ========== 功能要求 ==========
 * 1. 提供根据事件 ID 查询相关资料的能力
 * 2. 为详情页“相关资料”模块提供数据支持
 *
 * ========== 修改范围限制 ==========
 * - 此文件由资料功能负责人维护
 * - 禁止直接操作数据库，必须通过 Mapper
 */
@Service
public class MaterialService {

    @Autowired
    private MaterialMapper materialMapper;

    /**
     * 根据事件 ID 获取相关资料列表
     */
    @Cacheable(value = "materialsByEvent", key = "#eventId")
    public List<Material> getMaterialsByEvent(String eventId) {
        return materialMapper.selectMaterialsByEvent(eventId);
    }

    public Material getMaterialById(String id) {
        return materialMapper.selectMaterialById(id);
    }

    public int addMaterial(Material material) {
        return materialMapper.insertMaterial(material);
    }

    public int updateMaterial(Material material) {
        return materialMapper.updateMaterial(material);
    }

    public int deleteMaterial(String id) {
        return materialMapper.deleteMaterial(id);
    }
}

