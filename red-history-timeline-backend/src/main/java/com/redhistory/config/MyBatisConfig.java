package com.redhistory.config;

/**
 * MyBatis配置类
 * 
 * ========== 功能要求 ==========
 * 1. 配置MyBatis的Mapper扫描路径
 * 2. 配置MyBatis的其他设置（如驼峰命名转换等）
 * 3. 配置数据库连接池等（可选，通常在application.yml中配置）
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由数据访问层负责人维护
 * - 禁止修改Mapper扫描路径（com.redhistory.mapper）
 * - 禁止修改核心配置
 * - 如需添加MyBatis插件（如分页插件），需团队讨论
 * - 其他开发者禁止修改此文件
 * 
 * ========== 接口调用要求 ==========
 * - 不调用任何API
 * - 配置类，在应用启动时生效
 * 
 * ========== 函数关联要求 ==========
 * - 被Spring Boot自动加载
 * - 扫描com.redhistory.mapper包下的所有Mapper接口
 * - 与application.yml中的MyBatis配置配合使用
 */
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.redhistory.mapper")
public class MyBatisConfig {
    // 功能要求：MyBatis配置已通过application.yml完成
    // 这里只需要配置Mapper扫描路径
    // 其他配置（如数据库连接、映射文件路径等）在application.yml中
}

