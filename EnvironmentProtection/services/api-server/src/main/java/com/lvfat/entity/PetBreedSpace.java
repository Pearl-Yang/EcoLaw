package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 宠物养成空间实体
 */
@Data
@TableName("pet_breed_space")
public class PetBreedSpace {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 宠物ID
     */
    private Long petId;

    /**
     * 当前场景：garden(花园)、forest(森林)、beach(沙滩)、mountain(山峰)
     */
    private String currentScene;

    /**
     * 上次位置X坐标
     */
    private BigDecimal lastPositionX;

    /**
     * 上次位置Y坐标(高度)
     */
    private BigDecimal lastPositionY;

    /**
     * 上次位置Z坐标
     */
    private BigDecimal lastPositionZ;

    /**
     * 空间等级
     */
    private Integer spaceLevel;

    /**
     * 空间经验值
     */
    private Integer spaceExp;

    /**
     * 已解锁的场景列表，逗号分隔
     */
    private String unlockedScenes;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    // ==================== 场景常量 ====================
    public static final String SCENE_GARDEN = "garden";
    public static final String SCENE_FOREST = "forest";
    public static final String SCENE_BEACH = "beach";
    public static final String SCENE_MOUNTAIN = "mountain";
    public static final String SCENE_SNOWLAND = "snowland";
    public static final String SCENE_NIGHT = "night";

    // ==================== 便捷方法 ====================
    public boolean hasUnlockedScene(String scene) {
        if (unlockedScenes == null || unlockedScenes.isEmpty()) {
            return SCENE_GARDEN.equals(scene);
        }
        for (String s : unlockedScenes.split(",")) {
            if (s.trim().equals(scene)) {
                return true;
            }
        }
        return false;
    }

    public void unlockScene(String scene) {
        if (!hasUnlockedScene(scene)) {
            if (unlockedScenes == null || unlockedScenes.isEmpty()) {
                unlockedScenes = scene;
            } else {
                unlockedScenes = unlockedScenes + "," + scene;
            }
        }
    }
}
