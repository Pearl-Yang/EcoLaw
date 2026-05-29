package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 监护人实体
 */
@Data
@TableName("guardian")
public class Guardian {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 学生ID
     */
    private String studentId;
    
    // ==================== 监护人基本信息 ====================
    /**
     * 监护人姓名
     */
    private String guardianName;
    
    /**
     * 与学生关系: 
     * father-父亲, mother-母亲, grandfather-祖父, grandmother-祖母, 
     * maternal_grandfather-外祖父, maternal_grandmother-外祖母, other-其他
     */
    private String relation;
    
    /**
     * 身份证号
     */
    private String idCard;
    
    // ==================== 联系方式 ====================
    /**
     * 联系电话
     */
    private String phone;
    
    /**
     * 备用电话
     */
    private String alternatePhone;
    
    /**
     * 电子邮箱
     */
    private String email;
    
    // ==================== 工作信息 ====================
    /**
     * 工作单位
     */
    private String employer;
    
    /**
     * 职业
     */
    private String occupation;
    
    // ==================== 紧急联系 ====================
    /**
     * 是否紧急联系人: 0-否, 1-是
     */
    private Integer isEmergencyContact;
    
    /**
     * 联系优先级(1最高)
     */
    private Integer priority;
    
    // ==================== 地址信息 ====================
    private String address;
    
    // ==================== 状态 ====================
    /**
     * 状态: active-有效, inactive-无效
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
    private Student student;
    
    @TableField(exist = false)
    private String relationName;
    
    @TableField(exist = false)
    private String statusName;
    
    // ==================== 关系类型常量 ====================
    public static final String RELATION_FATHER = "father";
    public static final String RELATION_MOTHER = "mother";
    public static final String RELATION_GRANDFATHER = "grandfather";
    public static final String RELATION_GRANDMOTHER = "grandmother";
    public static final String RELATION_MATERNAL_GRANDFATHER = "maternal_grandfather";
    public static final String RELATION_MATERNAL_GRANDMOTHER = "maternal_grandmother";
    public static final String RELATION_OTHER = "other";
    
    // ==================== 状态常量 ====================
    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_INACTIVE = "inactive";
}
