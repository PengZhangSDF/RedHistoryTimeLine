-- 历史数据导入脚本
-- 
-- ========== 功能要求 ==========
-- 1. 导入历史事件数据
-- 2. 导入人物数据
-- 3. 导入地点数据（包含高德坐标）
-- 4. 导入媒体资源数据
-- 5. 建立事件与人物、地点的关联关系
-- 
-- ========== 修改范围限制 ==========
-- - 此文件由数据负责人维护
-- - 可以添加新的历史数据
-- - 禁止修改现有数据的ID和关键字段
-- - 其他开发者可以添加数据，但需确保数据准确性
-- 
-- ========== 执行要求 ==========
-- - 在init-db.sql执行后执行此脚本
-- - 执行命令：mysql -u root -p red_history < import-data.sql
-- 
-- ========== 数据格式要求 ==========
-- - 事件ID格式：event-001, event-002, ...
-- - 人物ID格式：person-001, person-002, ...
-- - 地点ID格式：loc-001, loc-002, ...
-- - 媒体ID格式：media-001, media-002, ...
-- - 日期格式：YYYY-MM-DD
-- - 坐标格式：经度在前，纬度在后

USE red_history;

-- 示例：插入事件数据
-- INSERT INTO events (id, title, date, category, description, background, impact, location_id) VALUES
-- ('event-001', '事件标题', '1931-09-18', '军事冲突', '事件描述', '历史背景', '历史影响', 'loc-001');

-- 示例：插入人物数据
-- INSERT INTO persons (id, name, role, description, birth_date, death_date) VALUES
-- ('person-001', '人物姓名', '角色', '人物描述', '1900-01-01', '2000-01-01');

-- 示例：插入地点数据（包含高德坐标）
-- INSERT INTO locations (id, name, province, longitude, latitude, description) VALUES
-- ('loc-001', '地点名称', '省份', 116.397428, 39.90923, '地点描述');

-- 示例：插入媒体资源数据
-- INSERT INTO media (id, type, url, title, event_id) VALUES
-- ('media-001', 'image', '/assets/images/event-001.jpg', '图片标题', 'event-001');

-- 示例：建立事件-人物关联
-- INSERT INTO event_person (event_id, person_id) VALUES
-- ('event-001', 'person-001');

-- 注意：实际数据需要根据历史资料填写
-- 坐标可以通过高德地图API获取

