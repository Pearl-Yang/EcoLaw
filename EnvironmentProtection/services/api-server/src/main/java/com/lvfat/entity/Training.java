package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 培训实体
 */
@Data
@TableName("training")
public class Training {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    private String title;
    
    private String content;
    
    /**
     * 类型: video, document, exam
     */
    private String type;
    
    private Integer duration;
    
    private String coverUrl;
    
    private String videoUrl;
    
    private String attachmentUrl;
    
    private Integer viewCount;
    
    private Integer completedCount;
    
    private String creatorId;
    
    /**
     * 状态: pending, published, archived
     */
    private String status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
