package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI对话消息实体
 */
@Data
@TableName("ai_message")
public class AIMessage {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 会话ID
     */
    private String conversationId;

    /**
     * 角色: user-用户, assistant-助手, system-系统
     */
    private String role;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 使用的模型
     */
    private String model;

    /**
     * 消耗的Token数
     */
    private Integer tokens;

    /**
     * 响应延迟(毫秒)
     */
    private Integer latencyMs;

    /**
     * 引用的法规(JSON数组)
     */
    private String lawReferences;

    /**
     * 用户反馈: like-满意, dislike-不满意
     */
    private String feedback;

    /**
     * 反馈内容
     */
    private String feedbackContent;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
