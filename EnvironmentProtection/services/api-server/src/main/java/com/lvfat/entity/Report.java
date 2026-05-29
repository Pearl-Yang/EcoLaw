package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 举报实体
 */
@Data
@TableName("report")
public class Report {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    private String title;
    
    private String content;
    
    /**
     * 类型: illegal_production, illegal_sale, illegal_use, environmental_pollution, other
     */
    private String type;
    
    private String location;
    
    private Double longitude;
    
    private Double latitude;
    
    private String images;
    
    /**
     * 状态: pending, processing, resolved, rejected
     */
    private String status;
    
    private String reporterId;
    
    private String reporterName;
    
    private String reporterPhone;
    
    private String handlerId;
    
    private String handlerName;
    
    private String handleResult;
    
    private LocalDateTime handleTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
