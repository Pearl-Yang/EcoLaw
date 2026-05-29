package com.lvfat.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.ScenePoint;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 场景互动点 Mapper
 */
@Mapper
public interface ScenePointMapper extends BaseMapper<ScenePoint> {

    @Select("SELECT * FROM scene_point WHERE scene_id = #{sceneId} AND status = 1 ORDER BY sort_order")
    List<ScenePoint> getBySceneId(@Param("sceneId") Integer sceneId);

    @Select("SELECT COUNT(*) FROM pet_interact_log WHERE point_id = #{pointId} AND DATE(interact_time) = CURDATE()")
    int getTodayInteractCount(@Param("pointId") String pointId);
}
