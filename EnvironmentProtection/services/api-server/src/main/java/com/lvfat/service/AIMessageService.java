package com.lvfat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lvfat.entity.AIMessage;

import java.util.List;
import java.util.Map;

/**
 * AI对话消息服务接口
 */
public interface AIMessageService extends IService<AIMessage> {

    /**
     * 获取会话消息列表
     */
    List<AIMessage> listByConversation(String conversationId);

    /**
     * 保存用户消息
     */
    AIMessage saveUserMessage(String conversationId, String content);

    /**
     * 保存AI回复
     */
    AIMessage saveAssistantMessage(String conversationId, String content, String model,
                                   Integer tokens, Integer latencyMs, String lawReferences);

    /**
     * 消息反馈
     */
    void feedback(String messageId, String feedback, String feedbackContent);

    /**
     * 获取统计信息
     */
    Map<String, Object> getStatistics(String userId, String startDate, String endDate);
}
