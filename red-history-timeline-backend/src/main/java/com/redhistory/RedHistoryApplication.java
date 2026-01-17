package com.redhistory;

/**
 * Spring Boot主启动类
 * 
 * ========== 功能要求 ==========
 * 1. Spring Boot应用的入口点
 * 2. 启动内嵌Tomcat服务器
 * 3. 初始化Spring容器和MyBatis
 * 4. 输出启动信息
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由项目负责人维护
 * - 禁止修改Spring Boot应用初始化逻辑
 * - 禁止添加业务逻辑
 * - 如需添加全局配置，使用@Configuration类
 * - 其他开发者禁止修改此文件
 * 
 * ========== 接口调用要求 ==========
 * - 不调用任何API
 * - 不处理业务逻辑
 * - 仅负责应用启动
 * 
 * ========== 函数关联要求 ==========
 * - 被Maven/IDE启动
 * - 自动扫描com.redhistory包下的所有组件
 * - 加载application.yml配置
 * - 初始化MyBatis
 * 
 * ========== 运行方式 ==========
 * - 开发环境：mvn spring-boot:run
 * - 生产环境：java -jar red-history-timeline-backend-1.0.0.jar
 */
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RedHistoryApplication {
    public static void main(String[] args) {
        SpringApplication.run(RedHistoryApplication.class, args);
        System.out.println("========================================");
        System.out.println("后端服务器运行在 http://localhost:3001");
        System.out.println("API文档: http://localhost:3001/api");
        System.out.println("========================================");
    }
}

