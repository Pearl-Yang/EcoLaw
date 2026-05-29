package com.lvfat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.PetInteractionTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 互动任务Mapper
 */
@Mapper
public interface PetInteractionTaskMapper extends BaseMapper<PetInteractionTask> {

    /**
     * 根据任务类型查询任务列表
     */
    @Select("SELECT * FROM pet_interaction_task WHERE task_type = #{taskType} AND is_active = TRUE ORDER BY sort_order")
    List<PetInteractionTask> findByTaskType(@Param("taskType") String taskType);

    /**
     * 查询所有启用的任务
     */
    @Select("SELECT * FROM pet_interaction_task WHERE is_active = TRUE ORDER BY sort_order")
    List<PetInteractionTask> findAllActive();

    /**
     * 根据任务代码查询
     */
    @Select("SELECT * FROM pet_interaction_task WHERE task_code = #{taskCode}")
    PetInteractionTask findByTaskCode(@Param("taskCode") String taskCode);

    /**
     * 根据最低等级查询可完成的任务
     */
    @Select("SELECT * FROM pet_interaction_task WHERE is_active = TRUE AND min_pet_level <= #{petLevel} ORDER BY sort_order")
    List<PetInteractionTask> findAvailableTasks(@Param("petLevel") Integer petLevel);
}
