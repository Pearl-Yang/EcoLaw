package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 案例实体
 */
@Data
@TableName("case_info")
public class CaseInfo {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    private String title;
    
    /**
     * 类型: civil, criminal, administrative
     */
    private String type;
    
    private String description;
    
    private String result;
    
    private String lawFirmId;
    
    private String lawyerId;
    
    private String lawyerName;
    
    private String coverUrl;
    
    private Integer viewCount;
    
    /**
     * 状态: 0-待审核, 1-已发布, 2-已下架
     */
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
