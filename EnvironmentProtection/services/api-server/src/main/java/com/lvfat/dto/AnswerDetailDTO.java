package com.lvfat.dto;

import lombok.Data;

/**
 * 答题详情DTO
 */
@Data
public class AnswerDetailDTO {
    
    private String questionId;
    
    /**
     * 题型
     */
    private String questionType;
    
    /**
     * 题目内容
     */
    private String content;
    
    /**
     * 选项
     */
    private String options;
    
    /**
     * 用户答案
     */
    private String userAnswer;
    
    /**
     * 正确答案
     */
    private String correctAnswer;
    
    /**
     * 是否正确
     */
    private Integer isCorrect;
    
    /**
     * 得分
     */
    private Integer score;
    
    /**
     * 题目分值
     */
    private Integer questionScore;
    
    /**
     * 答案解析
     */
    private String analysis;
    
    /**
     * 排序
     */
    private Integer sortOrder;
    
    /**
     * 答题用时
     */
    private Integer answerTime;
}
