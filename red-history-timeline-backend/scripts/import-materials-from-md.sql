-- 从 data/相关资料.md 抽取的权威资料链接
-- 将这些资料按事件写入 materials 表
-- 执行前请确认已执行 init-db.sql 并存在 red_history 库
-- 执行方式示例：
--   mysql -u root -p red_history < red-history-timeline-backend/scripts/import-materials-from-md.sql

USE red_history;
SET NAMES utf8mb4;

-- 九一八事变（E001）
INSERT INTO materials (id, event_id, title, url, type) VALUES
('mat-001-01', 'E001', '九一八事变：日本蓄谋侵华与张学良不抵抗政策', 'http://politics.people.com.cn/n/2015/0824/c1001-27506445.html', '新闻报道'),
('mat-001-02', 'E001', '在这里，九一八事变爆发', 'http://www.xinhuanet.com/local/2023-09/17/c_1129867942.htm', '新闻报道'),
('mat-001-03', 'E001', '“九一八”事变84周年：带着历史记忆向未来出发', 'https://china.huanqiu.com/article/9CaKrnJPzaX', '新闻报道');

-- 一・二八事变（E002）
INSERT INTO materials (id, event_id, title, url, type) VALUES
('mat-002-01', 'E002', '1932年1月28日：一·二八淞沪抗战', 'https://www.saac.gov.cn/zt/2015-01/28/content_80397.htm', '历史文献'),
('mat-002-02', 'E002', '一·二八抗战', 'https://www.zgbk.com/ecph/words?SiteID=1&ID=497220&Type=bkzyb&SubID=194626', '百科条目'),
('mat-002-03', 'E002', '抗战烽火：一·二八淞沪抗战[组图]', 'https://news.cctv.com/special/C18851/20070706/104101.shtml', '图片资料');

-- 伪满洲国建立（E003）
INSERT INTO materials (id, event_id, title, url, type) VALUES
('mat-003-01', 'E003', '伪满“建国”丑剧出台始末：大鬼小鬼粉墨来', 'http://www.chinanews.com.cn/cul/news/2010/05-26/2306134.shtml', '新闻报道'),
('mat-003-02', 'E003', '东北沦陷与东北人民的英勇斗争', 'http://dangshi.people.com.cn/n/2015/0526/c396421-27056835-2.html', '历史文献'),
('mat-003-03', 'E003', '“溥仪及其时代”研究数据库导航', 'https://www.wmhg.com.cn/single/detail/1887.html', '官方网站');

-- 长城抗战（E004）
INSERT INTO materials (id, event_id, title, url, type) VALUES
('mat-004-01', 'E004', '长城抗战：大刀向鬼子们的头上砍去!', 'http://cpc.people.com.cn/n1/2018/1213/c421684-30464471.html', '历史文献'),
('mat-004-02', 'E004', '长城抗战：救国壮举 永载史册', 'http://dangshi.people.com.cn/n1/2018/1226/c85037-30487984.html', '历史文献'),
('mat-004-03', 'E004', '珍档·记忆——长城抗战(一)', 'https://www.cddaw.cn/art/2025/5/12/art_13090_1085769.html', '档案资料');

-- 一二・九运动（E006）
INSERT INTO materials (id, event_id, title, url, type) VALUES
('mat-006-01', 'E006', '一二·九运动', 'https://cpc.people.com.cn/GB/33837/2534309.html', '历史文献'),
('mat-006-02', 'E006', '90年前的今天，“一二·九”运动掀起抗日救国新高潮', 'http://www.xinhuanet.com/politics/20251209/dfc8a335b8014d7cb9178907c88c2154/c.html', '新闻报道'),
('mat-006-03', 'E006', '一二·九运动', 'https://www.dswxyjy.org.cn/n/2013/1210/c244520-23798510.html', '历史文献');

-- 瓦窑堡会议（E007）
INSERT INTO materials (id, event_id, title, url, type) VALUES
('mat-007-01', 'E007', '瓦窑堡会议', 'https://cpc.people.com.cn/GB/33837/2534314.html', '历史文献'),
('mat-007-02', 'E007', '瓦窑堡会议：“一次极关重要的会议”', 'http://www.qstheory.cn/zhuanqu/2021-04/28/c_1127387158.htm', '历史文献'),
('mat-007-03', 'E007', '瓦窑堡会议', 'https://www.dswxyjy.org.cn/n/2013/1217/c244520-23862655.html', '历史文献');

