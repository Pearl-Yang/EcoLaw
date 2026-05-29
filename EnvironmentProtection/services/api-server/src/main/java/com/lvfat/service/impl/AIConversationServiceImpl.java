package com.lvfat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lvfat.entity.AIConversation;
import com.lvfat.entity.AIMessage;
import com.lvfat.repository.AIConversationMapper;
import com.lvfat.repository.AIMessageMapper;
import com.lvfat.service.AIConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI对话会话服务实现
 */
@Service
@RequiredArgsConstructor
public class AIConversationServiceImpl extends ServiceImpl<AIConversationMapper, AIConversation>
        implements AIConversationService {

    private final AIMessageMapper aiMessageMapper;

    @Override
    public List<AIConversation> listByUser(String userId, String sessionType, Integer page, Integer pageSize) {
        LambdaQueryWrapper<AIConversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AIConversation::getUserId, userId)
                .eq(sessionType != null && !sessionType.isEmpty(),
                        AIConversation::getSessionType, sessionType)
                .eq(AIConversation::getDeleted, 0)
                .orderByDesc(AIConversation::getLastMessageTime);

        if (page != null && pageSize != null) {
            Page<AIConversation> pageResult = new Page<>(page, pageSize);
            page(pageResult, wrapper);
            return pageResult.getRecords();
        }

        return list(wrapper);
    }

    @Override
    public AIConversation getConversationDetail(String conversationId) {
        AIConversation conversation = getById(conversationId);
        if (conversation != null) {
            // 获取消息列表
            LambdaQueryWrapper<AIMessage> msgWrapper = new LambdaQueryWrapper<>();
            msgWrapper.eq(AIMessage::getConversationId, conversationId)
                    .orderByAsc(AIMessage::getCreateTime);
            List<AIMessage> messages = aiMessageMapper.selectList(msgWrapper);
            // 将消息列表设置到conversation中（如果实体支持的话，这里暂时通过其他方式处理）
        }
        return conversation;
    }

    @Override
    @Transactional
    public AIConversation createConversation(String userId, String sessionType, String firstMessage) {
        AIConversation conversation = new AIConversation();
        conversation.setUserId(userId);
        conversation.setSessionType(sessionType != null ? sessionType : "law");
        conversation.setTitle(firstMessage != null && firstMessage.length() > 50
                ? firstMessage.substring(0, 50) + "..."
                : firstMessage);
        conversation.setMessageCount(0);
        conversation.setStatus("active");
        conversation.setLastMessageTime(LocalDateTime.now());
        save(conversation);
        return conversation;
    }

    @Override
    @Transactional
    public void updateConversationSummary(String conversationId, String title, String summary) {
        AIConversation conversation = getById(conversationId);
        if (conversation != null) {
            if (title != null) {
                conversation.setTitle(title.length() > 200 ? title.substring(0, 200) : title);
            }
            if (summary != null) {
                conversation.setContextSummary(summary);
            }
            conversation.setLastMessageTime(LocalDateTime.now());
            updateById(conversation);
        }
    }

    @Override
    @Transactional
    public void deleteConversation(String conversationId) {
        // 软删除会话
        AIConversation conversation = getById(conversationId);
        if (conversation != null) {
            conversation.setDeleted(1);
            updateById(conversation);
        }
    }

    @Override
    @Transactional
    public void archiveConversation(String conversationId) {
        AIConversation conversation = getById(conversationId);
        if (conversation != null) {
            conversation.setStatus("archived");
            updateById(conversation);
        }
    }
}
