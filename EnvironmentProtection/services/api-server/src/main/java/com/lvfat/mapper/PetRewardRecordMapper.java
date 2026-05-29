package com.lvfat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.PetRewardRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 宠物奖励记录Mapper
 */
@Mapper
public interface PetRewardRecordMapper extends BaseMapper<PetRewardRecord> {

    /**
     * 查询用户宠物的奖励记录
     */
    @Select("SELECT * FROM pet_reward_record WHERE user_id = #{userId} AND pet_id = #{petId} ORDER BY created_at DESC")
    List<PetRewardRecord> findByUserIdAndPetId(@Param("userId") Long userId, @Param("petId") Long petId);

    /**
     * 根据来源查询奖励记录
     */
    @Select("SELECT * FROM pet_reward_record WHERE user_id = #{userId} AND source = #{source} ORDER BY created_at DESC")
    List<PetRewardRecord> findByUserIdAndSource(@Param("userId") Long userId, @Param("source") String source);

    /**
     * 统计用户宠物获得的总经验
     */
    @Select("SELECT COALESCE(SUM(reward_value), 0) FROM pet_reward_record WHERE user_id = #{userId} AND pet_id = #{petId} AND reward_type = 'exp'")
    int sumExpByUserIdAndPetId(@Param("userId") Long userId, @Param("petId") Long petId);

    /**
     * 统计用户宠物获得的总金币
     */
    @Select("SELECT COALESCE(SUM(reward_value), 0) FROM pet_reward_record WHERE user_id = #{userId} AND pet_id = #{petId} AND reward_type = 'coin'")
    int sumCoinByUserIdAndPetId(@Param("userId") Long userId, @Param("petId") Long petId);

    /**
     * 查询最近的奖励记录
     */
    @Select("SELECT * FROM pet_reward_record WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT #{limit}")
    List<PetRewardRecord> findRecentByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);
}
