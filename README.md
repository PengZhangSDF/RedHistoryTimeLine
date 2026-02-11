# 抗日战争时间轴项目

## 一、项目简介
本项目是一个面向学习与展示的 **抗日战争历史可视化应用**，通过时间轴、地图与多媒体资料，将 1931–1945 年间的重要历史事件以直观的方式展现出来。

主要特性：

- 首页左侧 **竖直时间轴** 展示 20 个核心抗战事件，可缩放时间间距、点击跳转详情，并与地图联动滚动定位；
- 右侧 **高德地图** 展示事件发生地点，支持点击地图钉或钉上方事件预览图：
  - 点击地图钉 → 时间轴平滑滚动到对应事件；
  - 点击钉上方事件图片 → 进入事件详情页；
- 详情页包含：事件全文、地图定位、相关人物、多媒体资料（本地图片 + B 站视频）以及权威外部资料链接。

## 二、技术栈

- **前端**：Vue 3、Vite、Vue Router、Sass、Axios、`@amap/amap-jsapi-loader`
- **后端**：Spring Boot、MyBatis、MySQL
- **地图**：高德地图 JavaScript API 2.x

## 三、运行与数据初始化（简要）

详细步骤请参考根目录的 `使用指南.md`，这里仅列出关键点：

1. **初始化数据库结构**

   ```sql
   SOURCE red-history-timeline-backend/scripts/init-db.sql;
   ```

   该脚本会创建以下核心表：

   - `events`：抗战事件；
   - `persons`：人物；
   - `locations`：地点（含高德坐标）；
   - `media`：多媒体资源（图片 / 视频 / 音频）；
   - `event_person`：事件–人物关联；
   - `materials`：事件相关资料（外部权威链接）。

2. **导入基础数据**

   推荐使用从 JSON 生成的脚本（字段更完整）：

   ```sql
   SET NAMES utf8mb4;
   SOURCE red-history-timeline-backend/scripts/import-data-from-json.sql;
   ```

3. **导入 B 站多媒体视频（映像资料）**

   所有 B 站链接来自 `data/vedio.txt`，已整理为 19 条记录，对应 `E001–E020`：

   ```sql
   SET NAMES utf8mb4;
   SOURCE red-history-timeline-backend/scripts/import-media-bilibili-9.sql;
   ```

4. **导入“相关资料”外部权威链接**

   所有链接来自 `data/相关资料.md`，按事件 ID 写入 `materials` 表：

   ```sql
   SET NAMES utf8mb4;
   SOURCE red-history-timeline-backend/scripts/import-materials-from-md.sql;
   ```

5. **运行服务**

   - 后端：

     ```bash
     cd red-history-timeline-backend
     mvn spring-boot:run
     ```

   - 前端：

     ```bash
     cd red-history-timeline-frontend
     npm install
     npm run dev
     ```

   默认前端端口为 `http://localhost:3000/`，后端为 `http://localhost:3001/`（见 `application.yml`）。

## 四、前后端结构与模块说明

- **整体结构与引用关系**：见根目录的 `Struct.md`，其中详细说明了：
  - 前端 `Home.vue`（首页）、`EventDetail.vue`（详情页）如何整合时间轴、地图、多媒体和相关资料组件；
  - 后端 `EventController` / `MediaController` / `MaterialController` 等控制器与 MyBatis Mapper 的对应关系；
  - 时间轴与地图之间的联动（地图钉 → 时间轴滚动、时间轴事件 → 地图聚焦）。

## 五、其他文档

- `使用指南.md`：从零搭建数据库、启动前后端的完整指南（包括编码问题、脚本执行顺序等）。
- `现状.md`：功能完成情况及待开发项说明。
- `Struct.md`：项目结构与引用关系详细说明。
- `图片显示问题说明.md`：事件图片命名规范（目前统一为 `.png`）、存放路径以及占位图逻辑说明。
