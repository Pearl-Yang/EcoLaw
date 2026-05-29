package com.lvfat.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.AIUsageStats;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI使用统计 Mapper
 */
@Mapper
public interface AIUsageStatsMapper extends BaseMapper<AIUsageStats> {
}
