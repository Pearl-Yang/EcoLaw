package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 智能组卷模板实体
 */
@Data
@TableName("exam_template")
public class ExamTemplate {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 模板名称
     */
    private String name;
    
    /**
     * 模板描述
     */
    private String description;
    
    /**
     * 目标总分
     */
    private Integer totalScore;
    
    /**
     * 时限(分钟)
     */
    private Integer timeLimit;
    
    /**
     * 及格分数
     */
    private Integer passScore;
    
    /**
     * 组卷配置(JSON格式)
     */
    private String config;
    
    /**
     * 使用次数
     */
    private Integer usageCount;
    
    /**
     * 创建人ID
     */
    private String creatorId;
    
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
