package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 法规实体
 */
@Data
@TableName("law")
public class Law {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    private String title;
    
    /**
     * 层级: national, provincial, city
     */
    private String level;
    
    private String category;
    
    private String content;
    
    private String fileUrl;
    
    private String publishDate;
    
    private String effectiveDate;
    
    private String status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}