-- 西安事变（E008）
INSERT INTO materials (id, event_id, title, url, type) VALUES
('mat-008-01', 'E008', '西安事变及其和平解决', 'http://cpc.people.com.cn/GB/64107/65708/65722/4445224.html', '历史文献'),
('mat-008-02', 'E008', '【百个瞬间说百年】1936，“停止内战，一致抗日”', 'http://www.xinhuanet.com/politics/2021-04/13/c_1211107824.htm', '新闻报道'),
('mat-008-03', 'E008', '西安事变及其和平解决', 'http://dangshi.people.com.cn/n/2015/0526/c396421-27057784.html', '历史文献');

-- 七七事变（卢沟桥事变，E009）
INSERT INTO materials (id, event_id, title, url, type) VALUES
('mat-009-01', 'E009', '七七事变:中国全民族抗战的开端', 'https://www.xinhuanet.com/politics/2021-02/18/c_1127111693.htm', '新闻报道'),
('mat-009-02', 'E009', '七七事变:全民族抗战的开端', 'http://cpc.people.com.cn/BIG5/n1/2017/0707/c64094-29388880.html', '历史文献'),
('mat-009-03', 'E009', '卢沟桥事变和全国抗战的开始', 'https://www.dswxyjy.org.cn/n/2014/0706/c244520-25244235.html', '历史文献');

-- 淞沪会战（E010）
INSERT INTO materials (id, event_id, title, url, type) VALUES
('mat-010-01', 'E010', '“八一三”淞沪会战:以血荐轩辕', 'http://cpc.people.com.cn/BIG5/n1/2018/1031/c64094-30373038.html', '历史文献'),
('mat-010-02', 'E010', '淞沪会战:粉碎日军“三月亡华”妄想', 'http://dangshi.people.com.cn/n/2015/0831/c85037-27535274.html', '历史文献'),
('mat-010-03', 'E010', '八一三抗战', 'http://www.813china.com/index.php?m=content&c=index&a=lists&catid=40', '官方网站');

-- 平型关大捷（E011）
INSERT INTO materials (id, event_id, title, url, type) VALUES
('mat-011-01', 'E011', '平型关战役:打破“日军不可战胜”的神话', 'http://cpc.people.com.cn/n1/2018/1126/c421684-30421289.html', '历史文献'),
('mat-011-02', 'E011', '平型关大捷:八路军战士抱日军指挥官滚下山崖', 'http://www.xinhuanet.com/mil/2015-06/20/c_127934913.htm', '新闻报道'),
('mat-011-03', 'E011', '平型关大捷威名远扬', 'http://www.81.mil.cn/rmjz_203219/zgmb/2025nd3q/shxy_250533/16374678.html', '历史文献');

-- 南京大屠杀（E012）
INSERT INTO materials (id, event_id, title, url, type) VALUES
('mat-012-01', 'E012', '南京大屠杀史实', 'http://www.cngongji.cn/news/indexwap.htm', '官方网站'),
('mat-012-02', 'E012', '长明火不熄，人类记忆不灭!', 'http://www.19371213.org.cn/information/news/202005/t20200519_2233064.html', '官方网站'),
('mat-012-03', 'E012', '中共中央、国务院在南京举行2025年南京大屠杀死难者国家公祭仪式', 'https://www.gov.cn/yaowen/liebiao/202512/content_7051090.htm', '新闻报道');

-- 台儿庄战役（E013）
INSERT INTO materials (id, event_id, title, url, type) VALUES
('mat-013-01', 'E013', '台儿庄大捷', 'http://politics.people.com.cn/n/2014/0906/c1001-25614704.html', '历史文献'),
('mat-013-02', 'E013', '铭记历史 缅怀先烈|血战台儿庄 激战禹王山——徐州会战回眸', 'http://www.xinhuanet.com/mil/2025/07/25/c_1292034425.htm', '新闻报道'),
('mat-013-03', 'E013', '铭记历史 砥砺前行', 'http://www.tezwar.com/newview/?id=339', '官方网站');

