package com.lvfat.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.Training;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 培训 Mapper
 */
@Mapper
public interface TrainingMapper extends BaseMapper<Training> {
    
    @Select("SELECT COUNT(*) FROM training WHERE deleted = 0")
    int countAll();
    
    @Select("SELECT COUNT(*) FROM training_record WHERE status = 'completed' AND deleted = 0")
    int countCompleted();
}
