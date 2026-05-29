package com.lvfat.dto;

import lombok.Data;

import java.util.List;

/**
 * 智能组卷请求DTO
 */
@Data
public class AutoGeneratePaperDTO {
    
    /**
     * 试卷标题
     */
    private String title;
    
    /**
     * 试卷说明
     */
    private String description;
    
    /**
     * 目标总分
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
     * 题型配置列表
     */
    private List<QuestionTypeConfig> questionTypes;
    
    /**
     * 难度配置
     */
    private DifficultyConfig difficulty;
    
    /**
     * 分类配置
     */
    private List<CategoryConfig> categories;
    
    /**
     * 题型配置
     */
    @Data
    public static class QuestionTypeConfig {
        /**
         * 题型: single_choice, multiple_choice, judge, essay
         */
        private String type;
        
        /**
         * 题目数量
         */
        private Integer count;
        
        /**
         * 每题分值
         */
        private Integer score;
    }
    
    /**
     * 难度配置
     */
    @Data
    public static class DifficultyConfig {
        private Integer easy;
        private Integer medium;
        private Integer hard;
    }
    
    /**
     * 分类配置
     */
    @Data
    public static class CategoryConfig {
        private String categoryId;
        private Integer weight;
    }
}
