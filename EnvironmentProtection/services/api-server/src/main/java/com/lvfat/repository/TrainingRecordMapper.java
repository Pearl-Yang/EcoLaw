package com.lvfat.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.TrainingRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 培训记录 Mapper
 */
@Mapper
public interface TrainingRecordMapper extends BaseMapper<TrainingRecord> {
}
