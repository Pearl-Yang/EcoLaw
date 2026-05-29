package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 题目实体
 */
@Data
@TableName("exam_question")
public class ExamQuestion {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 题型: single_choice-单选题, multiple_choice-多选题, judge-判断题, essay-简答题
     */
    private String type;
    
    /**
     * 难度: easy-简单, medium-中等, hard-困难
     */
    private String difficulty;
    
    /**
     * 题目内容
     */
    private String content;
    
    /**
     * 选项(JSON格式)
     */
    private String options;
    
    /**
     * 正确答案(JSON格式)
     */
    private String answer;
    
    /**
     * 答案解析
     */
    private String analysis;
    
    /**
     * 分值
     */
    private Integer score;
    
    /**
     * 所属分类ID
     */
    private String categoryId;
    
    /**
     * 关联法规ID
     */
    private String lawId;
    
    /**
     * 标签(JSON数组)
     */
    private String tag;
    
    /**
     * 使用次数
     */
    private Integer usageCount;
    
    /**
     * 正确次数
     */
    private Integer correctCount;
    
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
