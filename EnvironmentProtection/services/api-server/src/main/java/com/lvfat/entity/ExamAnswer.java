package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 答题记录实体
 */
@Data
@TableName("exam_answer")
public class ExamAnswer {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 考试记录ID
     */
    private String examRecordId;
    
    /**
     * 题目ID
     */
    private String questionId;
    
    /**
     * 题型(冗余)
     */
    private String questionType;
    
    /**
     * 用户答案(JSON格式)
     */
    private String userAnswer;
    
    /**
     * 是否正确: 0-错误, 1-正确
     */
    private Integer isCorrect;
    
    /**
     * 得分
     */
    private Integer score;
    
    /**
     * 题目顺序
     */
    private Integer sortOrder;
    
    /**
     * 答题用时(秒)
     */
    private Integer answerTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
