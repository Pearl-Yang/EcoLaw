package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI对话会话实体
 */
@Data
@TableName("ai_conversation")
public class AIConversation {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 会话类型: law-法律问答, material-素材生成, case-案例分析, exam-组卷
     */
    private String sessionType;

    /**
     * 会话标题(首条消息摘要)
     */
    private String title;

    /**
     * 上下文摘要
     */
    private String contextSummary;

    /**
     * 消息数量
     */
    private Integer messageCount;

    /**
     * 最后消息时间
     */
    private LocalDateTime lastMessageTime;

    /**
     * 状态: active-进行中, archived-已归档
     */
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public Integer getDeleted() {
        return deleted;
    }
}
