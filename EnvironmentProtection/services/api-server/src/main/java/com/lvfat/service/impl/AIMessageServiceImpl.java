package com.lvfat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lvfat.entity.AIConversation;
import com.lvfat.entity.AIMessage;
import com.lvfat.entity.AIUsageStats;
import com.lvfat.repository.AIConversationMapper;
import com.lvfat.repository.AIMessageMapper;
import com.lvfat.repository.AIUsageStatsMapper;
import com.lvfat.service.AIMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI对话消息服务实现
 */
@Service
@RequiredArgsConstructor
public class AIMessageServiceImpl extends ServiceImpl<AIMessageMapper, AIMessage>
        implements AIMessageService {

    private final AIConversationMapper aiConversationMapper;
    private final AIMessageMapper aiMessageMapper;
    private final AIUsageStatsMapper aiUsageStatsMapper;

    @Override
    public List<AIMessage> listByConversation(String conversationId) {
        LambdaQueryWrapper<AIMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AIMessage::getConversationId, conversationId)
                .orderByAsc(AIMessage::getCreateTime);
        return list(wrapper);
    }

    @Override
    @Transactional
    public AIMessage saveUserMessage(String conversationId, String content) {
        AIMessage message = new AIMessage();
        message.setConversationId(conversationId);
        message.setRole("user");
        message.setContent(content);
        message.setCreateTime(LocalDateTime.now());
        save(message);

        // 更新会话统计
        updateConversationStats(conversationId, "user", 0, 0);
        return message;
    }

    @Override
    @Transactional
    public AIMessage saveAssistantMessage(String conversationId, String content, String model,
                                          Integer tokens, Integer latencyMs, String lawReferences) {
        AIMessage message = new AIMessage();
        message.setConversationId(conversationId);
        message.setRole("assistant");
        message.setContent(content);
        message.setModel(model);
        message.setTokens(tokens);
        message.setLatencyMs(latencyMs);
        message.setLawReferences(lawReferences);
        message.setCreateTime(LocalDateTime.now());
        save(message);

        // 更新会话统计
        updateConversationStats(conversationId, "assistant", tokens != null ? tokens : 0, latencyMs != null ? latencyMs : 0);
        return message;
    }

    @Override
    @Transactional
    public void feedback(String messageId, String feedback, String feedbackContent) {
        AIMessage message = getById(messageId);
        if (message != null) {
            message.setFeedback(feedback);
            message.setFeedbackContent(feedbackContent);
            updateById(message);
        }
    }

    @Override
    public Map<String, Object> getStatistics(String userId, String startDate, String endDate) {
        Map<String, Object> stats = new HashMap<>();

        // 查询该用户的所有对话
        LambdaQueryWrapper<AIConversation> convWrapper = new LambdaQueryWrapper<>();
        convWrapper.eq(AIConversation::getUserId, userId);
        List<AIConversation> conversations = aiConversationMapper.selectList(convWrapper);
        stats.put("totalConversations", conversations.size());

        // 消息统计
        long totalMessages = 0;
        long totalTokens = 0;
        for (AIConversation conv : conversations) {
            LambdaQueryWrapper<AIMessage> msgWrapper = new LambdaQueryWrapper<>();
            msgWrapper.eq(AIMessage::getConversationId, conv.getId());
            totalMessages += aiMessageMapper.selectCount(msgWrapper);

            LambdaQueryWrapper<AIMessage> tokenWrapper = new LambdaQueryWrapper<>();
            tokenWrapper.eq(AIMessage::getConversationId, conv.getId())
                    .isNotNull(AIMessage::getTokens);
            List<AIMessage> messagesWithTokens = aiMessageMapper.selectList(tokenWrapper);
            for (AIMessage msg : messagesWithTokens) {
                totalTokens += msg.getTokens() != null ? msg.getTokens() : 0;
            }
        }
        stats.put("totalMessages", totalMessages);
        stats.put("totalTokens", totalTokens);

        // 按类型统计
        Map<String, Long> typeStats = new HashMap<>();
        for (AIConversation conv : conversations) {
            String type = conv.getSessionType();
            typeStats.put(type, typeStats.getOrDefault(type, 0L) + 1);
        }
        stats.put("conversationsByType", typeStats);

        return stats;
    }

    /**
     * 更新会话统计信息
     */
    private void updateConversationStats(String conversationId, String role, int tokens, int latencyMs) {
        AIConversation conversation = aiConversationMapper.selectById(conversationId);
        if (conversation != null) {
            conversation.setMessageCount(conversation.getMessageCount() + 1);
            conversation.setLastMessageTime(LocalDateTime.now());
            aiConversationMapper.updateById(conversation);

            // 更新使用统计
            if ("assistant".equals(role)) {
                updateUsageStats(conversation.getUserId(), conversation.getSessionType(), tokens, latencyMs);
            }
        }
    }

    /**
     * 更新使用统计
     */
    private void updateUsageStats(String userId, String sessionType, int tokens, int latencyMs) {
        LocalDate today = LocalDate.now();

        LambdaQueryWrapper<AIUsageStats> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AIUsageStats::getUserId, userId)
                .eq(AIUsageStats::getStatDate, today)
                .eq(AIUsageStats::getSessionType, sessionType);

        AIUsageStats stats = aiUsageStatsMapper.selectOne(wrapper);
        if (stats == null) {
            stats = new AIUsageStats();
            stats.setUserId(userId);
            stats.setStatDate(today);
            stats.setSessionType(sessionType);
            stats.setRequestCount(1);
            stats.setTokenUsage(tokens);
            stats.setAvgLatencyMs(latencyMs);
            aiUsageStatsMapper.insert(stats);
        } else {
            int totalRequests = stats.getRequestCount() + 1;
            int totalTokens = stats.getTokenUsage() + tokens;
            int newAvgLatency = (stats.getAvgLatencyMs() * stats.getRequestCount() + latencyMs) / totalRequests;

            stats.setRequestCount(totalRequests);
            stats.setTokenUsage(totalTokens);
            stats.setAvgLatencyMs(newAvgLatency);
            aiUsageStatsMapper.updateById(stats);
        }
    }
}
