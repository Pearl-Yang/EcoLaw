package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 学生实体
 */
@Data
@TableName("student")
public class Student {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 关联职员ID(可选，复用现有employee表的用户信息)
     */
    private String employeeId;
    
    // ==================== 学籍基本信息 ====================
    /**
     * 学籍号
     */
    private String studentNo;
    
    /**
     * 学生姓名
     */
    private String studentName;
    
    // ==================== 基本信息 ====================
    /**
     * 性别: male/female
     */
    private String gender;
    
    /**
     * 出生日期
     */
    private LocalDate birthDate;
    
    /**
     * 身份证号
     */
    private String idCard;
    
    /**
     * 国籍/地区
     */
    private String nationality;
    
    /**
     * 民族
     */
    private String ethnicity;
    
    // ==================== 入学信息 ====================
    /**
     * 入学年份
     */
    private Integer enrollmentYear;
    
    /**
     * 入学方式: exam-考试入学, transfer-转入, adjustment-调整
     */
    private String enrollmentType;
    
    /**
     * 原学校(转学生)
     */
    private String previousSchool;
    
    // ==================== 学业信息 ====================
    /**
     * 教育类别: regular-普通教育, vocational-职业教育
     */
    private String educationLevel;
    
    /**
     * 学习方式: full_time-全日制, part_time-非全日制, correspondence-函授
     */
    private String studyType;
    
    // ==================== 当前就读信息 ====================
    /**
     * 就读学校ID
     */
    private String schoolId;
    
    /**
     * 所属班级ID
     */
    private String classId;
    
    /**
     * 当前年级
     */
    private Integer grade;
    
    // ==================== 状态 ====================
    /**
     * 学籍状态: 
     * studying-在读, suspended-休学, transferred-转出, 
     * dropped-辍学, graduated-毕业, expelled-开除
     */
    private String studentStatus;
    
    // ==================== 联系方式 ====================
    private String phone;
    private String email;
    
    /**
     * 证件照URL
     */
    private String photoUrl;
    
    // ==================== 审计字段 ====================
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
    
    // ==================== 关联对象(非数据库字段) ====================
    @TableField(exist = false)
    private School school;
    
    @TableField(exist = false)
    private SchoolClass schoolClass;
    
    @TableField(exist = false)
    private Employee employee;
    
    @TableField(exist = false)
    private List<Guardian> guardians;
    
    @TableField(exist = false)
    private String schoolName;
    
    @TableField(exist = false)
    private String className;
    
    @TableField(exist = false)
    private String studentStatusName;
    
    @TableField(exist = false)
    private String genderName;
    
    // ==================== 性别常量 ====================
    public static final String GENDER_MALE = "male";
    public static final String GENDER_FEMALE = "female";
    
    // ==================== 入学方式常量 ====================
    public static final String ENROLLMENT_EXAM = "exam";
    public static final String ENROLLMENT_TRANSFER = "transfer";
    public static final String ENROLLMENT_ADJUSTMENT = "adjustment";
    
    // ==================== 教育类别常量 ====================
    public static final String EDUCATION_REGULAR = "regular";
    public static final String EDUCATION_VOCATIONAL = "vocational";
    
    // ==================== 学习方式常量 ====================
    public static final String STUDY_FULL_TIME = "full_time";
    public static final String STUDY_PART_TIME = "part_time";
    public static final String STUDY_CORRESPONDENCE = "correspondence";
    
    // ==================== 学籍状态常量 ====================
    public static final String STATUS_STUDYING = "studying";
    public static final String STATUS_SUSPENDED = "suspended";
    public static final String STATUS_TRANSFERRED = "transferred";
    public static final String STATUS_DROPPED = "dropped";
    public static final String STATUS_GRADUATED = "graduated";
    public static final String STATUS_EXPELLED = "expelled";
}
