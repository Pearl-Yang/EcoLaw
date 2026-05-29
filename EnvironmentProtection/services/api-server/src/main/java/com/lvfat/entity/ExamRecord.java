package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 考试记录实体
 */
@Data
@TableName("exam_record")
public class ExamRecord {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 试卷ID
     */
    private String paperId;
    
    /**
     * 试卷标题(冗余)
     */
    private String paperTitle;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 用户姓名(冗余)
     */
    private String userName;
    
    /**
     * 所属组织ID
     */
    private String organizationId;
    
    /**
     * 组织名称(冗余)
     */
    private String organizationName;
    
    /**
     * 状态: not_started-未开始, in_progress-进行中, submitted-已提交, graded-已评分
     */
    private String status;
    
    /**
     * 得分
     */
    private Integer score;
    
    /**
     * 是否及格: 0-不及格, 1-及格
     */
    private Integer isPassed;
    
    /**
     * 正确题数
     */
    private Integer correctCount;
    
    /**
     * 总题数
     */
    private Integer totalCount;
    
    /**
     * 正确率
     */
    private BigDecimal correctRate;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 提交时间
     */
    private LocalDateTime submitTime;
    
    /**
     * 用时(秒)
     */
    private Integer duration;
    
    /**
     * IP地址
     */
    private String ipAddress;
    
    /**
     * 设备信息
     */
    private String deviceInfo;
    
    /**
     * 下发人ID
     */
    private String creatorId;
    
    /**
     * 关联任务ID
     */
    private String taskId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
