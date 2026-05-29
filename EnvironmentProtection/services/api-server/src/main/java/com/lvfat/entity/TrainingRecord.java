package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 培训记录实体
 */
@Data
@TableName("training_record")
public class TrainingRecord {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    private String trainingId;
    
    private String userId;
    
    /**
     * 状态: not_started, in_progress, completed
     */
    private String status;
    
    private Integer score;
    
    private Integer duration;
    
    private LocalDateTime startTime;
    
    private LocalDateTime completedTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
