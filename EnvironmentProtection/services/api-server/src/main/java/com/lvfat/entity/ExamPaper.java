package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 试卷实体
 */
@Data
@TableName("exam_paper")
public class ExamPaper {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 试卷标题
     */
    private String title;
    
    /**
     * 试卷说明
     */
    private String description;
    
    /**
     * 总分
     */
    private Integer totalScore;
    
    /**
     * 及格分数
     */
    private Integer passScore;
    
    /**
     * 时限(分钟)
     */
    private Integer timeLimit;
    
    /**
     * 题目数量
     */
    private Integer questionCount;
    
    /**
     * 创建人ID
     */
    private String creatorId;
    
    /**
     * 组卷方式: manual-手动组卷, auto-智能组卷, template-模板组卷
     */
    private String sourceType;
    
    /**
     * 组卷配置(JSON格式)
     */
    private String config;
    
    /**
     * 状态: draft-草稿, published-已发布, archived-已归档
     */
    private String status;
    
    /**
     * 发布时间
     */
    private LocalDateTime publishTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
