-- =============================================
-- 绿法通 - 白色污染治理AI智慧普法平台
-- 数据库初始化脚本 (完整版)
-- =============================================
-- 版本：v2.0
-- 更新说明：
--   - 补充 sys_organization 表缺失字段
--   - 新增 employee 职员表
--   - 新增 student 学生表
--   - 新增 guardian 监护人表
--   - 新增 school 学校表
--   - 新增 school_class 班级表
--   - 新增 task_dispatch 任务下发表
--   - 新增 task_progress_report 任务进度上报表
--   - 新增 organization_category 组织分类配置表
--   - 新增 organization_type 组织类型配置表
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS lvfat DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE lvfat;

-- =============================================
-- 【模块一】系统管理表
-- =============================================

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    nickname VARCHAR(100) DEFAULT NULL COMMENT '昵称',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    role VARCHAR(50) NOT NULL DEFAULT 'user' COMMENT '角色: super_admin, government, enterprise, law_firm, user',
    organization_id VARCHAR(32) DEFAULT NULL COMMENT '所属组织ID',
    status VARCHAR(10) NOT NULL DEFAULT '1' COMMENT '状态: 1-正常, 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    INDEX idx_username (username),
    INDEX idx_role (role),
    INDEX idx_organization_id (organization_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 组织架构表 (已扩展字段)
CREATE TABLE IF NOT EXISTS sys_organization (
    id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT '组织ID',
    name VARCHAR(100) NOT NULL COMMENT '组织名称',
    level INT NOT NULL COMMENT '层级: 1-省级, 2-县级, 3-乡镇级, 4-村级',
    parent_id VARCHAR(32) DEFAULT NULL COMMENT '父级组织ID',
    code VARCHAR(50) DEFAULT NULL COMMENT '组织编码',
    description VARCHAR(500) DEFAULT NULL COMMENT '描述',
    -- 新增字段
    type VARCHAR(50) DEFAULT NULL COMMENT '组织类型: government-政府, enterprise-企业, platform-平台运营, education-教育, law_firm-律所',
    category VARCHAR(50) DEFAULT NULL COMMENT '组织分类: province-省级, city-市级, county-县级, town-乡镇; enterprise: headquarters-总公司, branch-分公司, department-部门',
    region_code VARCHAR(50) DEFAULT NULL COMMENT '行政区划代码',
    leader_name VARCHAR(100) DEFAULT NULL COMMENT '负责人姓名',
    leader_phone VARCHAR(20) DEFAULT NULL COMMENT '负责人电话',
    contact_address VARCHAR(500) DEFAULT NULL COMMENT '联系地址',
    sort_order INT DEFAULT 0 COMMENT '排序序号',
    parent_chain VARCHAR(500) DEFAULT NULL COMMENT '父级链路径, 格式: /id1/id2/id3/',
    level_path VARCHAR(500) DEFAULT NULL COMMENT '层级路径, 格式: 省级-市级-县级',
    staff_count INT DEFAULT 0 COMMENT '职员人数',
    student_count INT DEFAULT 0 COMMENT '学生人数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_parent_id (parent_id),
    INDEX idx_level (level),
    INDEX idx_type (type),
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组织架构表';

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT '角色ID',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    name VARCHAR(100) NOT NULL COMMENT '角色名称',
    description VARCHAR(500) DEFAULT NULL COMMENT '描述',
    sort INT NOT NULL DEFAULT 0 COMMENT '排序',
    status VARCHAR(10) NOT NULL DEFAULT '1' COMMENT '状态: 1-正常, 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT 'ID',
    user_id VARCHAR(32) NOT NULL COMMENT '用户ID',
    role_id VARCHAR(32) NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- =============================================
-- 【模块一扩展】组织配置表
-- =============================================

-- 组织类型配置表
CREATE TABLE IF NOT EXISTS organization_type (
    id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT 'ID',
    type_code VARCHAR(50) NOT NULL COMMENT '类型编码',
    type_name VARCHAR(100) NOT NULL COMMENT '类型名称',
    icon VARCHAR(255) DEFAULT NULL COMMENT '图标',
    color VARCHAR(20) DEFAULT NULL COMMENT '颜色',
    description VARCHAR(500) DEFAULT NULL COMMENT '描述',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status VARCHAR(10) NOT NULL DEFAULT '1' COMMENT '状态: 1-正常, 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_type_code (type_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组织类型配置表';

-- 组织分类配置表
CREATE TABLE IF NOT EXISTS organization_category (
    id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT 'ID',
    type_id VARCHAR(32) NOT NULL COMMENT '组织类型ID',
    category_code VARCHAR(50) NOT NULL COMMENT '分类编码',
    category_name VARCHAR(100) NOT NULL COMMENT '分类名称',
    description VARCHAR(500) DEFAULT NULL COMMENT '描述',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status VARCHAR(10) NOT NULL DEFAULT '1' COMMENT '状态: 1-正常, 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_type_category (type_id, category_code),
    INDEX idx_type_id (type_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组织分类配置表';

-- =============================================
-- 【模块二】法规管理表
-- =============================================

-- 法规表
CREATE TABLE IF NOT EXISTS law (
    id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT '法规ID',
    title VARCHAR(500) NOT NULL COMMENT '法规标题',
    level VARCHAR(20) NOT NULL COMMENT '层级: national-国家级, provincial-省级, city-市级',
    category VARCHAR(50) DEFAULT NULL COMMENT '分类',
    content TEXT COMMENT '法规内容',
    file_url VARCHAR(500) DEFAULT NULL COMMENT '文件URL',
    publish_date VARCHAR(20) DEFAULT NULL COMMENT '发布日期',
    effective_date VARCHAR(20) DEFAULT NULL COMMENT '生效日期',
    status VARCHAR(20) NOT NULL DEFAULT 'effective' COMMENT '状态: effective-有效, expired-失效',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_level (level),
    INDEX idx_category (category),
    INDEX idx_status (status),
    FULLTEXT idx_title_content (title, content)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='法规表';

-- =============================================
-- 【模块三】任务管理表
-- =============================================

-- 任务表
CREATE TABLE IF NOT EXISTS task (
    id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT '任务ID',
    title VARCHAR(200) NOT NULL COMMENT '任务标题',
    content TEXT COMMENT '任务内容',
    type VARCHAR(50) NOT NULL COMMENT '类型: government-政府任务, enterprise-企业任务, law_firm-律所任务',
    status VARCHAR(50) NOT NULL DEFAULT 'pending' COMMENT '状态: pending-待处理, in_progress-进行中, completed-已完成, overdue-已逾期, rejected-已拒绝',
    creator_id VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
    organization_id VARCHAR(32) DEFAULT NULL COMMENT '执行组织ID',
    start_time DATETIME DEFAULT NULL COMMENT '开始时间',
    end_time DATETIME DEFAULT NULL COMMENT '结束时间',
    target_count INT NOT NULL DEFAULT 0 COMMENT '目标数量',
    completed_count INT NOT NULL DEFAULT 0 COMMENT '已完成数量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_type (type),
    INDEX idx_status (status),
    INDEX idx_creator_id (creator_id),
    INDEX idx_organization_id (organization_id),
    INDEX idx_end_time (end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务表';

-- 任务下发记录表 (新增)
CREATE TABLE IF NOT EXISTS task_dispatch (
    id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT 'ID',
    task_id VARCHAR(32) NOT NULL COMMENT '任务ID',
    parent_dispatch_id VARCHAR(32) DEFAULT NULL COMMENT '上级下发记录ID',
    source_organization_id VARCHAR(32) NOT NULL COMMENT '下发方组织ID',
    target_organization_id VARCHAR(32) DEFAULT NULL COMMENT '接收方组织ID',
    target_user_id VARCHAR(32) DEFAULT NULL COMMENT '接收方用户ID(个人下发时使用)',
    target_employee_id VARCHAR(32) DEFAULT NULL COMMENT '接收方职员ID(职员下发时使用)',
    dispatch_level INT NOT NULL DEFAULT 1 COMMENT '下发层级(从1开始)',
    status VARCHAR(50) NOT NULL DEFAULT 'pending' COMMENT '状态: pending-待接收, accepted-已接收, rejected-已拒绝, completed-已完成, delegated-已转发, overdue-已逾期',
    target_count INT NOT NULL DEFAULT 0 COMMENT '目标数量(需完成任务数)',
    completed_count INT NOT NULL DEFAULT 0 COMMENT '已完成数量',
    progress INT DEFAULT 0 COMMENT '完成进度百分比',
    deadline DATETIME DEFAULT NULL COMMENT '截止时间',
    notes VARCHAR(1000) DEFAULT NULL COMMENT '备注说明',
    reject_reason VARCHAR(500) DEFAULT NULL COMMENT '拒绝原因',
    dispatch_time DATETIME DEFAULT NULL COMMENT '下发时间',
    accept_time DATETIME DEFAULT NULL COMMENT '接收时间',
    complete_time DATETIME DEFAULT NULL COMMENT '完成时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_task_id (task_id),
    INDEX idx_parent_dispatch_id (parent_dispatch_id),
    INDEX idx_source_organization_id (source_organization_id),
    INDEX idx_target_organization_id (target_organization_id),
    INDEX idx_target_user_id (target_user_id),
    INDEX idx_target_employee_id (target_employee_id),
    INDEX idx_status (status),
    INDEX idx_dispatch_level (dispatch_level),
    INDEX idx_dispatch_time (dispatch_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务下发记录表';

-- 任务进度上报记录表 (新增)
CREATE TABLE IF NOT EXISTS task_progress_report (
    id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT 'ID',
    dispatch_id VARCHAR(32) NOT NULL COMMENT '下发记录ID',
    reporter_id VARCHAR(32) NOT NULL COMMENT '上报人ID',
    reporter_name VARCHAR(100) DEFAULT NULL COMMENT '上报人姓名',
    report_type VARCHAR(50) NOT NULL COMMENT '上报类型: progress-进度, completion-完成',
    completed_count INT NOT NULL DEFAULT 0 COMMENT '本次上报完成数量',
    total_completed INT NOT NULL DEFAULT 0 COMMENT '累计完成数量',
    progress INT DEFAULT 0 COMMENT '进度百分比',
    content TEXT COMMENT '上报内容/说明',
    attachments JSON DEFAULT NULL COMMENT '附件(JSON数组)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_dispatch_id (dispatch_id),
    INDEX idx_reporter_id (reporter_id),
    INDEX idx_report_type (report_type),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务进度上报记录表';

-- =============================================
-- 【模块四】培训管理表
-- =============================================

-- 培训表
CREATE TABLE IF NOT EXISTS training (
    id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT '培训ID',
    title VARCHAR(200) NOT NULL COMMENT '培训标题',
    content TEXT COMMENT '培训内容',
    type VARCHAR(50) NOT NULL COMMENT '类型: video-视频, document-文档, exam-考试',
    duration INT DEFAULT 0 COMMENT '时长(分钟)',
    cover_url VARCHAR(500) DEFAULT NULL COMMENT '封面URL',
    video_url VARCHAR(500) DEFAULT NULL COMMENT '视频URL',
    attachment_url VARCHAR(500) DEFAULT NULL COMMENT '附件URL',
    view_count INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
    completed_count INT NOT NULL DEFAULT 0 COMMENT '完成人数',
    creator_id VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
    status VARCHAR(20) NOT NULL DEFAULT 'published' COMMENT '状态: draft-草稿, published-已发布, archived-已归档',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_type (type),
    INDEX idx_status (status),
    INDEX idx_creator_id (creator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训表';

-- 培训记录表
CREATE TABLE IF NOT EXISTS training_record (
    id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT '记录ID',
    training_id VARCHAR(32) NOT NULL COMMENT '培训ID',
    user_id VARCHAR(32) NOT NULL COMMENT '用户ID',
    status VARCHAR(50) NOT NULL DEFAULT 'not_started' COMMENT '状态: not_started-未开始, in_progress-进行中, completed-已完成',
    score INT DEFAULT NULL COMMENT '得分',
    duration INT DEFAULT 0 COMMENT '学习时长(分钟)',
    start_time DATETIME DEFAULT NULL COMMENT '开始时间',
    completed_time DATETIME DEFAULT NULL COMMENT '完成时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_training_id (training_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训记录表';

-- =============================================
-- 【模块五】案例管理表
-- =============================================

-- 案例表
CREATE TABLE IF NOT EXISTS case_info (
    id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT '案例ID',
    title VARCHAR(200) NOT NULL COMMENT '案例标题',
    type VARCHAR(50) NOT NULL COMMENT '类型: civil-民事, criminal-刑事, administrative-行政',
    description TEXT COMMENT '案例描述',
    result TEXT COMMENT '处理结果',
    law_firm_id VARCHAR(32) DEFAULT NULL COMMENT '所属律所ID',
    lawyer_id VARCHAR(32) DEFAULT NULL COMMENT '主办律师ID',
    lawyer_name VARCHAR(100) DEFAULT NULL COMMENT '律师姓名',
    cover_url VARCHAR(500) DEFAULT NULL COMMENT '封面URL',
    view_count INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
    status INT NOT NULL DEFAULT 0 COMMENT '状态: 0-待审核, 1-已发布, 2-已下架',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_type (type),
    INDEX idx_status (status),
    INDEX idx_law_firm_id (law_firm_id),
    INDEX idx_lawyer_id (lawyer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='案例表';

-- =============================================
-- 【模块六】举报管理表
-- =============================================

-- 举报表
CREATE TABLE IF NOT EXISTS report (
    id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT '举报ID',
    title VARCHAR(200) NOT NULL COMMENT '举报标题',
    content TEXT COMMENT '举报内容',
    type VARCHAR(50) NOT NULL COMMENT '类型: illegal_production-违规生产, illegal_sale-违规销售, illegal_use-违规使用, environmental_pollution-环境污染, other-其他',
    location VARCHAR(500) DEFAULT NULL COMMENT '举报地点',
    longitude DECIMAL(10, 6) DEFAULT NULL COMMENT '经度',
    latitude DECIMAL(10, 6) DEFAULT NULL COMMENT '纬度',
    images VARCHAR(2000) DEFAULT NULL COMMENT '图片URLs(JSON数组)',
    status VARCHAR(50) NOT NULL DEFAULT 'pending' COMMENT '状态: pending-待处理, processing-处理中, resolved-已解决, rejected-已驳回',
    reporter_id VARCHAR(32) DEFAULT NULL COMMENT '举报人ID',
    reporter_name VARCHAR(100) DEFAULT NULL COMMENT '举报人姓名',
    reporter_phone VARCHAR(20) DEFAULT NULL COMMENT '举报人电话',
    handler_id VARCHAR(32) DEFAULT NULL COMMENT '处理人ID',
    handler_name VARCHAR(100) DEFAULT NULL COMMENT '处理人姓名',
    handle_result TEXT COMMENT '处理结果',
    handle_time DATETIME DEFAULT NULL COMMENT '处理时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_type (type),
    INDEX idx_status (status),
    INDEX idx_reporter_id (reporter_id),
    INDEX idx_handler_id (handler_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='举报表';

-- =============================================
-- 【模块七】AI素材表
-- =============================================

-- AI素材表
CREATE TABLE IF NOT EXISTS ai_material (
    id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT '素材ID',
    title VARCHAR(200) NOT NULL COMMENT '素材标题',
    type VARCHAR(50) NOT NULL COMMENT '类型: video-视频, image-图片, text-文本, h5-H5页面',
    content TEXT COMMENT '素材内容',
    cover_url VARCHAR(500) DEFAULT NULL COMMENT '封面URL',
    source_url VARCHAR(500) DEFAULT NULL COMMENT '素材来源URL',
    topic VARCHAR(200) DEFAULT NULL COMMENT '主题',
    target_audience VARCHAR(100) DEFAULT NULL COMMENT '目标受众',
    law_id VARCHAR(32) DEFAULT NULL COMMENT '关联法规ID',
    creator_id VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
    status VARCHAR(50) NOT NULL DEFAULT 'generating' COMMENT '状态: generating-生成中, completed-已完成, failed-失败',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_type (type),
    INDEX idx_status (status),
    INDEX idx_creator_id (creator_id),
    INDEX idx_law_id (law_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI素材表';

-- =============================================
-- 【模块八】AI对话模块表
-- =============================================

-- AI对话会话表
CREATE TABLE IF NOT EXISTS ai_conversation (
    id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT '会话ID',
    user_id VARCHAR(32) NOT NULL COMMENT '用户ID',
    session_type VARCHAR(20) NOT NULL DEFAULT 'law' COMMENT '会话类型: law-法律问答, material-素材生成, case-案例分析, exam-组卷',
    title VARCHAR(200) DEFAULT NULL COMMENT '会话标题(首条消息摘要)',
    context_summary TEXT COMMENT '上下文摘要',
    message_count INT NOT NULL DEFAULT 0 COMMENT '消息数量',
    last_message_time DATETIME DEFAULT NULL COMMENT '最后消息时间',
    status VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态: active-进行中, archived-已归档',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    INDEX idx_user_id (user_id),
    INDEX idx_session_type (session_type),
    INDEX idx_status (status),
    INDEX idx_last_message_time (last_message_time),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI对话会话表';

-- AI对话消息表
CREATE TABLE IF NOT EXISTS ai_message (
    id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT '消息ID',
    conversation_id VARCHAR(32) NOT NULL COMMENT '会话ID',
    role VARCHAR(20) NOT NULL COMMENT '角色: user-用户, assistant-助手, system-系统',
    content TEXT NOT NULL COMMENT '消息内容',
    model VARCHAR(50) DEFAULT NULL COMMENT '使用的模型',
    tokens INT DEFAULT NULL COMMENT '消耗的Token数',
    latency_ms INT DEFAULT NULL COMMENT '响应延迟(毫秒)',
    law_references JSON DEFAULT NULL COMMENT '引用的法规(JSON数组)',
    feedback VARCHAR(10) DEFAULT NULL COMMENT '用户反馈: like-满意, dislike-不满意',
    feedback_content TEXT DEFAULT NULL COMMENT '反馈内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_role (role),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI对话消息表';

-- AI使用统计表
CREATE TABLE IF NOT EXISTS ai_usage_stats (
    id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT 'ID',
    user_id VARCHAR(32) NOT NULL COMMENT '用户ID',
    stat_date DATE NOT NULL COMMENT '统计日期',
    session_type VARCHAR(20) NOT NULL COMMENT '会话类型',
    request_count INT NOT NULL DEFAULT 0 COMMENT '请求次数',
    token_usage INT NOT NULL DEFAULT 0 COMMENT 'Token消耗总量',
    avg_latency_ms INT DEFAULT 0 COMMENT '平均响应延迟',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_date_type (user_id, stat_date, session_type),
    INDEX idx_date (stat_date),
    INDEX idx_session_type (session_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI使用统计表';

-- =============================================
-- 【模块九】考试模块表
-- =============================================

-- 题目分类表
CREATE TABLE IF NOT EXISTS exam_category (
    id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT '分类ID',
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    parent_id VARCHAR(32) DEFAULT NULL COMMENT '父级分类ID',
    sort INT NOT NULL DEFAULT 0 COMMENT '排序',
    status VARCHAR(10) NOT NULL DEFAULT '1' COMMENT '状态: 1-正常, 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目分类表';

-- 题目表
CREATE TABLE IF NOT EXISTS exam_question (
    id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT '题目ID',
    type VARCHAR(50) NOT NULL COMMENT '题型: single_choice-单选题, multiple_choice-多选题, judge-判断题, essay-简答题',
    difficulty VARCHAR(20) NOT NULL DEFAULT 'medium' COMMENT '难度: easy-简单, medium-中等, hard-困难',
    content TEXT NOT NULL COMMENT '题目内容',
    options TEXT COMMENT '选项(JSON格式: [{"key":"A","value":"选项内容"},{"key":"B","value":"选项内容"}])',
    answer TEXT COMMENT '正确答案(JSON格式: ["A"] 或 ["A","B"])',
    analysis TEXT COMMENT '答案解析',
    score INT NOT NULL DEFAULT 10 COMMENT '分值',
    category_id VARCHAR(32) DEFAULT NULL COMMENT '所属分类ID',
    law_id VARCHAR(32) DEFAULT NULL COMMENT '关联法规ID',
    tag VARCHAR(200) DEFAULT NULL COMMENT '标签(JSON数组)',
    usage_count INT NOT NULL DEFAULT 0 COMMENT '使用次数',
    correct_count INT NOT NULL DEFAULT 0 COMMENT '正确次数',
    creator_id VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
    status VARCHAR(10) NOT NULL DEFAULT '1' COMMENT '状态: 1-正常, 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_type (type),
    INDEX idx_difficulty (difficulty),
    INDEX idx_category_id (category_id),
    INDEX idx_law_id (law_id),
    INDEX idx_status (status),
    INDEX idx_creator_id (creator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目表';

-- 试卷表
CREATE TABLE IF NOT EXISTS exam_paper (
    id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT '试卷ID',
    title VARCHAR(200) NOT NULL COMMENT '试卷标题',
    description TEXT COMMENT '试卷说明',
    total_score INT NOT NULL DEFAULT 100 COMMENT '总分',
    pass_score INT NOT NULL DEFAULT 60 COMMENT '及格分数',
    time_limit INT NOT NULL DEFAULT 60 COMMENT '时限(分钟)',
    question_count INT NOT NULL DEFAULT 0 COMMENT '题目数量',
    creator_id VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
    source_type VARCHAR(50) NOT NULL DEFAULT 'manual' COMMENT '组卷方式: manual-手动组卷, auto-智能组卷, template-模板组卷',
    config TEXT COMMENT '组卷配置(JSON格式)',
    status VARCHAR(20) NOT NULL DEFAULT 'draft' COMMENT '状态: draft-草稿, published-已发布, archived-已归档',
    publish_time DATETIME DEFAULT NULL COMMENT '发布时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_source_type (source_type),
    INDEX idx_status (status),
    INDEX idx_creator_id (creator_id),
    INDEX idx_publish_time (publish_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷表';

-- 试卷题目关联表
CREATE TABLE IF NOT EXISTS exam_paper_question (
    id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT 'ID',
    paper_id VARCHAR(32) NOT NULL COMMENT '试卷ID',
    question_id VARCHAR(32) NOT NULL COMMENT '题目ID',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序顺序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_paper_question (paper_id, question_id),
    INDEX idx_paper_id (paper_id),
    INDEX idx_question_id (question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷题目关联表';

-- 考试记录表
CREATE TABLE IF NOT EXISTS exam_record (
    id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT '记录ID',
    paper_id VARCHAR(32) NOT NULL COMMENT '试卷ID',
    paper_title VARCHAR(200) DEFAULT NULL COMMENT '试卷标题(冗余)',
    user_id VARCHAR(32) NOT NULL COMMENT '用户ID',
    user_name VARCHAR(100) DEFAULT NULL COMMENT '用户姓名(冗余)',
    organization_id VARCHAR(32) DEFAULT NULL COMMENT '所属组织ID',
    organization_name VARCHAR(100) DEFAULT NULL COMMENT '组织名称(冗余)',
    status VARCHAR(50) NOT NULL DEFAULT 'not_started' COMMENT '状态: not_started-未开始, in_progress-进行中, submitted-已提交, graded-已评分',
    score INT DEFAULT NULL COMMENT '得分',
    is_passed TINYINT DEFAULT NULL COMMENT '是否及格: 0-不及格, 1-及格',
    correct_count INT DEFAULT 0 COMMENT '正确题数',
    total_count INT DEFAULT 0 COMMENT '总题数',
    correct_rate DECIMAL(5, 2) DEFAULT 0.00 COMMENT '正确率',
    start_time DATETIME DEFAULT NULL COMMENT '开始时间',
    submit_time DATETIME DEFAULT NULL COMMENT '提交时间',
    duration INT DEFAULT 0 COMMENT '用时(秒)',
    ip_address VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    device_info VARCHAR(200) DEFAULT NULL COMMENT '设备信息',
    creator_id VARCHAR(32) DEFAULT NULL COMMENT '下发人ID',
    task_id VARCHAR(32) DEFAULT NULL COMMENT '关联任务ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_paper_id (paper_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_creator_id (creator_id),
    INDEX idx_task_id (task_id),
    INDEX idx_organization_id (organization_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试记录表';

-- 答题记录表
CREATE TABLE IF NOT EXISTS exam_answer (
    id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT '记录ID',
    exam_record_id VARCHAR(32) NOT NULL COMMENT '考试记录ID',
    question_id VARCHAR(32) NOT NULL COMMENT '题目ID',
    question_type VARCHAR(50) DEFAULT NULL COMMENT '题型(冗余)',
    user_answer TEXT COMMENT '用户答案(JSON格式)',
    is_correct TINYINT DEFAULT 0 COMMENT '是否正确: 0-错误, 1-正确',
    score INT DEFAULT 0 COMMENT '得分',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '题目顺序',
    answer_time INT DEFAULT 0 COMMENT '答题用时(秒)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_exam_record_id (exam_record_id),
    INDEX idx_question_id (question_id),
    INDEX idx_is_correct (is_correct)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='答题记录表';

-- 智能组卷模板表
CREATE TABLE IF NOT EXISTS exam_template (
    id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT '模板ID',
    name VARCHAR(100) NOT NULL COMMENT '模板名称',
    description TEXT COMMENT '模板描述',
    total_score INT NOT NULL DEFAULT 100 COMMENT '目标总分',
    time_limit INT NOT NULL DEFAULT 60 COMMENT '时限(分钟)',
    pass_score INT NOT NULL DEFAULT 60 COMMENT '及格分数',
    config TEXT NOT NULL COMMENT '组卷配置(JSON格式)',
    usage_count INT NOT NULL DEFAULT 0 COMMENT '使用次数',
    creator_id VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
    status VARCHAR(10) NOT NULL DEFAULT '1' COMMENT '状态: 1-正常, 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_status (status),
    INDEX idx_creator_id (creator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智能组卷模板表';

-- =============================================
-- 【模块十】新闻/通知/评论扩展表
-- =============================================

-- 新闻/热点表
CREATE TABLE IF NOT EXISTS news (
    id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT '新闻ID',
    title VARCHAR(500) NOT NULL COMMENT '新闻标题',
    summary VARCHAR(1000) DEFAULT NULL COMMENT '摘要',
    content TEXT COMMENT '新闻内容',
    cover_url VARCHAR(500) DEFAULT NULL COMMENT '封面图片URL',
    type VARCHAR(50) NOT NULL DEFAULT 'news' COMMENT '类型: news-新闻, hotspot-热点, policy-政策解读, activity-活动',
    source VARCHAR(200) DEFAULT NULL COMMENT '来源',
    author VARCHAR(100) DEFAULT NULL COMMENT '作者',
    tags VARCHAR(500) DEFAULT NULL COMMENT '标签(JSON数组)',
    is_top TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶: 0-否, 1-是',
    is_hot TINYINT NOT NULL DEFAULT 0 COMMENT '是否热门: 0-否, 1-是',
    view_count INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
    like_count INT NOT NULL DEFAULT 0 COMMENT '点赞次数',
    comment_count INT NOT NULL DEFAULT 0 COMMENT '评论次数',
    status VARCHAR(20) NOT NULL DEFAULT 'published' COMMENT '状态: draft-草稿, published-已发布, archived-已归档',
    publish_time DATETIME DEFAULT NULL COMMENT '发布时间',
    creator_id VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_type (type),
    INDEX idx_status (status),
    INDEX idx_is_top (is_top),
    INDEX idx_is_hot (is_hot),
    INDEX idx_publish_time (publish_time),
    INDEX idx_creator_id (creator_id),
    FULLTEXT idx_title_content (title, content)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='新闻/热点表';

-- 通知表
CREATE TABLE IF NOT EXISTS notification (
    id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT '通知ID',
    title VARCHAR(200) NOT NULL COMMENT '通知标题',
    content TEXT COMMENT '通知内容',
    type VARCHAR(50) NOT NULL DEFAULT 'system' COMMENT '类型: system-系统通知, task-任务通知, training-培训通知, activity-活动通知, report-举报通知',
    priority VARCHAR(20) NOT NULL DEFAULT 'normal' COMMENT '优先级: low-低, normal-普通, high-高, urgent-紧急',
    target_type VARCHAR(50) NOT NULL DEFAULT 'all' COMMENT '目标类型: all-全体, user-指定用户, role-指定角色, organization-指定组织',
    target_value VARCHAR(500) DEFAULT NULL COMMENT '目标值(用户ID/角色/组织ID，多个用逗号分隔)',
    action_url VARCHAR(500) DEFAULT NULL COMMENT '点击跳转URL',
    action_params VARCHAR(500) DEFAULT NULL COMMENT '跳转参数(JSON)',
    cover_url VARCHAR(500) DEFAULT NULL COMMENT '封面图片URL',
    is_read TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读(针对个人通知)',
    read_time DATETIME DEFAULT NULL COMMENT '阅读时间',
    user_id VARCHAR(32) DEFAULT NULL COMMENT '接收用户ID(针对个人通知)',
    sender_id VARCHAR(32) DEFAULT NULL COMMENT '发送人ID',
    sender_name VARCHAR(100) DEFAULT NULL COMMENT '发送人名称',
    expire_time DATETIME DEFAULT NULL COMMENT '过期时间',
    status VARCHAR(20) NOT NULL DEFAULT 'published' COMMENT '状态: draft-草稿, published-已发布, cancelled-已取消',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_type (type),
    INDEX idx_priority (priority),
    INDEX idx_target_type (target_type),
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time),
    INDEX idx_expire_time (expire_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- 评论表
CREATE TABLE IF NOT EXISTS comment (
    id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT '评论ID',
    content TEXT NOT NULL COMMENT '评论内容',
    target_type VARCHAR(50) NOT NULL COMMENT '目标类型: news-新闻, training-培训, case_info-案例',
    target_id VARCHAR(32) NOT NULL COMMENT '目标ID',
    parent_id VARCHAR(32) DEFAULT NULL COMMENT '父评论ID(用于回复)',
    root_id VARCHAR(32) DEFAULT NULL COMMENT '根评论ID(一级评论ID)',
    reply_count INT NOT NULL DEFAULT 0 COMMENT '回复数量',
    like_count INT NOT NULL DEFAULT 0 COMMENT '点赞次数',
    status VARCHAR(20) NOT NULL DEFAULT 'published' COMMENT '状态: pending-待审核, published-已发布, deleted-已删除',
    user_id VARCHAR(32) NOT NULL COMMENT '评论用户ID',
    user_nickname VARCHAR(100) DEFAULT NULL COMMENT '评论用户昵称',
    user_avatar VARCHAR(500) DEFAULT NULL COMMENT '评论用户头像',
    reviewer_id VARCHAR(32) DEFAULT NULL COMMENT '审核人ID',
    review_time DATETIME DEFAULT NULL COMMENT '审核时间',
    review_remark VARCHAR(500) DEFAULT NULL COMMENT '审核备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_target_type (target_type),
    INDEX idx_target_id (target_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_root_id (root_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 用户点赞记录表
CREATE TABLE IF NOT EXISTS user_like (
    id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT 'ID',
    user_id VARCHAR(32) NOT NULL COMMENT '用户ID',
    target_type VARCHAR(50) NOT NULL COMMENT '目标类型: news-新闻, comment-评论',
    target_id VARCHAR(32) NOT NULL COMMENT '目标ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    UNIQUE KEY uk_user_target (user_id, target_type, target_id),
    INDEX idx_user_id (user_id),
    INDEX idx_target (target_type, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户点赞记录表';

-- 用户通知关联表
CREATE TABLE IF NOT EXISTS user_notification (
    id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT 'ID',
    notification_id VARCHAR(32) NOT NULL COMMENT '通知ID',
    user_id VARCHAR(32) NOT NULL COMMENT '用户ID',
    is_read TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读: 0-否, 1-是',
    read_time DATETIME DEFAULT NULL COMMENT '阅读时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_notification_id (notification_id),
    INDEX idx_user_id (user_id),
    UNIQUE KEY uk_notification_user (notification_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户通知关联表';

-- =============================================
-- 【模块十一】职员/员工管理表 (新增)
-- =============================================

-- 职员表
CREATE TABLE IF NOT EXISTS employee (
    id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT '职员ID',
    user_id VARCHAR(32) DEFAULT NULL COMMENT '关联用户ID',
    organization_id VARCHAR(32) DEFAULT NULL COMMENT '所属组织ID',
    parent_employee_id VARCHAR(32) DEFAULT NULL COMMENT '上级职员ID(用于汇报关系)',
    employee_no VARCHAR(50) DEFAULT NULL COMMENT '职员编号',
    name VARCHAR(100) NOT NULL COMMENT '姓名',
    gender VARCHAR(20) DEFAULT NULL COMMENT '性别: male-男, female-女',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    id_card VARCHAR(50) DEFAULT NULL COMMENT '身份证号',
    position VARCHAR(100) DEFAULT NULL COMMENT '职位/职务',
    position_level VARCHAR(50) DEFAULT NULL COMMENT '职级',
    title VARCHAR(100) DEFAULT NULL COMMENT '职称',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    status VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态: active-在职, leave-离职, transfer-调离',
    hire_date DATE DEFAULT NULL COMMENT '入职日期',
    is_leader TINYINT DEFAULT 0 COMMENT '是否负责人: 0-否, 1-是',
    -- 教育体系专用字段
    student_no VARCHAR(50) DEFAULT NULL COMMENT '学号(学生专用)',
    grade VARCHAR(20) DEFAULT NULL COMMENT '年级(学生专用)',
    class_name VARCHAR(100) DEFAULT NULL COMMENT '班级(学生专用)',
    guardian_name VARCHAR(100) DEFAULT NULL COMMENT '监护人姓名(学生专用)',
    guardian_phone VARCHAR(20) DEFAULT NULL COMMENT '监护人电话(学生专用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_user_id (user_id),
    INDEX idx_organization_id (organization_id),
    INDEX idx_parent_employee_id (parent_employee_id),
    INDEX idx_employee_no (employee_no),
    INDEX idx_name (name),
    INDEX idx_status (status),
    INDEX idx_position (position)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='职员表';

-- =============================================
-- 【模块十二】学生管理表 (新增)
-- =============================================

-- 学生表
CREATE TABLE IF NOT EXISTS student (
    id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT '学生ID',
    employee_id VARCHAR(32) DEFAULT NULL COMMENT '关联职员ID(可选)',
    student_no VARCHAR(50) DEFAULT NULL COMMENT '学籍号',
    student_name VARCHAR(100) NOT NULL COMMENT '学生姓名',
    gender VARCHAR(20) DEFAULT NULL COMMENT '性别: male/female',
    birth_date DATE DEFAULT NULL COMMENT '出生日期',
    id_card VARCHAR(50) DEFAULT NULL COMMENT '身份证号',
    nationality VARCHAR(50) DEFAULT '中国' COMMENT '国籍/地区',
    ethnicity VARCHAR(50) DEFAULT NULL COMMENT '民族',
    enrollment_year INT DEFAULT NULL COMMENT '入学年份',
    enrollment_type VARCHAR(20) DEFAULT NULL COMMENT '入学方式: exam-考试入学, transfer-转入, adjustment-调整',
    previous_school VARCHAR(200) DEFAULT NULL COMMENT '原学校(转学生)',
    education_level VARCHAR(20) DEFAULT NULL COMMENT '教育类别: regular-普通教育, vocational-职业教育',
    study_type VARCHAR(20) DEFAULT NULL COMMENT '学习方式: full_time-全日制, part_time-非全日制, correspondence-函授',
    school_id VARCHAR(32) DEFAULT NULL COMMENT '就读学校ID',
    class_id VARCHAR(32) DEFAULT NULL COMMENT '所属班级ID',
    grade INT DEFAULT NULL COMMENT '当前年级',
    student_status VARCHAR(20) NOT NULL DEFAULT 'studying' COMMENT '学籍状态: studying-在读, suspended-休学, transferred-转出, dropped-辍学, graduated-毕业, expelled-开除',
    phone VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    email VARCHAR(100) DEFAULT NULL COMMENT '电子邮箱',
    photo_url VARCHAR(500) DEFAULT NULL COMMENT '证件照URL',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_employee_id (employee_id),
    INDEX idx_student_no (student_no),
    INDEX idx_school_id (school_id),
    INDEX idx_class_id (class_id),
    INDEX idx_student_status (student_status),
    INDEX idx_grade (grade)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生表';

-- 监护人表
CREATE TABLE IF NOT EXISTS guardian (
    id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT '监护人ID',
    student_id VARCHAR(32) NOT NULL COMMENT '学生ID',
    guardian_name VARCHAR(100) NOT NULL COMMENT '监护人姓名',
    relation VARCHAR(50) NOT NULL COMMENT '与学生关系: father-父亲, mother-母亲, grandfather-祖父, grandmother-祖母, maternal_grandfather-外祖父, maternal_grandmother-外祖母, other-其他',
    id_card VARCHAR(50) DEFAULT NULL COMMENT '身份证号',
    phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    alternate_phone VARCHAR(20) DEFAULT NULL COMMENT '备用电话',
    email VARCHAR(100) DEFAULT NULL COMMENT '电子邮箱',
    employer VARCHAR(200) DEFAULT NULL COMMENT '工作单位',
    occupation VARCHAR(100) DEFAULT NULL COMMENT '职业',
    is_emergency_contact TINYINT DEFAULT 0 COMMENT '是否紧急联系人: 0-否, 1-是',
    priority INT DEFAULT 99 COMMENT '联系优先级(1最高)',
    address VARCHAR(500) DEFAULT NULL COMMENT '地址',
    status VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态: active-有效, inactive-无效',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_student_id (student_id),
    INDEX idx_phone (phone),
    INDEX idx_relation (relation),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='监护人表';

-- =============================================
-- 【模块十三】学校管理表 (新增)
-- =============================================

-- 学校表
CREATE TABLE IF NOT EXISTS school (
    id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT '学校ID',
    organization_id VARCHAR(32) DEFAULT NULL COMMENT '关联组织ID',
    school_name VARCHAR(200) NOT NULL COMMENT '学校名称',
    school_code VARCHAR(50) DEFAULT NULL COMMENT '学校代码(教育局统一编号)',
    school_type VARCHAR(50) DEFAULT NULL COMMENT '学校类型: primary-小学, junior_middle-初中, senior_middle-高中, vocational-职高, nine_year-九年一贯制, twelve_year-十二年一贯制, complete-完全中学',
    school_level VARCHAR(50) DEFAULT NULL COMMENT '学校评级: provincial_key-省级示范, city_key-市级示范, county_key-县级示范, ordinary-普通',
    school_system VARCHAR(20) DEFAULT NULL COMMENT '学制: 6年/3年/9年/12年',
    total_grades INT DEFAULT 0 COMMENT '年级总数',
    total_classes INT DEFAULT 0 COMMENT '班级总数',
    total_students INT DEFAULT 0 COMMENT '学生总数',
    total_teachers INT DEFAULT 0 COMMENT '教师总数',
    established_year INT DEFAULT NULL COMMENT '建校年份',
    approval_number VARCHAR(100) DEFAULT NULL COMMENT '办学批准文号',
    school_area DECIMAL(12, 2) DEFAULT NULL COMMENT '校园面积(平方米)',
    building_area DECIMAL(12, 2) DEFAULT NULL COMMENT '建筑面积(平方米)',
    school_nature VARCHAR(20) DEFAULT NULL COMMENT '学校性质: public-公办, private-民办, shared-共建',
    contact_person VARCHAR(100) DEFAULT NULL COMMENT '联系人',
    contact_phone VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    contact_address VARCHAR(500) DEFAULT NULL COMMENT '联系地址',
    education_bureau_id VARCHAR(32) DEFAULT NULL COMMENT '所属教育局组织ID',
    jurisdiction_type VARCHAR(20) DEFAULT NULL COMMENT '管辖类型: direct-直属, affiliated-附属',
    status VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态: active-正常, suspended-停办, merged-合并',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_organization_id (organization_id),
    INDEX idx_school_code (school_code),
    INDEX idx_school_type (school_type),
    INDEX idx_school_level (school_level),
    INDEX idx_education_bureau_id (education_bureau_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学校表';

-- 班级表
CREATE TABLE IF NOT EXISTS school_class (
    id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT '班级ID',
    school_id VARCHAR(32) NOT NULL COMMENT '所属学校ID',
    class_name VARCHAR(100) NOT NULL COMMENT '班级名称(如: 初一(1)班)',
    class_code VARCHAR(50) DEFAULT NULL COMMENT '班级编号',
    class_number INT DEFAULT NULL COMMENT '班号(如: 1班=1, 2班=2)',
    grade INT DEFAULT NULL COMMENT '年级(1-12)',
    grade_name VARCHAR(50) DEFAULT NULL COMMENT '年级名称(如: 初一, 高二)',
    class_type VARCHAR(50) DEFAULT NULL COMMENT '班级类型: ordinary-普通班, key-重点班, art-美术班, music-音乐班, sports-体育班, international-国际班, science-理科班, liberal-文科班',
    student_limit INT DEFAULT 50 COMMENT '最大学生数',
    current_students INT DEFAULT 0 COMMENT '当前学生数',
    head_teacher_id VARCHAR(32) DEFAULT NULL COMMENT '班主任ID(关联employee表)',
    head_teacher_name VARCHAR(100) DEFAULT NULL COMMENT '班主任姓名',
    building VARCHAR(100) DEFAULT NULL COMMENT '教学楼',
    floor INT DEFAULT NULL COMMENT '楼层',
    room_number VARCHAR(50) DEFAULT NULL COMMENT '教室号',
    academic_year VARCHAR(20) DEFAULT NULL COMMENT '学年(如: 2024-2025)',
    semester INT DEFAULT 1 COMMENT '学期(1-2)',
    status VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态: active-在读, graduated-已毕业, cancelled-已取消',
    graduate_year INT DEFAULT NULL COMMENT '毕业年份(毕业班使用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_school_id (school_id),
    INDEX idx_grade (grade),
    INDEX idx_academic_year (academic_year),
    INDEX idx_semester (semester),
    INDEX idx_head_teacher_id (head_teacher_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';

-- =============================================
-- 【模块十四】初始化数据
-- =============================================

-- 插入组织类型配置
INSERT INTO organization_type (id, type_code, type_name, icon, color, sort_order) VALUES
('org_type_gov', 'government', '政府部门', 'government', '#1890ff', 1),
('org_type_ent', 'enterprise', '企业单位', 'enterprise', '#52c41a', 2),
('org_type_edu', 'education', '教育机构', 'school', '#722ed1', 3),
('org_type_law', 'law_firm', '律师事务所', 'bank', '#fa8c16', 4),
('org_type_plat', 'platform', '平台运营', 'appstore', '#eb2f96', 5);

-- 插入政府组织分类
INSERT INTO organization_category (id, type_id, category_code, category_name, sort_order) VALUES
('org_cat_province', 'org_type_gov', 'province', '省级', 1),
('org_cat_city', 'org_type_gov', 'city', '市级', 2),
('org_cat_county', 'org_type_gov', 'county', '县级', 3),
('org_cat_town', 'org_type_gov', 'town', '乡镇级', 4),
('org_cat_village', 'org_type_gov', 'village', '村级', 5);

-- 插入企业组织分类
INSERT INTO organization_category (id, type_id, category_code, category_name, sort_order) VALUES
('org_cat_headquarters', 'org_type_ent', 'headquarters', '总公司', 1),
('org_cat_branch', 'org_type_ent', 'branch', '分公司', 2),
('org_cat_department', 'org_type_ent', 'department', '部门', 3);

-- 插入教育组织分类
INSERT INTO organization_category (id, type_id, category_code, category_name, sort_order) VALUES
('org_cat_bureau', 'org_type_edu', 'bureau', '教育局', 1),
('org_cat_school', 'org_type_edu', 'school', '学校', 2);

-- 插入超级管理员用户 (密码: admin123)
INSERT INTO sys_user (id, username, password, nickname, role, status) VALUES
('$1', 'admin', '$2b$10$4JWgT3b3CAnTqZGXxvoaBuY0rL3/mBJmkZNqzm5fMcRN15ugIgJeG', '超级管理员', 'super_admin', '1'),
('$2', 'gov_user', '$2b$10$4JWgT3b3CAnTqZGXxvoaBuY0rL3/mBJmkZNqzm5fMcRN15ugIgJeG', '政府用户', 'government', '1'),
('$3', 'ent_user', '$2b$10$4JWgT3b3CAnTqZGXxvoaBuY0rL3/mBJmkZNqzm5fMcRN15ugIgJeG', '企业用户', 'enterprise', '1'),
('$4', 'law_user', '$2b$10$4JWgT3b3CAnTqZGXxvoaBuY0rL3/mBJmkZNqzm5fMcRN15ugIgJeG', '律所用户', 'law_firm', '1'),
('$5', 'test_user', '$2b$10$4JWgT3b3CAnTqZGXxvoaBuY0rL3/mBJmkZNqzm5fMcRN15ugIgJeG', '测试用户', 'user', '1');

-- 插入组织数据 (已扩展字段)
INSERT INTO sys_organization (id, name, level, parent_id, code, description, type, category, region_code, leader_name, leader_phone, contact_address) VALUES
('org_province', '某省司法厅', 1, NULL, 'PROV001', '省级司法行政机关', 'government', 'province', '610000', '张厅长', '13800000001', '某省省会城市'),
('org_city', '某市司法局', 2, 'org_province', 'CITY001', '市级司法行政机关', 'government', 'city', '610100', '李局长', '13800000002', '某市'),
('org_county', '某县司法局', 3, 'org_city', 'COUNTY001', '县级司法行政机关', 'government', 'county', '610101', '王局长', '13800000003', '某县'),
('org_town', '某镇司法所', 4, 'org_county', 'TOWN001', '乡镇级司法所', 'government', 'town', '610101001', '刘所长', '13800000004', '某镇'),
('org_enterprise1', '某塑料制品企业', 3, 'org_city', 'ENT001', '塑料制品生产企业', 'enterprise', 'headquarters', NULL, '赵总', '13800000005', '某工业园区'),
('org_enterprise2', '某餐饮连锁企业', 3, 'org_city', 'ENT002', '餐饮服务企业', 'enterprise', 'headquarters', NULL, '钱总', '13800000006', '某商业中心'),
('org_lawfirm1', '某律师事务所', 3, 'org_city', 'LAW001', '入驻律所', 'law_firm', NULL, NULL, '孙律师', '13800000007', '某写字楼'),
('org_school1', '某市第一中学', 3, 'org_city', 'SCH001', '市级重点中学', 'education', 'school', '610101', '周校长', '13800000008', '某市中心'),
('org_edu_bureau', '某市教育局', 2, 'org_province', 'EDU001', '市级教育行政机关', 'education', 'bureau', '610100', '吴主任', '13800000009', '某市');

-- 插入角色数据
INSERT INTO sys_role (id, code, name, description, sort) VALUES
('role_admin', 'super_admin', '超级管理员', '系统最高权限', 1),
('role_gov', 'government', '政府监管员', '政府监管角色', 2),
('role_ent', 'enterprise', '企业管理员', '企业管理角色', 3),
('role_law', 'law_firm', '律所管理员', '律所管理角色', 4),
('role_user', 'user', '普通用户', '普通用户角色', 5);

-- 插入职员数据
INSERT INTO employee (id, user_id, organization_id, employee_no, name, gender, phone, position, status, hire_date, is_leader) VALUES
('emp_001', '$1', 'org_province', 'EMP001', '张管理员', 'male', '13800001001', '系统管理员', 'active', '2024-01-01', 1),
('emp_002', '$2', 'org_city', 'EMP002', '李处长', 'female', '13800001002', '处长', 'active', '2023-06-01', 1),
('emp_003', '$3', 'org_enterprise1', 'EMP003', '王经理', 'male', '13800001003', '环保经理', 'active', '2023-03-01', 1),
('emp_004', '$4', 'org_lawfirm1', 'EMP004', '孙律师', 'female', '13800001004', '主办律师', 'active', '2022-09-01', 1),
('emp_005', '$5', 'org_town', 'EMP005', '刘所长', 'male', '13800001005', '所长', 'active', '2024-02-01', 1);

-- 插入学校数据
INSERT INTO school (id, organization_id, school_name, school_code, school_type, school_level, school_nature, total_grades, total_classes, total_students, total_teachers, contact_person, contact_phone) VALUES
('school_001', 'org_school1', '某市第一中学', 'SCH001', 'senior_middle', 'city_key', 'public', 3, 36, 1800, 120, '周校长', '13800001008'),
('school_002', 'org_school1', '某市实验小学', 'SCH002', 'primary', 'county_key', 'public', 6, 24, 960, 48, '郑校长', '13800001010');

-- 插入班级数据
INSERT INTO school_class (id, school_id, class_name, class_number, grade, grade_name, class_type, student_limit, current_students, head_teacher_name, academic_year, semester, status) VALUES
('class_001', 'school_001', '高一(1)班', 1, 10, '高一', 'key', 50, 48, '陈老师', '2025-2026', 1, 'active'),
('class_002', 'school_001', '高一(2)班', 2, 10, '高一', 'ordinary', 50, 50, '林老师', '2025-2026', 1, 'active'),
('class_003', 'school_001', '高二(1)班', 1, 11, '高二', 'key', 50, 47, '周老师', '2025-2026', 1, 'active'),
('class_004', 'school_002', '三年级(1)班', 1, 3, '三年级', 'ordinary', 45, 42, '吴老师', '2025-2026', 1, 'active');

-- 插入学生数据
INSERT INTO student (id, student_no, student_name, gender, birth_date, nationality, ethnicity, enrollment_year, enrollment_type, education_level, study_type, school_id, class_id, grade, student_status, phone) VALUES
('student_001', 'S2025001', '王小明', 'male', '2010-05-15', '中国', '汉族', 2025, 'exam', 'regular', 'full_time', 'school_001', 'class_001', 10, 'studying', '13800010001'),
('student_002', 'S2025002', '李小红', 'female', '2010-08-20', '中国', '汉族', 2025, 'exam', 'regular', 'full_time', 'school_001', 'class_002', 10, 'studying', '13800010002'),
('student_003', 'S2024001', '张小华', 'male', '2009-12-01', '中国', '汉族', 2024, 'exam', 'regular', 'full_time', 'school_001', 'class_003', 11, 'studying', '13800010003');

-- 插入监护人数据
INSERT INTO guardian (id, student_id, guardian_name, relation, phone, is_emergency_contact, priority, status) VALUES
('guardian_001', 'student_001', '王建国', 'father', '13900001001', 1, 1, 'active'),
('guardian_002', 'student_001', '李秀英', 'mother', '13900001002', 0, 2, 'active'),
('guardian_003', 'student_002', '李国强', 'father', '13900001003', 1, 1, 'active'),
('guardian_004', 'student_002', '张丽华', 'mother', '13900001004', 0, 2, 'active'),
('guardian_005', 'student_003', '张海东', 'father', '13900001005', 1, 1, 'active'),
('guardian_006', 'student_003', '刘芳', 'mother', '13900001006', 0, 2, 'active');

-- 插入示例法规
INSERT INTO law (id, title, level, category, content, publish_date, effective_date, status) VALUES
('law_001', '中华人民共和国固体废物污染环境防治法', 'national', '环境保护', '为了防治固体废物污染环境，保障人体健康，维护生态安全，促进经济社会可持续发展，制定本法。\n\n本法所称固体废物，是指在生产、生活和其他活动中产生的丧失原有利用价值或者虽未丧失利用价值但被抛弃或者放弃的固态、半固态和置于容器中的气态的物品、物质以及法律、行政法规规定纳入固体废物管理的物品、物质。\n\n禁止任何单位或者个人向江河、湖泊、运河、渠道、水库及其最高水位线以下的滩地和岸坡以及法律法规规定以外的其他水体倾倒固体废物。', '2020-04-29', '2020-09-01', 'effective'),
('law_002', '中华人民共和国产品质量法', 'national', '产品质量', '为了加强对产品质量的监督管理，提高产品质量水平，明确产品质量责任，保护消费者的合法权益，维护社会经济秩序，制定本法。\n\n在中华人民共和国境内从事产品生产、销售活动，必须遵守本法。\n\n禁止伪造或者冒用认证标志等质量标志；禁止伪造产品的产地，伪造或者冒用他人的厂名、厂址；禁止在生产、销售的产品中掺杂、掺假，以假充真，以次充好。', '2020-07-27', '2020-07-27', 'effective'),
('law_003', '某省塑料制品污染防治条例', 'provincial', '污染防治', '为防治塑料制品污染，保护和改善生态环境，推进生态文明建设，根据《中华人民共和国固体废物污染环境防治法》和相关法律、法规，结合本省实际，制定本条例。\n\n本条例所称塑料制品，是指以合成树脂为主要原料，添加助剂制成的具有可塑性的材料及其制品。\n\n县级以上人民政府应当加强对塑料制品污染防治工作的领导，将塑料制品污染防治工作纳入国民经济和社会发展规划、生态环境保护规划。', '2023-01-01', '2023-05-01', 'effective');

-- 插入示例任务
INSERT INTO task (id, title, content, type, status, creator_id, organization_id, start_time, end_time, target_count, completed_count) VALUES
('task_001', '白色污染治理专项行动', '开展白色污染治理专项执法检查行动', 'government', 'in_progress', '2', 'org_city', '2026-01-01 00:00:00', '2026-06-30 23:59:59', 100, 35),
('task_002', '企业环保培训', '组织企业员工参加环保法律法规培训', 'enterprise', 'pending', '3', 'org_enterprise1', '2026-04-01 00:00:00', '2026-04-30 23:59:59', 50, 0),
('task_003', '社区普法活动', '在社区开展白色污染防治普法宣传活动', 'government', 'completed', '2', 'org_town', '2026-03-01 00:00:00', '2026-03-31 23:59:59', 20, 20);

-- 插入任务下发记录
INSERT INTO task_dispatch (id, task_id, source_organization_id, target_organization_id, dispatch_level, status, target_count, completed_count, progress, deadline, dispatch_time) VALUES
('dispatch_001', 'task_001', 'org_city', 'org_county', 1, 'accepted', 100, 35, 35, '2026-06-30', '2026-01-05'),
('dispatch_002', 'task_001', 'org_county', 'org_town', 2, 'in_progress', 30, 15, 50, '2026-05-31', '2026-01-10'),
('dispatch_003', 'task_002', 'org_enterprise1', NULL, 1, 'pending', 50, 0, 0, '2026-04-30', '2026-03-28');

-- 插入任务进度上报
INSERT INTO task_progress_report (id, dispatch_id, reporter_id, reporter_name, report_type, completed_count, total_completed, progress, content) VALUES
('report_001', 'dispatch_001', '$2', '李处长', 'progress', 35, 35, 35, '已完成第一季度任务的35%，共计检查企业35家。'),
('report_002', 'dispatch_002', '$5', '刘所长', 'progress', 15, 15, 50, '已完成辖区任务的50%，开展社区宣传活动15场。');

-- 插入示例培训
INSERT INTO training (id, title, content, type, duration, creator_id, status, view_count, completed_count) VALUES
('train_001', '塑料污染防治法律法规解读', '详细介绍塑料污染防治相关法律法规', 'video', 120, '1', 'published', 156, 45),
('train_002', '企业合规经营指南', '指导企业如何合规经营', 'document', 60, '1', 'published', 89, 32),
('train_003', '环保知识考核', '测试环保知识掌握程度', 'exam', 30, '1', 'published', 234, 180);

-- 插入示例案例
INSERT INTO case_info (id, title, type, description, result, lawyer_name, status, view_count) VALUES
('case_001', '某塑料厂违规排污案', 'administrative', '某塑料制品厂违反环保法规，未经处理直接排放污水', '罚款50万元，责令停产整顿', '张律师', 1, 1256),
('case_002', '某超市使用不可降解塑料袋案', 'administrative', '某超市违规使用不可降解塑料购物袋', '罚款2万元，要求整改', '李律师', 1, 876),
('case_003', '某企业非法倾倒废塑料案', 'environmental', '某企业非法倾倒废塑料污染环境', '罚款100万元，追究刑事责任', '王律师', 1, 2345);

-- 插入示例举报
INSERT INTO report (id, title, content, type, location, status, reporter_name, reporter_phone) VALUES
('report_rep_001', '某工厂违规排放', '发现某工厂夜间违规排放污水', 'environmental_pollution', '某工业园区', 'pending', '张三', '13800138001'),
('report_rep_002', '超市使用不可降解塑料袋', '某大型超市仍在使用不可降解塑料袋', 'illegal_use', '某商业中心', 'processing', '李四', '13800138002'),
('report_rep_003', '非法倾倒废塑料', '发现有人非法倾倒废塑料', 'illegal_production', '某城乡结合部', 'resolved', '王五', '13800138003');

-- 插入示例新闻
INSERT INTO news (id, title, summary, content, cover_url, type, source, author, tags, is_top, is_hot, view_count, status, publish_time) VALUES
('news_001', '某省启动白色污染治理攻坚行动', '近日，某省正式印发《白色污染治理攻坚行动方案》，标志着该省进入全面治理白色污染的新阶段。', '近日，某省正式印发《白色污染治理攻坚行动方案》，标志着该省进入全面治理白色污染的新阶段。\n\n本次行动方案明确了以下重点任务：\n\n一、加强源头管控\n严格控制塑料制品生产环节，推广使用可降解塑料制品。\n\n二、完善回收体系\n建立健全废旧塑料回收利用网络，提高回收利用率。\n\n三、强化执法监管\n加大对违法行为的查处力度，形成有效震慑。\n\n四、广泛宣传教育\n通过多种形式开展环保宣传，提高公众环保意识。', '/static/news/cover1.jpg', 'hotspot', '省生态环境厅', '省生态环境厅', '["白色污染","攻坚行动","环保政策"]', 1, 1, 2568, 'published', NOW()),
('news_002', '全国首部！这个地方出台禁塑专项法规', '某市近日出台全国首部针对不可降解塑料制品的地方性法规，将于下月起正式实施。', '某市近日出台全国首部针对不可降解塑料制品的地方性法规，将于下月起正式实施。\n\n该法规明确规定：\n\n1. 禁止生产、销售不可降解塑料袋\n2. 餐饮行业禁止使用不可降解塑料餐具\n3. 商场超市不得无偿提供塑料袋\n4. 违规者最高罚款50万元', '/static/news/cover2.jpg', 'policy', '市人大', '市人大法制委', '["禁塑","地方立法","法规"]', 0, 1, 1893, 'published', NOW()),
('news_003', '可降解塑料行业迎来发展新机遇', '随着国家禁塑政策的深入推进，可降解塑料行业迎来快速发展期。', '随着国家禁塑政策的深入推进，可降解塑料行业迎来快速发展期。\n\n据业内人士分析，未来3-5年将是可降解塑料行业的黄金发展期。预计到2028年，市场规模将突破500亿元。', '/static/news/cover3.jpg', 'news', '经济日报', '王记者', '["可降解塑料","行业分析","市场"]', 0, 0, 956, 'published', NOW());

-- 插入示例通知
INSERT INTO notification (id, title, content, type, priority, target_type, status) VALUES
('notif_001', '系统通知：平台功能更新公告', '尊敬的用户，平台已完成新一轮功能更新，新增了新闻评论、通知中心等功能，欢迎体验！', 'system', 'normal', 'all', 'published'),
('notif_002', '培训通知：下周将开展环保法规培训', '各位同事，下周将组织一场关于白色污染防治法律法规的培训，请准时参加。', 'training', 'high', 'all', 'published'),
('notif_003', '任务提醒：您有新的任务待处理', '您有一个新的任务"企业合规检查"需要处理，请尽快登录系统查看详情。', 'task', 'normal', 'user', 'published');

-- 插入示例评论
INSERT INTO comment (id, content, target_type, target_id, user_id, user_nickname, status) VALUES
('comment_001', '这个政策来得太及时了，支持！', 'news', 'news_001', '$5', '环保达人', 'published'),
('comment_002', '希望能够真正落实到位，不要只是说说而已。', 'news', 'news_001', '$5', '绿色生活', 'published'),
('comment_003', '文章写得很详细，给作者点赞！', 'news', 'news_002', '$5', '法律爱好者', 'published');

-- 更新评论的根评论ID
UPDATE comment SET root_id = id WHERE root_id IS NULL;

-- =============================================
-- 【模块十五】考试模块初始数据
-- =============================================

-- 插入题目分类
INSERT INTO exam_category (id, name, parent_id, sort) VALUES
('cat_001', '环境保护法', NULL, 1),
('cat_002', '白色污染治理', 'cat_001', 1),
('cat_003', '塑料制品管理', 'cat_001', 2),
('cat_004', '企业合规', NULL, 2),
('cat_005', '生产许可', 'cat_004', 1),
('cat_006', '排污许可', 'cat_004', 2),
('cat_007', '执法检查', NULL, 3),
('cat_008', '行政处罚', 'cat_007', 1),
('cat_009', '刑事追究', 'cat_007', 2);

-- 插入示例题目 - 单选题
INSERT INTO exam_question (id, type, difficulty, content, options, answer, analysis, score, category_id) VALUES
('q_001', 'single_choice', 'easy', '根据《中华人民共和国固体废物污染环境防治法》，下列哪种行为是禁止的？', '[{"key":"A","value":"依法建设固体废物贮存设施"},{"key":"B","value":"擅自倾倒工业固体废物"},{"key":"C","value":"按照规定申报固体废物产生情况"},{"key":"D","value":"开展固体废物综合利用"}]', '["B"]', '《固废法》明确规定，禁止擅自倾倒、堆放、丢弃、遗撒工业固体废物。', 10, 'cat_002'),
('q_002', 'single_choice', 'medium', '下列关于塑料购物袋的说法，错误的是？', '[{"key":"A","value":"厚度不低于0.025毫米"},{"key":"B","value":"实行有偿使用制度"},{"key":"C","value":"可以在所有超市免费提供"},{"key":"D","value":"应当明码标价"}]', '["C"]', '根据相关规定，禁止免费提供塑料购物袋，零售环节实行明码标价和有偿使用制度。', 10, 'cat_003'),
('q_003', 'single_choice', 'hard', '企业非法倾倒危险废物多少吨以上，可以构成污染环境罪？', '[{"key":"A","value":"1吨"},{"key":"B","value":"3吨"},{"key":"C","value":"10吨"},{"key":"D","value":"100吨"}]', '["A"]', '根据《最高人民法院、最高人民检察院关于办理环境污染刑事案件适用法律若干问题的解释》，非法倾倒危险废物3吨以上的，即可构成污染环境罪。', 10, 'cat_009');

-- 插入示例题目 - 多选题
INSERT INTO exam_question (id, type, difficulty, content, options, answer, analysis, score, category_id) VALUES
('q_004', 'multiple_choice', 'medium', '根据相关规定，以下哪些属于禁止或限制的塑料制品？（多选）', '[{"key":"A","value":"厚度低于0.025毫米的塑料购物袋"},{"key":"B","value":"一次性塑料餐具"},{"key":"C","value":"塑料快递包装"},{"key":"D","value":"可降解塑料购物袋"}]', '["A","B","C"]', '可降解塑料购物袋是鼓励使用的，而厚度不达标的一次性塑料制品是被限制或禁止的。', 15, 'cat_003'),
('q_005', 'multiple_choice', 'hard', '企业申领排污许可证需要具备哪些条件？（多选）', '[{"key":"A","value":"依法取得建设项目环境影响报告书（表）批准文件"},{"key":"B","value":"污染物排放符合污染物排放标准要求"},{"key":"C","value":"依法取得营业执照"},{"key":"D","value":"具备污染物处理能力"}]', '["A","B","D"]', '排污许可证的申领需要满足环保相关条件，营业执照属于基本经营资质，不属于环保审批条件。', 15, 'cat_006');

-- 插入示例题目 - 判断题
INSERT INTO exam_question (id, type, difficulty, content, answer, analysis, score, category_id) VALUES
('q_006', 'judge', 'easy', '单位和个人可以向江河、湖泊、运河、渠道、水库及其最高水位线以下的滩地和岸坡倾倒固体废物。', '["false"]', '《固体废物污染环境防治法》明确规定，禁止任何单位或者个人向水体倾倒固体废物。', 5, 'cat_002'),
('q_007', 'judge', 'medium', '企业事业单位和其他生产经营者应当建立健全环境管理制度，采取有效措施，防治固体废物污染环境。', '["true"]', '这是《固体废物污染环境防治法》对企事业单位规定的法定义务。', 5, 'cat_004'),
('q_008', 'judge', 'hard', '产生、收集、贮存、运输、利用、处置固体废物的单位，应当依法取得排污许可证。', '["false"]', '并非所有产生固体废物的单位都需要取得排污许可证，这取决于具体行业和管理要求。部分重点排污单位需要取得许可证。', 5, 'cat_006');

-- 插入示例题目 - 简答题
INSERT INTO exam_question (id, type, difficulty, content, answer, analysis, score, category_id) VALUES
('q_009', 'essay', 'hard', '请简述企业防治固体废物污染环境的主要措施有哪些？', '["1.建立固体废物管理台账；2.分类收集、贮存固体废物；3.委托有资质的单位处置；4.开展清洁生产，减少固体废物产生；5.定期报告固体废物产生、处置情况。"]', '企业应从源头减量、过程控制、末端处置三个环节入手，建立完善的固体废物管理制度。', 20, 'cat_004');

-- 插入智能组卷模板
INSERT INTO exam_template (id, name, description, total_score, time_limit, pass_score, config, creator_id) VALUES
('tpl_001', '企业环保基础考核', '适用于企业员工入职培训考核', 100, 60, 60,
 '{"questionTypes": [{"type": "single_choice", "count": 10, "score": 5}, {"type": "judge", "count": 10, "score": 3}, {"type": "multiple_choice", "count": 2, "score": 10}, {"type": "essay", "count": 1, "score": 20}], "difficulty": {"easy": 50, "medium": 30, "hard": 20}, "categories": [{"categoryId": "cat_001", "weight": 60}, {"categoryId": "cat_004", "weight": 40}]}',
 '1'),
('tpl_002', '执法人员资格考核', '适用于执法人员上岗考核', 100, 90, 70,
 '{"questionTypes": [{"type": "single_choice", "count": 15, "score": 4}, {"type": "judge", "count": 10, "score": 2}, {"type": "multiple_choice", "count": 5, "score": 6}, {"type": "essay", "count": 2, "score": 15}], "difficulty": {"easy": 20, "medium": 50, "hard": 30}, "categories": [{"categoryId": "cat_007", "weight": 70}, {"categoryId": "cat_001", "weight": 30}]}',
 '1');

-- =============================================
-- 【模块十六】AI对话初始数据
-- =============================================

-- 插入示例AI会话
INSERT INTO ai_conversation (id, user_id, session_type, title, message_count, status, create_time) VALUES
('conv_001', '$5', 'law', '关于塑料袋使用的问题咨询', 4, 'active', NOW()),
('conv_002', '$5', 'material', '生成环保宣传海报文案', 2, 'archived', DATE_SUB(NOW(), INTERVAL 7 DAY)),
('conv_003', '$2', 'case', '某企业违规案例分析', 3, 'active', DATE_SUB(NOW(), INTERVAL 2 DAY));

-- 插入示例AI消息
INSERT INTO ai_message (id, conversation_id, role, content, create_time) VALUES
('msg_001', 'conv_001', 'user', '企业可以使用的塑料袋类型有哪些？', NOW()),
('msg_002', 'conv_001', 'assistant', '根据《固体废物污染环境防治法》相关规定，企业在使用塑料袋时应注意以下几点：

1. **可降解塑料袋**：优先使用符合国家标准的可降解塑料袋，这类塑料袋在一定条件下可以自然分解，减少环境污染。

2. **可重复使用塑料袋**：鼓励企业使用可重复使用的塑料袋，如布袋、无纺布袋等，既环保又经济。

3. **厚度要求**：根据相关规定，塑料购物袋的厚度不应小于0.025毫米。

4. **标识要求**：塑料袋上应标明生产日期、生产厂家等信息。

建议企业建立塑料袋使用管理制度，规范采购和使用流程。', NOW()),
('msg_003', 'conv_001', 'user', '那餐饮行业呢？', NOW()),
('msg_004', 'conv_001', 'assistant', '餐饮行业在塑料袋使用方面有更严格的要求：

1. **禁止使用不可降解一次性塑料餐具**
   - 不可降解一次性塑料餐具包括：塑料吸管、塑料刀叉、塑料餐盒等
   - 应使用可降解材料或纸质替代品

2. **外卖包装要求**
   - 外卖平台应减少一次性塑料包装
   - 鼓励使用可循环利用的包装方式

3. **处罚措施**
   - 违规者将面临警告、罚款等处罚
   - 情节严重的可能被责令停业整顿

如果您是餐饮企业负责人，建议尽快排查并替换不符合要求的塑料制品。', DATE_SUB(NOW(), INTERVAL 1 HOUR));

-- 更新会话的最后消息时间和消息数量
UPDATE ai_conversation SET last_message_time = NOW() WHERE id = 'conv_001';
UPDATE ai_conversation SET message_count = 4 WHERE id = 'conv_001';

-- =============================================
-- 【模块十七】存储过程和定时任务
-- =============================================

DELIMITER //
DROP PROCEDURE IF EXISTS update_task_status//
CREATE PROCEDURE update_task_status()
BEGIN
    -- 将过期的进行中任务标记为已逾期
    UPDATE task 
    SET status = 'overdue' 
    WHERE status = 'in_progress' 
      AND end_time < NOW() 
      AND deleted = 0;
    
    -- 将过期的下发记录标记为已逾期
    UPDATE task_dispatch 
    SET status = 'overdue' 
    WHERE status IN ('pending', 'accepted') 
      AND deadline < NOW() 
      AND deleted = 0;
END //
DELIMITER ;

-- 设置事件调度器
SET GLOBAL event_scheduler = ON;

-- 创建每日执行的事件
DELIMITER //
DROP EVENT IF EXISTS daily_task_status_update//
CREATE EVENT daily_task_status_update
ON SCHEDULE EVERY 1 DAY
STARTS CURRENT_TIMESTAMP
DO
BEGIN
    CALL update_task_status();
END //
DELIMITER ;

-- =============================================
-- 初始化完成
-- =============================================

SELECT '========================================' AS '';
SELECT '  绿法通数据库初始化完成！' AS message;
SELECT '  版本：v2.0 (完整版)' AS '';
SELECT '  数据库包含以下模块：' AS '';
SELECT '  1. 系统管理表（用户、角色、组织）' AS '';
SELECT '  2. 组织配置表（类型、分类）' AS '';
SELECT '  3. 法规管理表' AS '';
SELECT '  4. 任务管理表（含下发记录）' AS '';
SELECT '  5. 任务进度上报表' AS '';
SELECT '  6. 培训管理表' AS '';
SELECT '  7. 案例管理表' AS '';
SELECT '  8. 举报管理表' AS '';
SELECT '  9. AI素材表' AS '';
SELECT '  10. AI对话模块表' AS '';
SELECT '  11. 考试模块表' AS '';
SELECT '  12. 新闻/通知/评论扩展表' AS '';
SELECT '  13. 职员/员工管理表' AS '';
SELECT '  14. 学生管理表' AS '';
SELECT '  15. 监护人表' AS '';
SELECT '  16. 学校管理表' AS '';
SELECT '  17. 班级管理表' AS '';
SELECT '  18. 存储过程和定时任务' AS '';
SELECT '========================================' AS '';
