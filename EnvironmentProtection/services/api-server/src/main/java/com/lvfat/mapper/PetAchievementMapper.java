package com.lvfat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.PetAchievement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 宠物成就Mapper
 */
@Mapper
public interface PetAchievementMapper extends BaseMapper<PetAchievement> {

    /**
     * 根据成就代码查询
     */
    @Select("SELECT * FROM pet_achievement WHERE achievement_code = #{achievementCode}")
    PetAchievement findByAchievementCode(@Param("achievementCode") String achievementCode);

    /**
     * 根据成就类型查询
     */
    @Select("SELECT * FROM pet_achievement WHERE achievement_type = #{type} AND is_active = TRUE ORDER BY sort_order")
    List<PetAchievement> findByAchievementType(@Param("type") String type);

    /**
     * 查询所有启用的成就
     */
    @Select("SELECT * FROM pet_achievement WHERE is_active = TRUE ORDER BY sort_order")
    List<PetAchievement> findAllActive();

    /**
     * 查询非隐藏的成就
     */
    @Select("SELECT * FROM pet_achievement WHERE is_active = TRUE AND is_hidden = FALSE ORDER BY sort_order")
    List<PetAchievement> findVisibleAchievements();
}
