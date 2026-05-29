package com.lvfat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.PetLevelConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 宠物等级配置Mapper
 */
@Mapper
public interface PetLevelConfigMapper extends BaseMapper<PetLevelConfig> {

    /**
     * 根据等级查询配置
     */
    @Select("SELECT * FROM pet_level_config WHERE level = #{level}")
    PetLevelConfig findByLevel(@Param("level") Integer level);

    /**
     * 根据经验值查询对应的等级
     */
    @Select("SELECT * FROM pet_level_config WHERE min_exp <= #{exp} AND (max_exp IS NULL OR max_exp >= #{exp}) ORDER BY level DESC LIMIT 1")
    PetLevelConfig findByExp(@Param("exp") Integer exp);

    /**
     * 获取最高等级配置
     */
    @Select("SELECT * FROM pet_level_config ORDER BY level DESC LIMIT 1")
    PetLevelConfig findMaxLevel();

    /**
     * 获取下一等级配置
     */
    @Select("SELECT * FROM pet_level_config WHERE level = #{currentLevel} + 1")
    PetLevelConfig findNextLevel(@Param("currentLevel") Integer currentLevel);
}
