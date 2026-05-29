package com.lvfat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.UserPetAchievement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户宠物成就记录Mapper
 */
@Mapper
public interface UserPetAchievementMapper extends BaseMapper<UserPetAchievement> {

    /**
     * 查询用户宠物的成就记录
     */
    @Select("SELECT * FROM user_pet_achievement WHERE user_id = #{userId} AND pet_id = #{petId}")
    List<UserPetAchievement> findByUserIdAndPetId(@Param("userId") Long userId, @Param("petId") Long petId);

    /**
     * 检查用户宠物是否已解锁指定成就
     */
    @Select("SELECT COUNT(*) FROM user_pet_achievement WHERE user_id = #{userId} AND pet_id = #{petId} AND achievement_code = #{achievementCode}")
    int countByUserIdAndPetIdAndAchievementCode(@Param("userId") Long userId, @Param("petId") Long petId, @Param("achievementCode") String achievementCode);

    /**
     * 统计用户宠物解锁的成就数量
     */
    @Select("SELECT COUNT(*) FROM user_pet_achievement WHERE user_id = #{userId} AND pet_id = #{petId}")
    int countByUserIdAndPetId(@Param("userId") Long userId, @Param("petId") Long petId);
}
