package com.lvfat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.PetAction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 宠物动作Mapper
 */
@Mapper
public interface PetActionMapper extends BaseMapper<PetAction> {

    /**
     * 根据动作代码查询
     */
    @Select("SELECT * FROM pet_action WHERE action_code = #{actionCode}")
    PetAction findByActionCode(@Param("actionCode") String actionCode);

    /**
     * 查询所有启用的动作
     */
    @Select("SELECT * FROM pet_action WHERE is_active = TRUE ORDER BY id")
    List<PetAction> findAllActive();

    /**
     * 查询可循环播放的动作
     */
    @Select("SELECT * FROM pet_action WHERE is_active = TRUE AND (loop_count = -1 OR loop_count > 1) ORDER BY id")
    List<PetAction> findLoopableActions();
}
