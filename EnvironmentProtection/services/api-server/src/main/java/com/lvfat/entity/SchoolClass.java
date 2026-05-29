package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 班级实体
 */
@Data
@TableName("school_class")
public class SchoolClass {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 所属学校ID
     */
    private String schoolId;
    
    // ==================== 班级基本信息 ====================
    /**
     * 班级名称(如: 初一(1)班)
     */
    private String className;
    
    /**
     * 班级编号
     */
    private String classCode;
    
    /**
     * 班号(如: 1班=1, 2班=2)
     */
    private Integer classNumber;
    
    // ==================== 年级信息 ====================
    /**
     * 年级(1-12)
     */
    private Integer grade;
    
    /**
     * 年级名称(如: 初一, 高二)
     */
    private String gradeName;
    
    // ==================== 班级属性 ====================
    /**
     * 班级类型: 
     * ordinary-普通班, key-重点班, art-美术班, music-音乐班, 
     * sports-体育班, international-国际班, science-理科班, liberal-文科班
     */
    private String classType;
    
    // ==================== 班级规模 ====================
    /**
     * 最大学生数
     */
    private Integer studentLimit;
    
    /**
     * 当前学生数
     */
    private Integer currentStudents;
    
    // ==================== 班主任 ====================
    /**
     * 班主任ID(关联employee表)
     */
    private String headTeacherId;
    
    /**
     * 班主任姓名
     */
    private String headTeacherName;
    
    // ==================== 教室信息 ====================
    private String building;
    private Integer floor;
    private String roomNumber;
    
    // ==================== 学年信息 ====================
    /**
     * 学年(如: 2024-2025)
     */
    private String academicYear;
    
    /**
     * 学期(1-2)
     */
    private Integer semester;
    
    // ==================== 状态 ====================
    /**
     * 状态: active-在读, graduated-已毕业, cancelled-已取消
     */
    private String status;
    
    /**
     * 毕业年份(毕业班使用)
     */
    private Integer graduateYear;
    
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
    private Employee headTeacher;
    
    @TableField(exist = false)
    private List<Student> students;
    
    @TableField(exist = false)
    private String schoolName;
    
    @TableField(exist = false)
    private String classTypeName;
    
    @TableField(exist = false)
    private String statusName;
    
    // ==================== 班级类型常量 ====================
    public static final String CLASS_TYPE_ORDINARY = "ordinary";
    public static final String CLASS_TYPE_KEY = "key";
    public static final String CLASS_TYPE_ART = "art";
    public static final String CLASS_TYPE_MUSIC = "music";
    public static final String CLASS_TYPE_SPORTS = "sports";
    public static final String CLASS_TYPE_INTERNATIONAL = "international";
    public static final String CLASS_TYPE_SCIENCE = "science";
    public static final String CLASS_TYPE_LIBERAL = "liberal";
    
    // ==================== 状态常量 ====================
    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_GRADUATED = "graduated";
    public static final String STATUS_CANCELLED = "cancelled";
}
