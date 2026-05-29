package com.lvfat.dto;

import lombok.Data;

/**
 * 考试提交请求DTO
 */
@Data
public class ExamSubmitDTO {
    
    /**
     * 考试记录ID
     */
    private String examRecordId;
    
    /**
     * 答题记录列表
     */
    private String answers;
}
