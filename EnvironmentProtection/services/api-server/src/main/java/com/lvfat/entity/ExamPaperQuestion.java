package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 试卷题目关联实体
 */
@Data
@TableName("exam_paper_question")
public class ExamPaperQuestion {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 试卷ID
     */
    private String paperId;
    
    /**
     * 题目ID
     */
    private String questionId;
    
    /**
     * 排序顺序
     */
    private Integer sortOrder;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
