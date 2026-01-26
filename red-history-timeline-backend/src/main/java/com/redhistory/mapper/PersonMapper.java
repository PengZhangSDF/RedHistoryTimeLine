package com.redhistory.mapper;

/**
 * 人物数据访问层（MyBatis Mapper接口）
 * 
 * ========== 功能要求 ==========
 * 1. 定义人物相关的数据库操作方法
 * 2. 与PersonMapper.xml中的SQL语句对应
 * 3. 提供人物数据的查询功能
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由数据访问层负责人维护
 * - 禁止修改方法签名
 * - 禁止在接口中添加业务逻辑
 * 
 * ========== 函数关联要求 ==========
 * - 被 PersonService 调用
 * - 对应 PersonMapper.xml 中的SQL语句
 * - 操作 persons 表和 event_person 关联表
 */
import com.redhistory.model.Person;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PersonMapper {
    /**
     * 根据ID查询人物详情
     * 功能要求：根据人物ID查询完整人物信息
     */
    Person selectPersonById(@Param("id") String id);
    
    /**
     * 根据事件ID查询关联人物列表
     * 功能要求：
     * - 通过event_person关联表查询
     * - 返回事件相关的所有人物
     */
    List<Person> selectPersonsByEvent(@Param("eventId") String eventId);
    
    /**
     * 插入人物
     * 功能要求：新增人物记录
     */
    int insertPerson(Person person);
    
    /**
     * 更新人物
     * 功能要求：根据ID更新人物记录
     */
    int updatePerson(Person person);
    
    /**
     * 删除人物
     * 功能要求：根据ID删除人物记录
     */
    int deletePerson(@Param("id") String id);
}

