package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务进度上报记录实体
 */
@Data
@TableName("task_progress_report")
public class TaskProgressReport {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 下发记录ID
     */
    private String dispatchId;
    
    /**
     * 上报人ID
     */
    private String reporterId;
    
    /**
     * 上报人姓名
     */
    private String reporterName;
    
    /**
     * 上报类型: progress-进度, completion-完成
     */
    private String reportType;
    
    /**
     * 本次上报完成数量
     */
    private Integer completedCount;
    
    /**
     * 累计完成数量
     */
    private Integer totalCompleted;
    
    /**
     * 进度百分比
     */
    private Integer progress;
    
    /**
     * 上报内容/说明
     */
    private String content;
    
    /**
     * 附件(JSON数组)
     */
    private String attachments;
    
    @TableField(exist = false)
    private TaskDispatch dispatch;
    
    @TableField(exist = false)
    private User reporter;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
