package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 职员实体
 */
@Data
@TableName("employee")
public class Employee {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 关联用户ID
     */
    private String userId;
    
    /**
     * 所属组织ID
     */
    private String organizationId;
    
    /**
     * 上级职员ID(用于汇报关系)
     */
    private String parentEmployeeId;
    
    /**
     * 职员编号
     */
    private String employeeNo;
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 性别: male男, female女
     */
    private String gender;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 身份证号
     */
    private String idCard;
    
    /**
     * 职位/职务
     */
    private String position;
    
    /**
     * 职级
     */
    private String positionLevel;
    
    /**
     * 职称
     */
    private String title;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 状态: active-在职, leave-离职, transfer-调离
     */
    private String status;
    
    /**
     * 入职日期
     */
    private LocalDate hireDate;
    
    /**
     * 是否负责人: 0-否, 1-是
     */
    private Integer isLeader;
    
    /**
     * 学号(学生专用)
     */
    private String studentNo;
    
    /**
     * 年级(学生专用)
     */
    private String grade;
    
    /**
     * 班级(学生专用)
     */
    private String className;
    
    /**
     * 监护人姓名(学生专用)
     */
    private String guardianName;
    
    /**
     * 监护人电话(学生专用)
     */
    private String guardianPhone;
    
    @TableField(exist = false)
    private Organization organization;
    
    @TableField(exist = false)
    private User user;
    
    @TableField(exist = false)
    private Employee parentEmployee;
    
    @TableField(exist = false)
    private List<Employee> subordinates;
    
    @TableField(exist = false)
    private String organizationName;
    
    @TableField(exist = false)
    private String organizationType;
    
    @TableField(exist = false)
    private String parentEmployeeName;
    
    @TableField(exist = false)
    private String parentPosition;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
