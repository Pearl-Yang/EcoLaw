package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务下发记录实体
 */
@Data
@TableName("task_dispatch")
public class TaskDispatch {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 任务ID
     */
    private String taskId;
    
    /**
     * 上级下发记录ID
     */
    private String parentDispatchId;
    
    /**
     * 下发方组织ID
     */
    private String sourceOrganizationId;
    
    /**
     * 接收方组织ID
     */
    private String targetOrganizationId;
    
    /**
     * 接收方用户ID(个人下发时使用)
     */
    private String targetUserId;
    
    /**
     * 接收方职员ID(职员下发时使用)
     */
    private String targetEmployeeId;
    
    /**
     * 下发层级(从1开始)
     */
    private Integer dispatchLevel;
    
    /**
     * 状态: pending-待接收, accepted-已接收, rejected-已拒绝, 
     *       completed-已完成, delegated-已转发, overdue-已逾期
     */
    private String status;
    
    /**
     * 目标数量(需完成任务数)
     */
    private Integer targetCount;
    
    /**
     * 已完成数量
     */
    private Integer completedCount;
    
    /**
     * 完成进度百分比
     */
    private Integer progress;
    
    /**
     * 截止时间
     */
    private LocalDateTime deadline;
    
    /**
     * 备注说明
     */
    private String notes;
    
    /**
     * 拒绝原因
     */
    private String rejectReason;
    
    /**
     * 下发时间
     */
    private LocalDateTime dispatchTime;
    
    /**
     * 接收时间
     */
    private LocalDateTime acceptTime;
    
    /**
     * 完成时间
     */
    private LocalDateTime completeTime;
    
    @TableField(exist = false)
    private Task task;
    
    @TableField(exist = false)
    private Organization sourceOrganization;
    
    @TableField(exist = false)
    private Organization targetOrganization;
    
    @TableField(exist = false)
    private User targetUser;
    
    @TableField(exist = false)
    private Employee targetEmployee;
    
    @TableField(exist = false)
    private TaskDispatch parentDispatch;
    
    @TableField(exist = false)
    private List<TaskDispatch> children;
    
    @TableField(exist = false)
    private String taskTitle;
    
    @TableField(exist = false)
    private String sourceOrgName;
    
    @TableField(exist = false)
    private String targetOrgName;
    
    @TableField(exist = false)
    private String targetEmployeeName;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
