-- ============================================
-- 宠物养成空间数据库表
-- 绿法生态岛 - 环保普法宠物养成系统
-- ============================================

-- 1. 用户宠物表
CREATE TABLE IF NOT EXISTS `user_pet` (
    `id` VARCHAR(64) NOT NULL COMMENT '主键ID',
    `user_id` VARCHAR(64) NOT NULL COMMENT '用户ID',
    `pet_name` VARCHAR(50) NOT NULL DEFAULT '绿小宝' COMMENT '宠物名称',
    `pet_type` VARCHAR(20) NOT NULL DEFAULT 'leaf_balance' COMMENT '宠物类型: leaf_balance-叶子天平, energy_leaf-能源叶子, law_tree-法律之树',
    `pet_image` VARCHAR(500) DEFAULT NULL COMMENT '宠物形象URL',
    `level` INT NOT NULL DEFAULT 1 COMMENT '等级',
    `exp` INT NOT NULL DEFAULT 0 COMMENT '经验值',
    `energy` INT NOT NULL DEFAULT 0 COMMENT '绿色能量',
    `total_energy` INT NOT NULL DEFAULT 0 COMMENT '累计获得能量',
    `current_x` DOUBLE NOT NULL DEFAULT 375 COMMENT '当前X坐标',
    `current_y` DOUBLE NOT NULL DEFAULT 600 COMMENT '当前Y坐标',
    `direction` VARCHAR(10) DEFAULT 'down' COMMENT '朝向: up, down, left, right',
    `scene_id` INT NOT NULL DEFAULT 1 COMMENT '场景ID',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-离线, 1-在线',
    `last_active_time` DATETIME DEFAULT NULL COMMENT '最后活跃时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户宠物表';

-- 2. 场景互动点表
CREATE TABLE IF NOT EXISTS `scene_point` (
    `id` VARCHAR(64) NOT NULL COMMENT '主键ID',
    `scene_id` INT NOT NULL COMMENT '场景ID',
    `point_name` VARCHAR(100) NOT NULL COMMENT '互动点名称',
    `point_type` VARCHAR(20) NOT NULL COMMENT '类型: energy-能量点, task-任务点, law-普法点',
    `point_icon` VARCHAR(100) DEFAULT NULL COMMENT '图标',
    `x` DOUBLE NOT NULL COMMENT 'X坐标',
    `y` DOUBLE NOT NULL COMMENT 'Y坐标',
    `radius` DOUBLE NOT NULL DEFAULT 50 COMMENT '触发半径',
    `reward_energy` INT NOT NULL DEFAULT 10 COMMENT '奖励能量',
    `reward_exp` INT NOT NULL DEFAULT 5 COMMENT '奖励经验',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
    `content_id` VARCHAR(64) DEFAULT NULL COMMENT '关联内容ID(法律条文/任务等)',
    `daily_limit` INT NOT NULL DEFAULT 0 COMMENT '每日限制次数: 0-不限',
    `cooldown_seconds` INT NOT NULL DEFAULT 0 COMMENT '冷却时间(秒)',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_scene_id` (`scene_id`),
    KEY `idx_point_type` (`point_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='场景互动点表';

-- 3. 宠物互动日志表
CREATE TABLE IF NOT EXISTS `pet_interact_log` (
    `id` VARCHAR(64) NOT NULL COMMENT '主键ID',
    `user_id` VARCHAR(64) NOT NULL COMMENT '用户ID',
    `pet_id` VARCHAR(64) NOT NULL COMMENT '宠物ID',
    `point_id` VARCHAR(64) NOT NULL COMMENT '互动点ID',
    `point_type` VARCHAR(20) NOT NULL COMMENT '互动点类型',
    `reward_energy` INT NOT NULL DEFAULT 0 COMMENT '获得能量',
    `reward_exp` INT NOT NULL DEFAULT 0 COMMENT '获得经验',
    `level_up` INT NOT NULL DEFAULT 0 COMMENT '是否升级: 0-否, 升级后等级',
    `task_id` VARCHAR(64) DEFAULT NULL COMMENT '关联任务ID(如果完成任务)',
    `complete_status` TINYINT NOT NULL DEFAULT 1 COMMENT '完成状态: 0-未完成, 1-已完成',
    `interact_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '互动时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_pet_id` (`pet_id`),
    KEY `idx_point_id` (`point_id`),
    KEY `idx_interact_time` (`interact_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='宠物互动日志表';

-- 4. 场景表
CREATE TABLE IF NOT EXISTS `pet_scene` (
    `id` INT NOT NULL COMMENT '主键ID',
    `scene_name` VARCHAR(100) NOT NULL COMMENT '场景名称',
    `scene_code` VARCHAR(50) NOT NULL COMMENT '场景编码',
    `scene_image` VARCHAR(500) DEFAULT NULL COMMENT '场景背景图',
    `scene_width` INT NOT NULL DEFAULT 750 COMMENT '场景宽度',
    `scene_height` INT NOT NULL DEFAULT 1200 COMMENT '场景高度',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
    `unlock_level` INT NOT NULL DEFAULT 1 COMMENT '解锁等级',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_scene_code` (`scene_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='宠物场景表';

-- 5. 宠物升级规则表
CREATE TABLE IF NOT EXISTS `pet_level_rule` (
    `id` VARCHAR(64) NOT NULL COMMENT '主键ID',
    `level` INT NOT NULL COMMENT '等级',
    `exp_required` INT NOT NULL COMMENT '所需经验',
    `unlock_skill` VARCHAR(200) DEFAULT NULL COMMENT '解锁技能',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '升级描述',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='宠物升级规则表';

-- ============================================
-- 初始化数据
-- ============================================

-- 初始化场景数据
INSERT INTO `pet_scene` (`id`, `scene_name`, `scene_code`, `scene_width`, `scene_height`, `description`, `unlock_level`, `status`) VALUES
(1, '绿法生态岛', 'green_law_island', 750, 1200, '绿色环保普法主题岛屿', 1, 1);

-- 初始化场景互动点数据
INSERT INTO `scene_point` (`id`, `scene_id`, `point_name`, `point_type`, `x`, `y`, `radius`, `reward_energy`, `reward_exp`, `description`, `daily_limit`, `sort_order`) VALUES
('point_001', 1, '普法知识站', 'law', 100, 300, 50, 20, 10, '学习环保法律知识，提升法治意识', 3, 1),
('point_002', 1, '绿色能量池', 'energy', 400, 500, 40, 10, 5, '领取每日绿色能量', 5, 2),
('point_003', 1, '环保任务板', 'task', 650, 800, 45, 50, 25, '完成今日环保普法任务', 1, 3),
('point_004', 1, '法律天平', 'law', 200, 1000, 55, 30, 15, '了解法律公平正义', 2, 4),
('point_005', 1, '生态花园', 'energy', 550, 200, 40, 15, 8, '收集生态能量', 5, 5);

-- 初始化升级规则（1-20级）
INSERT INTO `pet_level_rule` (`id`, `level`, `exp_required`, `unlock_skill`, `description`) VALUES
('level_01', 1, 100, NULL, '初始状态'),
('level_02', 2, 200, '环保知识+1', '解锁环保知识技能'),
('level_03', 3, 300, '能量收集+1', '提升能量收集效率'),
('level_04', 4, 400, '普法传播+1', '普法传播范围扩大'),
('level_05', 5, 500, '生态守护', '解锁生态守护技能'),
('level_06', 6, 600, NULL, '继续升级'),
('level_07', 7, 700, NULL, '继续升级'),
('level_08', 8, 800, NULL, '继续升级'),
('level_09', 9, 900, NULL, '继续升级'),
('level_10', 10, 1000, '法律顾问', '解锁法律顾问技能'),
('level_11', 11, 1100, NULL, '继续升级'),
('level_12', 12, 1200, NULL, '继续升级'),
('level_13', 13, 1300, NULL, '继续升级'),
('level_14', 14, 1400, NULL, '继续升级'),
('level_15', 15, 1500, '环保先锋', '解锁环保先锋技能'),
('level_16', 16, 1600, NULL, '继续升级'),
('level_17', 17, 1700, NULL, '继续升级'),
('level_18', 18, 1800, NULL, '继续升级'),
('level_19', 19, 1900, NULL, '继续升级'),
('level_20', 20, 2000, '绿法大师', '成为绿法大师');
