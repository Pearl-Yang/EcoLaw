package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * AI使用统计实体
 */
@Data
@TableName("ai_usage_stats")
public class AIUsageStats {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 统计日期
     */
    private LocalDate statDate;

    /**
     * 会话类型
     */
    private String sessionType;

    /**
     * 请求次数
     */
    private Integer requestCount;

    /**
     * Token消耗总量
     */
    private Integer tokenUsage;

    /**
     * 平均响应延迟
     */
    private Integer avgLatencyMs;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
