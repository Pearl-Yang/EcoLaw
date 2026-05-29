package com.lvfat.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.AIConversation;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI对话会话 Mapper
 */
@Mapper
public interface AIConversationMapper extends BaseMapper<AIConversation> {
}
