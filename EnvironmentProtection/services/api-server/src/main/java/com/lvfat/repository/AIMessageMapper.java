package com.lvfat.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.AIMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI对话消息 Mapper
 */
@Mapper
public interface AIMessageMapper extends BaseMapper<AIMessage> {
}
