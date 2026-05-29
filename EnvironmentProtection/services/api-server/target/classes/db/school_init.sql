-- =============================================
-- 学校管理模块数据库表结构
-- =============================================

-- 1. 学校扩展信息表
CREATE TABLE IF NOT EXISTS `school` (
    `id` VARCHAR(32) PRIMARY KEY COMMENT '主键ID',
    `organization_id` VARCHAR(32) NOT NULL COMMENT '关联组织ID',
    
    -- 学校基本信息
    `school_name` VARCHAR(100) NOT NULL COMMENT '学校名称',
    `school_code` VARCHAR(50) COMMENT '学校代码(教育局统一编号)',
    
    -- 学校类型分类
    `school_type` VARCHAR(20) NOT NULL COMMENT '学校类型: primary-小学, junior_middle-初中, senior_middle-高中, vocational-职高, nine_year-九年一贯制, twelve_year-十二年一贯制, complete-完全中学',
    
    -- 学校评级
    `school_level` VARCHAR(20) COMMENT '学校评级: provincial_key-省级示范, city_key-市级示范, county_key-县级示范, ordinary-普通',
    
    -- 学校规模
    `school_system` VARCHAR(10) COMMENT '学制: 6年/3年/9年/12年',
    `total_grades` INT COMMENT '总年级数',
    `total_classes` INT COMMENT '总班级数',
    `total_students` INT COMMENT '学生总数',
    `total_teachers` INT COMMENT '教师总数',
    
    -- 办学资质
    `established_year` INT COMMENT '建校年份',
    `approval_number` VARCHAR(100) COMMENT '办学批准文号',
    `school_area` DECIMAL(10,2) COMMENT '校园面积(平方米)',
    `building_area` DECIMAL(10,2) COMMENT '建筑面积(平方米)',
    
    -- 学校性质
    `school_nature` VARCHAR(20) COMMENT '学校性质: public-公办, private-民办, shared-共建',
    
    -- 联系方式
    `contact_person` VARCHAR(50) COMMENT '联系人',
    `contact_phone` VARCHAR(20) COMMENT '联系电话',
    `contact_address` VARCHAR(200) COMMENT '详细地址',
    
    -- 管辖关系
    `education_bureau_id` VARCHAR(32) COMMENT '所属教育局ID',
    `jurisdiction_type` VARCHAR(20) COMMENT '管辖类型: direct-直属, affiliated-附属',
    
    -- 状态
    `status` VARCHAR(10) DEFAULT 'active' COMMENT '状态: active-正常, suspended-停办, merged-合并',
    
    -- 审计字段
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    
    UNIQUE KEY `uk_org_id` (`organization_id`),
    UNIQUE KEY `uk_school_code` (`school_code`),
    INDEX `idx_education_bureau` (`education_bureau_id`),
    INDEX `idx_school_type` (`school_type`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学校扩展信息表';


-- 2. 班级表
CREATE TABLE IF NOT EXISTS `school_class` (
    `id` VARCHAR(32) PRIMARY KEY COMMENT '主键ID',
    `school_id` VARCHAR(32) NOT NULL COMMENT '所属学校ID',
    
    -- 班级基本信息
    `class_name` VARCHAR(50) NOT NULL COMMENT '班级名称(如: 初一(1)班)',
    `class_code` VARCHAR(50) COMMENT '班级编号',
    `class_number` INT NOT NULL COMMENT '班号(如: 1班=1, 2班=2)',
    
    -- 年级信息
    `grade` INT NOT NULL COMMENT '年级(1-12)',
    `grade_name` VARCHAR(20) NOT NULL COMMENT '年级名称(如: 初一, 高二)',
    
    -- 班级属性
    `class_type` VARCHAR(20) COMMENT '班级类型: ordinary-普通班, key-重点班, art-美术班, music-音乐班, sports-体育班, international-国际班, science-理科班, liberal-文科班',
    
    -- 班级规模
    `student_limit` INT DEFAULT 50 COMMENT '最大学生数',
    `current_students` INT DEFAULT 0 COMMENT '当前学生数',
    
    -- 班主任
    `head_teacher_id` VARCHAR(32) COMMENT '班主任ID(关联employee表)',
    `head_teacher_name` VARCHAR(50) COMMENT '班主任姓名',
    
    -- 教室信息
    `building` VARCHAR(50) COMMENT '教学楼',
    `floor` INT COMMENT '楼层',
    `room_number` VARCHAR(20) COMMENT '教室编号',
    
    -- 学年信息
    `academic_year` VARCHAR(10) NOT NULL COMMENT '学年(如: 2024-2025)',
    `semester` INT DEFAULT 1 COMMENT '学期(1-2)',
    
    -- 状态
    `status` VARCHAR(10) DEFAULT 'active' COMMENT '状态: active-在读, graduated-已毕业, cancelled-已取消',
    `graduate_year` INT COMMENT '毕业年份(毕业班使用)',
    
    -- 审计字段
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    
    INDEX `idx_school_id` (`school_id`),
    INDEX `idx_grade` (`grade`),
    INDEX `idx_academic_year` (`academic_year`),
    INDEX `idx_head_teacher` (`head_teacher_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';


-- 3. 学生表
CREATE TABLE IF NOT EXISTS `student` (
    `id` VARCHAR(32) PRIMARY KEY COMMENT '主键ID',
    `employee_id` VARCHAR(32) COMMENT '关联职员ID(可选)',
    
    -- 学籍基本信息
    `student_no` VARCHAR(50) NOT NULL COMMENT '学籍号',
    `student_name` VARCHAR(50) NOT NULL COMMENT '学生姓名',
    
    -- 基本信息
    `gender` VARCHAR(10) COMMENT '性别: male/female',
    `birth_date` DATE COMMENT '出生日期',
    `id_card` VARCHAR(18) COMMENT '身份证号',
    `nationality` VARCHAR(20) DEFAULT '中国' COMMENT '国籍/地区',
    `ethnicity` VARCHAR(20) COMMENT '民族',
    
    -- 入学信息
    `enrollment_year` INT NOT NULL COMMENT '入学年份',
    `enrollment_type` VARCHAR(20) COMMENT '入学方式: exam-考试入学, transfer-转入, adjustment-调整',
    `previous_school` VARCHAR(100) COMMENT '原学校(转学生)',
    
    -- 学业信息
    `education_level` VARCHAR(20) COMMENT '教育类别: regular-普通教育, vocational-职业教育',
    `study_type` VARCHAR(20) COMMENT '学习方式: full_time-全日制, part_time-非全日制, correspondence-函授',
    
    -- 当前就读信息
    `school_id` VARCHAR(32) COMMENT '就读学校ID',
    `class_id` VARCHAR(32) COMMENT '所属班级ID',
    `grade` INT COMMENT '当前年级',
    
    -- 状态
    `student_status` VARCHAR(20) DEFAULT 'studying' COMMENT '学籍状态: studying-在读, suspended-休学, transferred-转出, dropped-辍学, graduated-毕业, expelled-开除',
    
    -- 联系方式
    `phone` VARCHAR(20) COMMENT '学生电话',
    `email` VARCHAR(100) COMMENT '学生邮箱',
    `photo_url` VARCHAR(200) COMMENT '证件照URL',
    
    -- 审计字段
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    
    UNIQUE KEY `uk_student_no` (`student_no`),
    INDEX `idx_school_id` (`school_id`),
    INDEX `idx_class_id` (`class_id`),
    INDEX `idx_enrollment_year` (`enrollment_year`),
    INDEX `idx_student_status` (`student_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生表';


-- 4. 监护人表
CREATE TABLE IF NOT EXISTS `guardian` (
    `id` VARCHAR(32) PRIMARY KEY COMMENT '主键ID',
    `student_id` VARCHAR(32) NOT NULL COMMENT '学生ID',
    
    -- 监护人基本信息
    `guardian_name` VARCHAR(50) NOT NULL COMMENT '监护人姓名',
    `relation` VARCHAR(20) NOT NULL COMMENT '与学生关系: father-父亲, mother-母亲, grandfather-祖父, grandmother-祖母, maternal_grandfather-外祖父, maternal_grandmother-外祖母, other-其他',
    `id_card` VARCHAR(18) COMMENT '身份证号',
    
    -- 联系方式
    `phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
    `alternate_phone` VARCHAR(20) COMMENT '备用电话',
    `email` VARCHAR(100) COMMENT '电子邮箱',
    
    -- 工作信息
    `employer` VARCHAR(100) COMMENT '工作单位',
    `occupation` VARCHAR(50) COMMENT '职业',
    
    -- 紧急联系
    `is_emergency_contact` TINYINT DEFAULT 0 COMMENT '是否紧急联系人: 0-否, 1-是',
    `priority` INT DEFAULT 1 COMMENT '联系优先级(1最高)',
    
    -- 地址信息
    `address` VARCHAR(200) COMMENT '居住地址',
    
    -- 状态
    `status` VARCHAR(10) DEFAULT 'active' COMMENT '状态: active-有效, inactive-无效',
    
    -- 审计字段
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    
    INDEX `idx_student_id` (`student_id`),
    INDEX `idx_phone` (`phone`),
    INDEX `idx_is_emergency` (`is_emergency_contact`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='监护人表';


-- =============================================
-- 初始化数据
-- =============================================

-- 插入学校类型字典数据
INSERT INTO `sys_dict` (`code`, `name`, `type`, `value`, `sort`, `status`) VALUES
('school_type', '学校类型', 'school', 'primary', '小学', 1, '1'),
('school_type', '学校类型', 'school', 'junior_middle', '初中', 2, '1'),
('school_type', '学校类型', 'school', 'senior_middle', '高中', 3, '1'),
('school_type', '学校类型', 'school', 'vocational', '职高', 4, '1'),
('school_type', '学校类型', 'school', 'nine_year', '九年一贯制', 5, '1'),
('school_type', '学校类型', 'school', 'twelve_year', '十二年一贯制', 6, '1'),
('school_type', '学校类型', 'school', 'complete', '完全中学', 7, '1'),
('school_level', '学校评级', 'school', 'provincial_key', '省级示范', 1, '1'),
('school_level', '学校评级', 'school', 'city_key', '市级示范', 2, '1'),
('school_level', '学校评级', 'school', 'county_key', '县级示范', 3, '1'),
('school_level', '学校评级', 'school', 'ordinary', '普通', 4, '1'),
('school_nature', '学校性质', 'school', 'public', '公办', 1, '1'),
('school_nature', '学校性质', 'school', 'private', '民办', 2, '1'),
('school_nature', '学校性质', 'school', 'shared', '共建', 3, '1'),
('class_type', '班级类型', 'school', 'ordinary', '普通班', 1, '1'),
('class_type', '班级类型', 'school', 'key', '重点班', 2, '1'),
('class_type', '班级类型', 'school', 'art', '美术班', 3, '1'),
('class_type', '班级类型', 'school', 'music', '音乐班', 4, '1'),
('class_type', '班级类型', 'school', 'sports', '体育班', 5, '1'),
('class_type', '班级类型', 'school', 'international', '国际班', 6, '1'),
('student_status', '学籍状态', 'school', 'studying', '在读', 1, '1'),
('student_status', '学籍状态', 'school', 'suspended', '休学', 2, '1'),
('student_status', '学籍状态', 'school', 'transferred', '转出', 3, '1'),
('student_status', '学籍状态', 'school', 'dropped', '辍学', 4, '1'),
('student_status', '学籍状态', 'school', 'graduated', '毕业', 5, '1'),
('student_status', '学籍状态', 'school', 'expelled', '开除', 6, '1'),
('guardian_relation', '监护人关系', 'school', 'father', '父亲', 1, '1'),
('guardian_relation', '监护人关系', 'school', 'mother', '母亲', 2, '1'),
('guardian_relation', '监护人关系', 'school', 'grandfather', '祖父', 3, '1'),
('guardian_relation', '监护人关系', 'school', 'grandmother', '祖母', 4, '1'),
('guardian_relation', '监护人关系', 'school', 'maternal_grandfather', '外祖父', 5, '1'),
('guardian_relation', '监护人关系', 'school', 'maternal_grandmother', '外祖母', 6, '1'),
('guardian_relation', '监护人关系', 'school', 'other', '其他', 7, '1');
