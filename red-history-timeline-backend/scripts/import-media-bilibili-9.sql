-- 将部分抗战事件的 B 站多媒体资料写入 media 表
-- 执行方式（已初始化数据库后）：
--   mysql -u root -p red_history < scripts/import-media-bilibili-9.sql

USE red_history;
SET NAMES utf8mb4;

INSERT INTO media (id, type, url, title, event_id) VALUES
('media-bv-001', 'video', 'https://www.bilibili.com/video/BV1TFF5egEMs/', '九一八事变 B站资料', 'E001'),
('media-bv-002', 'video', 'https://www.bilibili.com/video/BV1eE421u7ua/', '一二八事变 B站资料', 'E002'),
('media-bv-003', 'video', 'https://www.bilibili.com/video/BV1WDi1YAEKK/', '伪满洲国建立 B站资料', 'E003'),
('media-bv-004', 'video', 'https://www.bilibili.com/video/BV1kYo9YuEke/', '长城抗战 B站资料', 'E004'),
('media-bv-005', 'video', 'https://www.bilibili.com/video/BV1jf4y1K7yJ/', '一二九运动 B站资料', 'E006'),
('media-bv-006', 'video', 'https://www.bilibili.com/video/BV16DfKYuE4Z/', '瓦窑堡会议 B站资料', 'E007'),
('media-bv-007', 'video', 'https://www.bilibili.com/video/BV1eTtvzkEZn/', '西安事变 B站资料', 'E008'),
('media-bv-008', 'video', 'https://www.bilibili.com/video/BV1NpYuzNEqP',  '七七事变 B站资料', 'E009'),
('media-bv-009', 'video', 'https://www.bilibili.com/video/BV1A2sezjELz',  '淞沪会战 B站资料', 'E010'),
('media-bv-010', 'video', 'https://www.bilibili.com/video/BV1LV411k7FH',  '平型关大捷 B站资料', 'E011'),
('media-bv-011', 'video', 'https://www.bilibili.com/video/BV1ZbmXBFEod',  '南京大屠杀 B站资料', 'E012'),
('media-bv-012', 'video', 'https://www.bilibili.com/video/BV1Czk3BCE9t',  '台儿庄战役 B站资料', 'E013'),
('media-bv-013', 'video', 'https://www.bilibili.com/video/BV1zX3KzhEWF',  '徐州会战 B站资料', 'E014'),
('media-bv-014', 'video', 'https://www.bilibili.com/video/BV19QokY4ELz',  '武汉会战 B站资料', 'E015'),
('media-bv-015', 'video', 'https://www.bilibili.com/video/BV1wSmqBhEoA',  '百团大战 B站资料', 'E016'),
('media-bv-016', 'video', 'https://www.bilibili.com/video/BV1srAgeiEHP',  '皖南事变 B站资料', 'E017'),
('media-bv-017', 'video', 'https://www.bilibili.com/video/BV14QQ2YLEjX',  '延安整风运动 B站资料', 'E018'),
('media-bv-018', 'video', 'https://www.bilibili.com/video/BV1hP411L7u4/', '豫湘桂战役 B站资料', 'E019'),
('media-bv-019', 'video', 'https://www.bilibili.com/video/BV1hqbHzBEs5',  '日本无条件投降 B站资料', 'E020');

