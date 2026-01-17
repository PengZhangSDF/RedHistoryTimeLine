package com.redhistory.controller;

/**
 * 人物控制器
 * 
 * ========== 功能要求 ==========
 * 1. 处理与历史人物相关的HTTP请求
 * 2. 提供人物详情、关联事件等接口
 * 
 * ========== API接口列表 ==========
 * - GET /api/persons/{id} - 获取人物详情
 *   * 参数：id(路径参数)
 *   * 返回：{ code: 200, data: Person }
 * 
 * - GET /api/persons/event/{eventId} - 根据事件ID获取关联人物
 *   * 参数：eventId(路径参数)
 *   * 返回：{ code: 200, data: List<Person>, total: number }
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由人物功能负责人维护
 * - 禁止修改接口路径和HTTP方法
 * - 禁止修改参数格式和返回值格式
 * - 禁止在Controller中添加业务逻辑
 * - 其他开发者如需添加新接口，需先确认需求并提交PR
 * 
 * ========== 接口调用要求 ==========
 * - 必须调用 PersonService 处理业务逻辑
 * - 必须使用 ResponseUtil 格式化响应
 * 
 * ========== 函数关联要求 ==========
 * - 被前端 personApi.js 调用
 * - 调用 PersonService
 * - 调用 ResponseUtil
 */
import com.redhistory.model.Person;
import com.redhistory.service.PersonService;
import com.redhistory.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/persons")
@CrossOrigin(origins = "*")
public class PersonController {
    
    @Autowired
    private PersonService personService;
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPersonDetail(@PathVariable String id) {
        try {
            Person person = personService.getPersonById(id);
            if (person != null) {
                return ResponseEntity.ok(ResponseUtil.success(person));
            } else {
                return ResponseEntity.ok(ResponseUtil.error("人物未找到"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(ResponseUtil.error("获取人物详情失败：" + e.getMessage()));
        }
    }
    
    @GetMapping("/event/{eventId}")
    public ResponseEntity<Map<String, Object>> getPersonsByEvent(@PathVariable String eventId) {
        try {
            List<Person> persons = personService.getPersonsByEvent(eventId);
            return ResponseEntity.ok(ResponseUtil.success(persons, persons.size()));
        } catch (Exception e) {
            return ResponseEntity.ok(ResponseUtil.error("获取人物列表失败：" + e.getMessage()));
        }
    }
}

