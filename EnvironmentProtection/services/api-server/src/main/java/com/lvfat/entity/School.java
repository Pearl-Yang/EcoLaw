package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学校扩展信息实体
 */
@Data
@TableName("school")
public class School {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 关联组织ID
     */
    private String organizationId;
    
    // ==================== 学校基本信息 ====================
    private String schoolName;
    
    /**
     * 学校代码(教育局统一编号)
     */
    private String schoolCode;
    
    /**
     * 学校类型: 
     * primary-小学, junior_middle-初中, senior_middle-高中, 
     * vocational-职高, nine_year-九年一贯制, twelve_year-十二年一贯制, complete-完全中学
     */
    private String schoolType;
    
    /**
     * 学校评级: 
     * provincial_key-省级示范, city_key-市级示范, county_key-县级示范, ordinary-普通
     */
    private String schoolLevel;
    
    // ==================== 学校规模 ====================
    /**
     * 学制: 6年/3年/9年/12年
     */
    private String schoolSystem;
    
    private Integer totalGrades;
    private Integer totalClasses;
    private Integer totalStudents;
    private Integer totalTeachers;
    
    // ==================== 办学资质 ====================
    /**
     * 建校年份
     */
    private Integer establishedYear;
    
    /**
     * 办学批准文号
     */
    private String approvalNumber;
    
    /**
     * 校园面积(平方米)
     */
    private Double schoolArea;
    
    /**
     * 建筑面积(平方米)
     */
    private Double buildingArea;
    
    /**
     * 学校性质: public-公办, private-民办, shared-共建
     */
    private String schoolNature;
    
    // ==================== 联系方式 ====================
    private String contactPerson;
    private String contactPhone;
    private String contactAddress;
    
    // ==================== 管辖关系 ====================
    /**
     * 所属教育局组织ID
     */
    private String educationBureauId;
    
    /**
     * 管辖类型: direct-直属, affiliated-附属
     */
    private String jurisdictionType;
    
    // ==================== 状态 ====================
    /**
     * 状态: active-正常, suspended-停办, merged-合并
     */
    private String status;
    
    // ==================== 审计字段 ====================
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
    
    // ==================== 关联对象(非数据库字段) ====================
    @TableField(exist = false)
    private Organization organization;
    
    @TableField(exist = false)
    private Organization educationBureau;
    
    @TableField(exist = false)
    private List<SchoolClass> classes;
    
    @TableField(exist = false)
    private String educationBureauName;
    
    @TableField(exist = false)
    private String schoolTypeName;
    
    @TableField(exist = false)
    private String schoolLevelName;
    
    @TableField(exist = false)
    private String schoolNatureName;
    
    // ==================== 学校类型常量 ====================
    public static final String SCHOOL_TYPE_PRIMARY = "primary";
    public static final String SCHOOL_TYPE_JUNIOR_MIDDLE = "junior_middle";
    public static final String SCHOOL_TYPE_SENIOR_MIDDLE = "senior_middle";
    public static final String SCHOOL_TYPE_VOCATIONAL = "vocational";
    public static final String SCHOOL_TYPE_NINE_YEAR = "nine_year";
    public static final String SCHOOL_TYPE_TWELVE_YEAR = "twelve_year";
    public static final String SCHOOL_TYPE_COMPLETE = "complete";
    
    // ==================== 学校评级常量 ====================
    public static final String LEVEL_PROVINCIAL_KEY = "provincial_key";
    public static final String LEVEL_CITY_KEY = "city_key";
    public static final String LEVEL_COUNTY_KEY = "county_key";
    public static final String LEVEL_ORDINARY = "ordinary";
    
    // ==================== 学校性质常量 ====================
    public static final String NATURE_PUBLIC = "public";
    public static final String NATURE_PRIVATE = "private";
    public static final String NATURE_SHARED = "shared";
    
    // ==================== 管辖类型常量 ====================
    public static final String JURISDICTION_DIRECT = "direct";
    public static final String JURISDICTION_AFFILIATED = "affiliated";
    
    // ==================== 状态常量 ====================
    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_SUSPENDED = "suspended";
    public static final String STATUS_MERGED = "merged";
}
