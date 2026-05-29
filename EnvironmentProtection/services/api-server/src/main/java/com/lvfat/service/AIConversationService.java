package com.lvfat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lvfat.entity.AIConversation;

import java.util.List;

/**
 * AI对话会话服务接口
 */
public interface AIConversationService extends IService<AIConversation> {

    /**
     * 获取用户对话列表
     */
    List<AIConversation> listByUser(String userId, String sessionType, Integer page, Integer pageSize);

    /**
     * 获取对话详情（含消息）
     */
    AIConversation getConversationDetail(String conversationId);

    /**
     * 创建新对话
     */
    AIConversation createConversation(String userId, String sessionType, String firstMessage);

    /**
     * 更新对话摘要
     */
    void updateConversationSummary(String conversationId, String title, String summary);

    /**
     * 删除对话
     */
    void deleteConversation(String conversationId);

    /**
     * 归档对话
     */
    void archiveConversation(String conversationId);
}