-- 徐州会战（E014）
INSERT INTO materials (id, event_id, title, url, type) VALUES
('mat-014-01', 'E014', '徐州会战', 'https://www.chinanews.com.cn/mil/2014/02-25/5881377.shtml', '新闻报道'),
('mat-014-02', 'E014', '铭记历史 缅怀先烈 | 战斗至最后一刻 血肉之躯筑起钢铁防线', 'https://news.cctv.cn/2025/08/13/ARTITd7GKpS0D36SOCoGHHg1250813.shtml', '新闻报道'),
('mat-014-03', 'E014', '抗战战略防御阶段的结束', 'http://www.81.cn/jwzl/2015-09/23/content_6695521.htm', '历史文献');

-- 武汉会战（E015）
INSERT INTO materials (id, event_id, title, url, type) VALUES
('mat-015-01', 'E015', '铭记历史 缅怀先烈丨武汉会战:中国抗战的战略转折', 'http://www.xinhuanet.com/politics/2025/07/29/c_1292034425.htm', '新闻报道'),
('mat-015-02', 'E015', '武汉会战', 'http://military.people.com.cn/n/2014/0908/c1011-25619695.html', '历史文献'),
('mat-015-03', 'E015', '武汉会战:中国军人牺牲40万将抗战拖入持久战', 'https://www.chinanews.com.cn/mil/2015/06-22/7358796.shtml', '新闻报道');

-- 百团大战（E016）
INSERT INTO materials (id, event_id, title, url, type) VALUES
('mat-016-01', 'E016', '百团大战:举世瞩目的战略性进攻', 'http://cpc.people.com.cn/BIG5/n1/2025/0807/c443712-40537793.html', '历史文献'),
('mat-016-02', 'E016', '百团大战:八路军在华北发动的最大进攻战役', 'http://www.mod.gov.cn/gfbw/gfjy_index/16400432.html', '历史文献'),
('mat-016-03', 'E016', '“百团大战”档案', 'http://www.81.mil.cn/yljnt_208627/lsda_208656/16420475.html', '档案资料');

-- 皖南事变（E017）
INSERT INTO materials (id, event_id, title, url, type) VALUES
('mat-017-01', 'E017', '皖南事变', 'http://cpc.people.com.cn/GB/33837/2534539.html', '历史文献'),
('mat-017-02', 'E017', '皖南事变', 'http://www.81.cn/jwzl/2016-01/18/content_6860899.htm', '历史文献'),
('mat-017-03', 'E017', '军民血肉深情坚不可摧', 'http://dangshi.people.com.cn/n1/2021/0731/c436975-32176764.html', '历史文献');

-- 延安整风运动（E018）
INSERT INTO materials (id, event_id, title, url, type) VALUES
('mat-018-01', 'E018', '延安整风运动(1942年—1945年)', 'https://www.12371.cn/2023/11/28/STUD1701138146515427.shtml', '历史文献'),
('mat-018-02', 'E018', '【百个瞬间说百年】1942，“惩前毖后，治病救人”', 'https://www.xinhuanet.com/politics/2021-04/19/c_1211116599.htm', '新闻报道'),
('mat-018-03', 'E018', '延安整风运动实现思想和行动统一', 'http://cpc.people.com.cn/BIG5/n1/2022/0809/c443712-32497928.html', '历史文献');

-- 豫湘桂战役（E019）
INSERT INTO materials (id, event_id, title, url, type) VALUES
('mat-019-01', 'E019', '豫湘桂战役', 'http://www.1937china.com/views/newsdetail/news_detail.html?id=31&newsSession=kzls_zyzy&parentPage=kzls_zyzhanyi&fileName=20190612/156605108093237562696.html', '历史文献'),
('mat-019-02', 'E019', '日军打通大陆交通线并威胁重庆', 'http://m.cnr.cn/news/20150906/t20150906_519782594.html', '新闻报道'),
('mat-019-03', 'E019', '豫湘桂战役全记录', 'http://www.81.cn/jwzl/2023-12/05/content_10333331.htm', '历史文献');

-- 日本无条件投降（E020）
INSERT INTO materials (id, event_id, title, url, type) VALUES
('mat-020-01', 'E020', '伟大胜利——中国受降档案', 'https://www.saac.gov.cn/zt/kzda/qy.html', '档案资料'),
('mat-020-02', 'E020', '话说1945·8·15昭和天皇宣布“终战”，无条件投降', 'https://politics.people.com.cn/n/2015/0816/c1001-27468789.html', '新闻报道'),
('mat-020-03', 'E020', '新闻背景:中国人民抗日战争胜利纪念日', 'http://politics.people.com.cn/n/2014/0227/c70731-24485277.html', '新闻报道');

