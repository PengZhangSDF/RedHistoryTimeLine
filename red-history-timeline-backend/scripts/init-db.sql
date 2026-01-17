-- 数据库初始化脚本
-- 
-- ========== 功能要求 ==========
-- 1. 创建数据库和表结构
-- 2. 定义表之间的关系（外键）
-- 3. 创建索引以提升查询性能
-- 
-- ========== 修改范围限制 ==========
-- - 此文件由数据库负责人维护
-- - 禁止修改表名和字段名（除非需求变更）
-- - 禁止修改主键和外键定义
-- - 可以添加新字段，但需团队讨论
-- - 其他开发者如需修改，需提交PR
-- 
-- ========== 执行要求 ==========
-- - 首次部署时执行此脚本
-- - 执行前需确保MySQL服务已启动
-- - 执行命令：mysql -u root -p < init-db.sql

-- 创建数据库
CREATE DATABASE IF NOT EXISTS red_history DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE red_history;

-- 事件表
CREATE TABLE IF NOT EXISTS events (
    id VARCHAR(50) PRIMARY KEY COMMENT '事件唯一标识',
    title VARCHAR(200) NOT NULL COMMENT '事件标题',
    date DATE NOT NULL COMMENT '事件日期',
    category VARCHAR(50) COMMENT '事件类别',
    description TEXT COMMENT '事件描述',
    background TEXT COMMENT '历史背景',
    process TEXT COMMENT '发展经过',
    impact TEXT COMMENT '历史影响',
    location_id VARCHAR(50) COMMENT '关联地点ID',
    INDEX idx_date (date) COMMENT '日期索引',
    INDEX idx_location (location_id) COMMENT '地点索引',
    INDEX idx_category (category) COMMENT '类别索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='历史事件表';

-- 人物表
CREATE TABLE IF NOT EXISTS persons (
    id VARCHAR(50) PRIMARY KEY COMMENT '人物唯一标识',
    name VARCHAR(100) NOT NULL COMMENT '人物姓名',
    role VARCHAR(100) COMMENT '人物角色',
    description TEXT COMMENT '人物简介',
    birth_date DATE COMMENT '出生日期',
    death_date DATE COMMENT '逝世日期',
    INDEX idx_name (name) COMMENT '姓名索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='历史人物表';

-- 地点表
CREATE TABLE IF NOT EXISTS locations (
    id VARCHAR(50) PRIMARY KEY COMMENT '地点唯一标识',
    name VARCHAR(100) NOT NULL COMMENT '地点名称',
    province VARCHAR(50) COMMENT '省份',
    longitude DOUBLE COMMENT '经度（高德坐标）',
    latitude DOUBLE COMMENT '纬度（高德坐标）',
    description TEXT COMMENT '地点描述',
    INDEX idx_name (name) COMMENT '名称索引',
    INDEX idx_coordinates (longitude, latitude) COMMENT '坐标索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='历史地点表';

-- 媒体资源表
CREATE TABLE IF NOT EXISTS media (
    id VARCHAR(50) PRIMARY KEY COMMENT '媒体资源唯一标识',
    type VARCHAR(20) NOT NULL COMMENT '媒体类型（image/video/audio）',
    url VARCHAR(500) NOT NULL COMMENT '媒体资源链接',
    title VARCHAR(200) COMMENT '媒体标题',
    event_id VARCHAR(50) COMMENT '关联事件ID',
    INDEX idx_event (event_id) COMMENT '事件索引',
    INDEX idx_type (type) COMMENT '类型索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='媒体资源表';

-- 事件-人物关联表
CREATE TABLE IF NOT EXISTS event_person (
    event_id VARCHAR(50) COMMENT '事件ID',
    person_id VARCHAR(50) COMMENT '人物ID',
    PRIMARY KEY (event_id, person_id),
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    FOREIGN KEY (person_id) REFERENCES persons(id) ON DELETE CASCADE,
    INDEX idx_event (event_id),
    INDEX idx_person (person_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='事件-人物关联表';

