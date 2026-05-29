package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 普法任务实体
 */
@Data
@TableName("task")
public class Task {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    private String title;
    
    private String content;
    
    /**
     * 类型: government, enterprise, law_firm
     */
    private String type;
    
    /**
     * 状态: pending, in_progress, completed, overdue, rejected
     */
    private String status;
    
    private String creatorId;
    
    private String organizationId;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private Integer targetCount;
    
    private Integer completedCount;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}