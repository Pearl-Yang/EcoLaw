package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 题目分类实体
 */
@Data
@TableName("exam_category")
public class ExamCategory {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    private String name;
    
    private String parentId;
    
    private Integer sort;
    
    /**
     * 状态: 1-正常, 0-禁用
     */
    private String status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
