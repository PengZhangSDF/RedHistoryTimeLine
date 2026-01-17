# 开发指南

## 项目结构说明

本项目采用前后端分离架构，前端使用Vue3+Vite，后端使用Spring Boot+MyBatis。

## 文件修改范围限制

### 核心配置文件（禁止修改）
- `frontend/vite.config.js` - 由项目负责人维护
- `backend/pom.xml` - 由项目负责人维护
- `backend/src/main/resources/application.yml` - 由项目负责人维护

### 功能模块文件（需负责人审批）
- 各Controller、Service、Mapper文件 - 由对应功能负责人维护
- 各Vue组件文件 - 由对应功能负责人维护

### 工具类文件（可扩展，禁止修改核心逻辑）
- `utils/request.js` - API负责人维护
- `utils/amapUtils.js` - 地图功能负责人维护
- `util/ResponseUtil.java` - 工具类负责人维护

## 开发流程

1. 从dev分支创建功能分支
2. 开发完成后请先更新项目后合并项目
3. 代码审查通过后不要自行合并

## 接口调用规范

- 前端必须通过API文件调用后端接口
- 后端Controller必须使用ResponseUtil格式化响应
- 所有接口响应格式统一

