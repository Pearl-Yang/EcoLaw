package com.lvfat.dto;

import lombok.Data;

/**
 * 题目DTO
 */
@Data
public class QuestionDTO {
    
    private String id;
    
    /**
     * 题型
     */
    private String type;
    
    /**
     * 难度
     */
    private String difficulty;
    
    /**
     * 题目内容
     */
    private String content;
    
    /**
     * 选项
     */
    private String options;
    
    /**
     * 题目分值
     */
    private Integer score;
    
    /**
     * 排序
     */
    private Integer sortOrder;
    
    /**
     * 是否显示答案
     */
    private Boolean showAnswer;
    
    /**
     * 正确答案
     */
    private String answer;
    
    /**
     * 答案解析
     */
    private String analysis;
}
