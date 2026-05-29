-- ===============================================
-- 宠物养成空间模块数据库扩展脚本
-- 作者: EnvironmentProtection Team
-- 创建时间: 2026-04-19
-- ===============================================

-- 确保使用正确的数据库
USE lvfat;

-- -----------------------------------------------
-- 表1: 宠物养成空间表
-- 存储用户宠物的养成空间状态和位置信息
-- -----------------------------------------------
DROP TABLE IF EXISTS pet_breed_space;
CREATE TABLE pet_breed_space (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    pet_id BIGINT NOT NULL COMMENT '宠物ID',
    current_scene VARCHAR(50) DEFAULT 'garden' COMMENT '当前场景：garden(花园)、forest(森林)、beach(沙滩)、mountain(山峰)',
    last_position_x DECIMAL(10,2) DEFAULT 0 COMMENT '上次位置X坐标',
    last_position_y DECIMAL(10,2) DEFAULT 0 COMMENT '上次位置Y坐标(高度)',
    last_position_z DECIMAL(10,2) DEFAULT 0 COMMENT '上次位置Z坐标',
    space_level INT DEFAULT 1 COMMENT '空间等级',
    space_exp INT DEFAULT 0 COMMENT '空间经验值',
    unlocked_scenes VARCHAR(500) DEFAULT 'garden' COMMENT '已解锁的场景列表，逗号分隔',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_pet (user_id, pet_id),
    INDEX idx_user_id (user_id),
    INDEX idx_pet_id (pet_id),
    INDEX idx_current_scene (current_scene)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='宠物养成空间表';

-- -----------------------------------------------
-- 表2: 互动任务表
-- 定义宠物可以完成的互动任务类型
-- -----------------------------------------------
DROP TABLE IF EXISTS pet_interaction_task;
CREATE TABLE pet_interaction_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    task_code VARCHAR(50) NOT NULL UNIQUE COMMENT '任务代码，唯一标识',
    task_name VARCHAR(100) NOT NULL COMMENT '任务名称',
    task_type VARCHAR(20) NOT NULL COMMENT '任务类型：feed(喂食)、pet(抚摸)、clean(清洁)、train(训练)、play(玩耍)',
    description TEXT COMMENT '任务描述',
    icon_url VARCHAR(500) COMMENT '任务图标URL',
    exp_reward INT DEFAULT 0 COMMENT '经验奖励',
    coin_reward INT DEFAULT 0 COMMENT '金币奖励',
    item_reward_json TEXT COMMENT '物品奖励JSON，格式：[{"itemId":1,"count":1}]',
    cooldown_seconds INT DEFAULT 0 COMMENT '冷却时间（秒），0表示无冷却',
    min_pet_level INT DEFAULT 1 COMMENT '最低宠物等级要求',
    energy_cost INT DEFAULT 0 COMMENT '体力消耗',
    duration_seconds INT DEFAULT 5 COMMENT '互动持续时间（秒）',
    animation_type VARCHAR(30) DEFAULT 'default' COMMENT '动画类型：idle、walk、eat、pet、clean、play',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_task_type (task_type),
    INDEX idx_is_active (is_active),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='互动任务表';

-- -----------------------------------------------
-- 表3: 用户宠物任务日志表
-- 记录用户完成任务的历只
-- -----------------------------------------------
DROP TABLE IF EXISTS user_pet_task_log;
CREATE TABLE user_pet_task_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    pet_id BIGINT NOT NULL COMMENT '宠物ID',
    task_id BIGINT NOT NULL COMMENT '任务ID',
    task_code VARCHAR(50) NOT NULL COMMENT '任务代码',
    task_name VARCHAR(100) COMMENT '任务名称（冗余存储）',
    completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '完成时间',
    exp_gained INT DEFAULT 0 COMMENT '获得经验值',
    coin_gained INT DEFAULT 0 COMMENT '获得金币',
    item_reward_json TEXT COMMENT '获得的物品（冗余存储）',
    duration_ms INT COMMENT '完成耗时（毫秒）',
    ip_address VARCHAR(50) COMMENT '操作IP地址',
    device_info VARCHAR(200) COMMENT '设备信息',
    INDEX idx_user_id (user_id),
    INDEX idx_pet_id (pet_id),
    INDEX idx_task_id (task_id),
    INDEX idx_completed_at (completed_at),
    INDEX idx_user_pet (user_id, pet_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户宠物任务日志表';

-- -----------------------------------------------
-- 表4: 宠物等级配置表
-- 定义宠物等级和经验值规则
-- -----------------------------------------------
DROP TABLE IF EXISTS pet_level_config;
CREATE TABLE pet_level_config (
    level INT PRIMARY KEY COMMENT '等级',
    min_exp INT NOT NULL COMMENT '该等级最低经验值',
    max_exp INT COMMENT '该等级最高经验值，MAX表示无上限',
    level_title VARCHAR(50) COMMENT '等级称号',
    unlock_skill VARCHAR(200) COMMENT '解锁的技能（逗号分隔）',
    unlock_scene VARCHAR(200) COMMENT '解锁的场景',
    bonus_exp_rate DECIMAL(5,4) DEFAULT 1.0000 COMMENT '经验加成倍率',
    bonus_coin_rate DECIMAL(5,4) DEFAULT 1.0000 COMMENT '金币加成倍率',
    required_friends INT DEFAULT 0 COMMENT '需要的好友数量',
    required_tasks INT DEFAULT 0 COMMENT '需要完成的任务数量',
    avatar_frame VARCHAR(100) COMMENT '头像框样式',
    title_bg_color VARCHAR(20) COMMENT '称号背景颜色',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_min_exp (min_exp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='宠物等级配置表';

-- -----------------------------------------------
-- 表5: 宠物奖励记录表
-- 记录用户获得的所有宠物相关奖励
-- -----------------------------------------------
DROP TABLE IF EXISTS pet_reward_record;
CREATE TABLE pet_reward_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    pet_id BIGINT NOT NULL COMMENT '宠物ID',
    reward_type VARCHAR(20) NOT NULL COMMENT '奖励类型：exp(经验)、coin(金币)、item(物品)',
    reward_value INT NOT NULL COMMENT '奖励值',
    reward_name VARCHAR(100) COMMENT '奖励名称（如物品名称）',
    source VARCHAR(50) NOT NULL COMMENT '奖励来源：task_complete、daily_login、achievement、level_up',
    source_id BIGINT COMMENT '来源ID（如任务ID）',
    source_name VARCHAR(100) COMMENT '来源名称（如任务名称）',
    pet_level_before INT COMMENT '变化前宠物等级',
    pet_level_after INT COMMENT '变化后宠物等级',
    pet_exp_before INT COMMENT '变化前宠物经验',
    pet_exp_after INT COMMENT '变化后宠物经验',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_pet_id (pet_id),
    INDEX idx_reward_type (reward_type),
    INDEX idx_source (source),
    INDEX idx_created_at (created_at),
    INDEX idx_user_source (user_id, source)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='宠物奖励记录表';

-- -----------------------------------------------
-- 表6: 宠物场景表（扩展原有）
-- 存储可用的宠物养成场景信息
-- -----------------------------------------------
DROP TABLE IF EXISTS pet_scene_ext;
CREATE TABLE pet_scene_ext (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    scene_code VARCHAR(50) NOT NULL UNIQUE COMMENT '场景代码',
    scene_name VARCHAR(100) NOT NULL COMMENT '场景名称',
    scene_description TEXT COMMENT '场景描述',
    thumbnail_url VARCHAR(500) COMMENT '缩略图URL',
    model_url VARCHAR(500) COMMENT '3D模型URL',
    background_url VARCHAR(500) COMMENT '背景图URL',
    music_url VARCHAR(500) COMMENT '背景音乐URL',
    particle_effect VARCHAR(200) COMMENT '粒子特效配置',
    required_level INT DEFAULT 1 COMMENT '解锁所需等级',
    required_coin INT DEFAULT 0 COMMENT '解锁所需金币',
    is_default BOOLEAN DEFAULT FALSE COMMENT '是否为默认场景',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    map_bounds_x_min DECIMAL(10,2) DEFAULT -50 COMMENT '地图边界X最小值',
    map_bounds_x_max DECIMAL(10,2) DEFAULT 50 COMMENT '地图边界X最大值',
    map_bounds_z_min DECIMAL(10,2) DEFAULT -50 COMMENT '地图边界Z最小值',
    map_bounds_z_max DECIMAL(10,2) DEFAULT 50 COMMENT '地图边界Z最大值',
    spawn_point_x DECIMAL(10,2) DEFAULT 0 COMMENT '出生点X坐标',
    spawn_point_z DECIMAL(10,2) DEFAULT 0 COMMENT '出生点Z坐标',
    interactive_points_json TEXT COMMENT '互动点配置JSON',
    decoration_items_json TEXT COMMENT '装饰物配置JSON',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_scene_code (scene_code),
    INDEX idx_required_level (required_level),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='宠物场景扩展表';

-- -----------------------------------------------
-- 表7: 宠物动作表
-- 定义宠物可执行的动作动画
-- -----------------------------------------------
DROP TABLE IF EXISTS pet_action;
CREATE TABLE pet_action (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    action_code VARCHAR(50) NOT NULL UNIQUE COMMENT '动作代码',
    action_name VARCHAR(100) NOT NULL COMMENT '动作名称',
    animation_name VARCHAR(100) COMMENT '3D动画名称',
    duration_seconds DECIMAL(5,2) DEFAULT 1.00 COMMENT '动作持续时间（秒）',
    loop_count INT DEFAULT 1 COMMENT '循环次数，-1表示无限循环',
    blend_mode VARCHAR(20) DEFAULT 'default' COMMENT '混合模式：default、additive、override',
    can_interrupt BOOLEAN DEFAULT TRUE COMMENT '是否可被中断',
    sound_effect_url VARCHAR(500) COMMENT '音效URL',
    particle_effect VARCHAR(200) COMMENT '粒子特效配置',
    emotion_change INT DEFAULT 0 COMMENT '情绪值变化',
    energy_change INT DEFAULT 0 COMMENT '体力值变化',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    description TEXT COMMENT '动作描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_action_code (action_code),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='宠物动作表';

-- -----------------------------------------------
-- 表8: 宠物成就表
-- 定义宠物可解锁的成就
-- -----------------------------------------------
DROP TABLE IF EXISTS pet_achievement;
CREATE TABLE pet_achievement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    achievement_code VARCHAR(50) NOT NULL UNIQUE COMMENT '成就代码',
    achievement_name VARCHAR(100) NOT NULL COMMENT '成就名称',
    achievement_type VARCHAR(30) NOT NULL COMMENT '成就类型：interaction(互动)、level(等级)、time(时间)、collection(收集)',
    description TEXT COMMENT '成就描述',
    icon_url VARCHAR(500) COMMENT '图标URL',
    condition_type VARCHAR(30) COMMENT '达成条件类型：task_count、total_exp、play_time、specific_task',
    condition_value INT DEFAULT 0 COMMENT '达成条件值',
    condition_param VARCHAR(200) COMMENT '达成条件参数（如特定任务代码）',
    exp_reward INT DEFAULT 0 COMMENT '经验奖励',
    coin_reward INT DEFAULT 0 COMMENT '金币奖励',
    title_reward VARCHAR(100) COMMENT '称号奖励',
    avatar_frame_reward VARCHAR(100) COMMENT '头像框奖励',
    is_hidden BOOLEAN DEFAULT FALSE COMMENT '是否隐藏成就',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_achievement_type (achievement_type),
    INDEX idx_condition_type (condition_type),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='宠物成就表';

-- -----------------------------------------------
-- 表9: 用户宠物成就记录表
-- 记录用户宠物已解锁的成就
-- -----------------------------------------------
DROP TABLE IF EXISTS user_pet_achievement;
CREATE TABLE user_pet_achievement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    pet_id BIGINT NOT NULL COMMENT '宠物ID',
    achievement_id BIGINT NOT NULL COMMENT '成就ID',
    achievement_code VARCHAR(50) NOT NULL COMMENT '成就代码',
    unlocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '解锁时间',
    notification_sent BOOLEAN DEFAULT FALSE COMMENT '是否已发送通知',
    INDEX idx_user_pet (user_id, pet_id),
    INDEX idx_achievement_id (achievement_id),
    INDEX idx_unlocked_at (unlocked_at),
    UNIQUE KEY uk_user_pet_achievement (user_id, pet_id, achievement_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户宠物成就记录表';

-- ===============================================
-- 初始化数据
-- ===============================================

-- 插入宠物等级配置（1-30级）
INSERT INTO pet_level_config (level, min_exp, max_exp, level_title, unlock_skill, bonus_exp_rate) VALUES
(1, 0, 99, '新手', NULL, 1.0000),
(2, 100, 299, '学徒', NULL, 1.0000),
(3, 300, 599, '初学者', NULL, 1.0000),
(4, 600, 999, '入门', '撒娇', 1.0000),
(5, 1000, 1499, '成长', NULL, 1.1000),
(6, 1500, 2099, '进阶', '打滚', 1.1000),
(7, 2100, 2799, '熟练', NULL, 1.1000),
(8, 2800, 3599, '精通', '跳跃', 1.2000),
(9, 3600, 4499, '高手', NULL, 1.2000),
(10, 4500, 5499, '专家', '翻滚', 1.2000),
(11, 5500, 6599, '精英', NULL, 1.3000),
(12, 6600, 7799, '大师', '特技', 1.3000),
(13, 7800, 9099, '宗师', NULL, 1.3000),
(14, 9100, 10499, '传奇', '飞行', 1.4000),
(15, 10500, 11999, '王者', NULL, 1.4000),
(16, 12000, 13499, '皇者', '瞬移', 1.4000),
(17, 13500, 14999, '帝皇', NULL, 1.5000),
(18, 15000, 16499, '神话', '神圣之光', 1.5000),
(19, 16500, 17999, '至尊', NULL, 1.5000),
(20, 18000, 19499, '天帝', '时间静止', 1.6000),
(21, 19500, 20999, '天道', NULL, 1.6000),
(22, 21000, 22499, '创始', '创造', 1.6000),
(23, 22500, 23999, '创世', NULL, 1.7000),
(24, 24000, 25499, '主宰', '领域展开', 1.7000),
(25, 25500, 26999, '永恒', NULL, 1.7000),
(26, 27000, 28499, '不朽', '永生', 1.8000),
(27, 28500, 29999, '无上', NULL, 1.8000),
(28, 30000, 31499, '究极', '究极进化', 1.8000),
(29, 31500, 32999, '完美', NULL, 1.9000),
(30, 33000, NULL, '完美至极', '终极形态', 2.0000);

-- 插入互动任务配置
INSERT INTO pet_interaction_task (task_code, task_name, task_type, description, exp_reward, coin_reward, cooldown_seconds, min_pet_level, animation_type, energy_cost, duration_seconds, sort_order) VALUES
-- 喂食类任务
('feed_01', '喂食小零食', 'feed', '给宠物喂一颗美味的小零食', 5, 2, 60, 1, 'eat', 0, 3, 1),
('feed_02', '喂食正餐', 'feed', '给宠物准备一顿丰盛的大餐', 15, 5, 300, 3, 'eat', 5, 5, 2),
('feed_03', '喂食高级料理', 'feed', '给宠物喂食珍贵的高级料理', 30, 10, 600, 7, 'eat', 10, 8, 3),
('feed_04', '喂食神秘食物', 'feed', '给宠物喂食据说能增加幸运的神秘食物', 50, 20, 1800, 15, 'eat', 15, 10, 4),

-- 抚摸类任务
('pet_01', '轻轻抚摸', 'pet', '温柔地抚摸宠物的头部', 3, 1, 30, 1, 'pet', 0, 2, 5),
('pet_02', '全身按摩', 'pet', '给宠物做一个全身放松按摩', 10, 3, 180, 2, 'pet', 2, 5, 6),
('pet_03', '深情拥抱', 'pet', '给宠物一个暖暖的拥抱', 20, 8, 600, 5, 'pet', 5, 8, 7),
('pet_04', '心灵沟通', 'pet', '与宠物进行心灵感应交流', 40, 15, 1200, 12, 'pet', 10, 12, 8),

-- 清洁类任务
('clean_01', '梳理毛发', 'clean', '用梳子仔细梳理宠物的毛发', 5, 2, 120, 1, 'clean', 0, 4, 9),
('clean_02', '洗澡清洁', 'clean', '给宠物洗一个香香的澡', 15, 5, 600, 3, 'clean', 5, 10, 10),
('clean_03', '美容护理', 'clean', '带宠物去做一个全面的美容护理', 25, 10, 1800, 8, 'clean', 10, 15, 11),
('clean_04', 'SPA护理', 'clean', '给宠物安排一次高级SPA护理', 45, 20, 3600, 15, 'clean', 15, 20, 12),

-- 训练类任务
('train_01', '基础训练', 'train', '教宠物一些简单的基础动作', 8, 3, 180, 2, 'train', 3, 5, 13),
('train_02', '技能学习', 'train', '训练宠物学习新技能', 20, 8, 600, 5, 'train', 8, 10, 14),
('train_03', '高级训练', 'train', '进行高强度的技能训练', 35, 15, 1800, 10, 'train', 15, 15, 15),
('train_04', '极限挑战', 'train', '挑战宠物的极限能力', 60, 30, 3600, 20, 'train', 25, 25, 16),

-- 玩耍类任务
('play_01', '追逐游戏', 'play', '和宠物玩追逐游戏', 6, 2, 120, 1, 'play', 2, 4, 17),
('play_02', '接飞盘', 'play', '和宠物玩接飞盘游戏', 15, 5, 300, 4, 'play', 5, 8, 18),
('play_03', '捉迷藏', 'play', '和宠物玩捉迷藏游戏', 25, 10, 600, 7, 'play', 8, 12, 19),
('play_04', '探险之旅', 'play', '带宠物去进行一次有趣的探险', 40, 15, 1800, 12, 'play', 12, 18, 20),
('play_05', '欢乐派对', 'play', '和宠物开一个欢乐派对', 70, 30, 3600, 18, 'play', 20, 25, 21);

-- 插入宠物场景配置
INSERT INTO pet_scene_ext (scene_code, scene_name, scene_description, required_level, required_coin, is_default, sort_order, map_bounds_x_min, map_bounds_x_max, map_bounds_z_min, map_bounds_z_max) VALUES
('garden', '阳光花园', '充满阳光和鲜花的美丽花园，是宠物最喜欢的地方', 1, 0, TRUE, 1, -30, 30, -30, 30),
('forest', '神秘森林', '充满神秘气息的森林，有各种有趣的事物等待发现', 5, 100, FALSE, 2, -40, 40, -40, 40),
('beach', '金色沙滩', '阳光明媚的金色沙滩，可以和宠物一起玩沙子和海水', 10, 300, FALSE, 3, -35, 35, -35, 35),
('mountain', '云端山峰', '高耸入云的山峰，站在山顶可以俯瞰整个世界', 15, 500, FALSE, 4, -50, 50, -50, 50),
('snowland', '冰雪世界', '银装素裹的冰雪世界，有着独特的美丽风景', 20, 800, FALSE, 5, -45, 45, -45, 45),
('night', '星空之夜', '繁星点点的夜空，可以和宠物一起数星星', 25, 1200, FALSE, 6, -40, 40, -40, 40);

-- 插入宠物动作配置
INSERT INTO pet_action (action_code, action_name, animation_name, duration_seconds, can_interrupt, emotion_change, energy_change) VALUES
('idle', '待机', 'idle', 2.0, TRUE, 0, 0),
('walk', '行走', 'walk', 0.8, TRUE, 0, -1),
('run', '奔跑', 'run', 0.5, TRUE, 1, -3),
('jump', '跳跃', 'jump', 0.6, FALSE, 2, -2),
('eat', '吃东西', 'eat', 2.0, FALSE, 3, 5),
('pet', '被抚摸', 'pet', 3.0, FALSE, 5, 3),
('clean', '清洁', 'clean', 4.0, FALSE, 2, 2),
('play', '玩耍', 'play', 3.0, FALSE, 4, -3),
('train', '训练', 'train', 5.0, FALSE, 1, -5),
('sleep', '睡觉', 'sleep', 10.0, FALSE, 3, 10),
('happy', '开心', 'happy', 1.5, TRUE, 5, 0),
('sad', '难过', 'sad', 2.0, TRUE, -5, 0),
('angry', '生气', 'angry', 2.0, TRUE, -3, 0),
('roll', '翻滚', 'roll', 1.5, FALSE, 3, -1),
('fly', '飞行', 'fly', 5.0, FALSE, 4, -5);

-- 插入宠物成就配置
INSERT INTO pet_achievement (achievement_code, achievement_name, achievement_type, description, condition_type, condition_value, exp_reward, coin_reward, sort_order) VALUES
-- 互动类成就
('first_interaction', '初次相遇', 'interaction', '完成第一次互动', 'task_count', 1, 10, 5, 1),
('hundred_interactions', '百次互动', 'interaction', '累计完成100次互动', 'task_count', 100, 100, 50, 2),
('thousand_interactions', '千次互动', 'interaction', '累计完成1000次互动', 'task_count', 1000, 500, 200, 3),

-- 等级类成就
('reach_level_5', '初露锋芒', 'level', '宠物等级达到5级', 'level', 5, 50, 30, 4),
('reach_level_10', '小有所成', 'level', '宠物等级达到10级', 'level', 10, 150, 80, 5),
('reach_level_20', '登堂入室', 'level', '宠物等级达到20级', 'level', 20, 400, 200, 6),
('reach_level_30', '登峰造极', 'level', '宠物等级达到30级', 'level', 30, 1000, 500, 7),

-- 时间类成就
('daily_visit_7', '一周陪伴', 'time', '连续7天登录', 'login_days', 7, 70, 35, 8),
('daily_visit_30', '一月相伴', 'time', '连续30天登录', 'login_days', 30, 300, 150, 9),

-- 收集类成就
('all_scenes', '环游世界', 'collection', '解锁所有场景', 'scene_count', 6, 300, 150, 10),
('all_tasks', '全能达人', 'collection', '完成所有类型的互动任务', 'task_type_count', 5, 200, 100, 11);
