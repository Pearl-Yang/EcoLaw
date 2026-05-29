package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 宠物场景扩展实体
 */
@Data
@TableName("pet_scene_ext")
public class PetSceneExt {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 场景代码
     */
    private String sceneCode;

    /**
     * 场景名称
     */
    private String sceneName;

    /**
     * 场景描述
     */
    private String sceneDescription;

    /**
     * 缩略图URL
     */
    private String thumbnailUrl;

    /**
     * 3D模型URL
     */
    private String modelUrl;

    /**
     * 背景图URL
     */
    private String backgroundUrl;

    /**
     * 背景音乐URL
     */
    private String musicUrl;

    /**
     * 粒子特效配置
     */
    private String particleEffect;

    /**
     * 解锁所需等级
     */
    private Integer requiredLevel;

    /**
     * 解锁所需金币
     */
    private Integer requiredCoin;

    /**
     * 是否为默认场景
     */
    private Boolean isDefault;

    /**
     * 是否启用
     */
    private Boolean isActive;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    /**
     * 地图边界X最小值
     */
    private BigDecimal mapBoundsXMin;

    /**
     * 地图边界X最大值
     */
    private BigDecimal mapBoundsXMax;

    /**
     * 地图边界Z最小值
     */
    private BigDecimal mapBoundsZMin;

    /**
     * 地图边界Z最大值
     */
    private BigDecimal mapBoundsZMax;

    /**
     * 出生点X坐标
     */
    private BigDecimal spawnPointX;

    /**
     * 出生点Z坐标
     */
    private BigDecimal spawnPointZ;

    /**
     * 互动点配置JSON
     */
    private String interactivePointsJson;

    /**
     * 装饰物配置JSON
     */
    private String decorationItemsJson;

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
}
