package com.lvfat.dto;

import lombok.Data;

import java.util.List;

/**
 * 试卷详情响应DTO
 */
@Data
public class ExamPaperDetailDTO {
    
    private String id;
    
    private String title;
    
    private String description;
    
    private Integer totalScore;
    
    private Integer passScore;
    
    private Integer timeLimit;
    
    private Integer questionCount;
    
    private String sourceType;
    
    private String status;
    
    /**
     * 题目列表
     */
    private List<QuestionDTO> questions;
}
