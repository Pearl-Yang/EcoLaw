package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI素材实体
 */
@Data
@TableName("ai_material")
public class AIMaterial {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    private String title;
    
    /**
     * 类型: video, image, text, h5
     */
    private String type;
    
    private String content;
    
    private String coverUrl;
    
    private String sourceUrl;
    
    private String topic;
    
    private String targetAudience;
    
    private String lawId;
    
    private String creatorId;
    
    /**
     * 状态: generating, completed, failed
     */
    private String status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
