package com.lvfat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.PetSceneExt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 宠物场景扩展Mapper
 */
@Mapper
public interface PetSceneExtMapper extends BaseMapper<PetSceneExt> {

    /**
     * 根据场景代码查询
     */
    @Select("SELECT * FROM pet_scene_ext WHERE scene_code = #{sceneCode}")
    PetSceneExt findBySceneCode(@Param("sceneCode") String sceneCode);

    /**
     * 查询所有启用的场景
     */
    @Select("SELECT * FROM pet_scene_ext WHERE is_active = TRUE ORDER BY sort_order")
    List<PetSceneExt> findAllActive();

    /**
     * 根据解锁等级查询可解锁的场景
     */
    @Select("SELECT * FROM pet_scene_ext WHERE is_active = TRUE AND required_level <= #{petLevel} ORDER BY sort_order")
    List<PetSceneExt> findUnlockableScenes(@Param("petLevel") Integer petLevel);

    /**
     * 查询默认场景
     */
    @Select("SELECT * FROM pet_scene_ext WHERE is_default = TRUE LIMIT 1")
    PetSceneExt findDefaultScene();
}
