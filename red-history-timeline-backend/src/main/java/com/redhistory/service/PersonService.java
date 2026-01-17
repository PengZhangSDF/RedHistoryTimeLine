package com.redhistory.service;

/**
 * 人物业务逻辑层
 * 
 * ========== 功能要求 ==========
 * 1. 处理人物相关的业务逻辑
 * 2. 关联查询人物与事件的关系
 * 3. 调用Mapper层进行数据库操作
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由人物功能负责人维护
 * - 禁止直接操作数据库
 * - 禁止调用其他Service（除非是关联查询）
 * - 可以优化业务逻辑
 * 
 * ========== 接口调用要求 ==========
 * - 必须调用 PersonMapper 进行数据库操作
 * 
 * ========== 函数关联要求 ==========
 * - 被 PersonController 调用
 * - 调用 PersonMapper
 */
import com.redhistory.mapper.PersonMapper;
import com.redhistory.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {
    
    @Autowired
    private PersonMapper personMapper;
    
    /**
     * 根据ID获取人物详情
     * 功能要求：根据人物ID查询完整人物信息
     */
    public Person getPersonById(String id) {
        return personMapper.selectPersonById(id);
    }
    
    /**
     * 根据事件ID获取关联人物列表
     * 功能要求：
     * - 通过关联表查询事件相关的人物
     * - 返回人物列表
     */
    public List<Person> getPersonsByEvent(String eventId) {
        return personMapper.selectPersonsByEvent(eventId);
    }
}

