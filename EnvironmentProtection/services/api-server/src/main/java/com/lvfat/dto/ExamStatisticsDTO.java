package com.lvfat.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 考试统计DTO
 */
@Data
public class ExamStatisticsDTO {
    
    /**
     * 试卷ID
     */
    private String paperId;
    
    /**
     * 试卷标题
     */
    private String paperTitle;
    
    /**
     * 下发人数
     */
    private Integer totalCount;
    
    /**
     * 已完成人数
     */
    private Integer completedCount;
    
    /**
     * 完成率
     */
    private BigDecimal completionRate;
    
    /**
     * 平均分
     */
    private BigDecimal avgScore;
    
    /**
     * 最高分
     */
    private Integer maxScore;
    
    /**
     * 最低分
     */
    private Integer minScore;
    
    /**
     * 及格人数
     */
    private Integer passCount;
    
    /**
     * 及格率
     */
    private BigDecimal passRate;
    
    /**
     * 平均正确率
     */
    private BigDecimal avgCorrectRate;
}
