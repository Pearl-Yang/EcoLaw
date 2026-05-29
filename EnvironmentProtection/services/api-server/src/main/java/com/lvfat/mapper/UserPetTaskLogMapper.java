package com.lvfat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.UserPetTaskLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户宠物任务日志Mapper
 */
@Mapper
public interface UserPetTaskLogMapper extends BaseMapper<UserPetTaskLog> {

    /**
     * 查询用户宠物的任务完成记录
     */
    @Select("SELECT * FROM user_pet_task_log WHERE user_id = #{userId} AND pet_id = #{petId} ORDER BY completed_at DESC")
    List<UserPetTaskLog> findByUserIdAndPetId(@Param("userId") Long userId, @Param("petId") Long petId);

    /**
     * 查询用户在指定时间后的任务完成记录
     */
    @Select("SELECT * FROM user_pet_task_log WHERE user_id = #{userId} AND completed_at > #{afterTime} ORDER BY completed_at DESC")
    List<UserPetTaskLog> findByUserIdAfter(@Param("userId") Long userId, @Param("afterTime") LocalDateTime afterTime);

    /**
     * 统计用户宠物完成任务总数
     */
    @Select("SELECT COUNT(*) FROM user_pet_task_log WHERE user_id = #{userId} AND pet_id = #{petId}")
    int countByUserIdAndPetId(@Param("userId") Long userId, @Param("petId") Long petId);

    /**
     * 统计用户完成任务总数
     */
    @Select("SELECT COUNT(*) FROM user_pet_task_log WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") Long userId);

    /**
     * 查询用户宠物的最近一次任务完成记录
     */
    @Select("SELECT * FROM user_pet_task_log WHERE user_id = #{userId} AND pet_id = #{petId} AND task_code = #{taskCode} ORDER BY completed_at DESC LIMIT 1")
    UserPetTaskLog findLastByUserIdAndPetIdAndTaskCode(@Param("userId") Long userId, @Param("petId") Long petId, @Param("taskCode") String taskCode);

    /**
     * 查询用户宠物的累计经验
     */
    @Select("SELECT COALESCE(SUM(exp_gained), 0) FROM user_pet_task_log WHERE user_id = #{userId} AND pet_id = #{petId}")
    int sumExpByUserIdAndPetId(@Param("userId") Long userId, @Param("petId") Long petId);

    /**
     * 查询用户宠物的累计金币
     */
    @Select("SELECT COALESCE(SUM(coin_gained), 0) FROM user_pet_task_log WHERE user_id = #{userId} AND pet_id = #{petId}")
    int sumCoinByUserIdAndPetId(@Param("userId") Long userId, @Param("petId") Long petId);
}
