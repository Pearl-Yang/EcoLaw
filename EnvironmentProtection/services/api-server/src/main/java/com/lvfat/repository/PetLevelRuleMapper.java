package com.lvfat.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.PetLevelRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 宠物升级规则 Mapper
 */
@Mapper
public interface PetLevelRuleMapper extends BaseMapper<PetLevelRule> {

    @Select("SELECT * FROM pet_level_rule WHERE level = #{level}")
    PetLevelRule getByLevel(@Param("level") Integer level);

    @Select("SELECT exp_required FROM pet_level_rule WHERE level = #{level}")
    Integer getExpRequiredByLevel(@Param("level") Integer level);
}
