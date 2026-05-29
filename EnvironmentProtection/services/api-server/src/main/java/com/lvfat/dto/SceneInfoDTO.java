package com.lvfat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 场景信息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SceneInfoDTO {

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
    private String description;

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
     * 是否已解锁
     */
    private Boolean isUnlocked;

    /**
     * 是否为默认场景
     */
    private Boolean isDefault;

    /**
     * 地图边界
     */
    private MapBounds mapBounds;

    /**
     * 出生点位置
     */
    private SpawnPoint spawnPoint;

    /**
     * 互动点配置
     */
    private List<InteractivePointConfig> interactivePoints;

    /**
     * 装饰物配置
     */
    private List<DecorationConfig> decorations;

    /**
     * 地图边界内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MapBounds {
        private BigDecimal xMin;
        private BigDecimal xMax;
        private BigDecimal zMin;
        private BigDecimal zMax;
    }

    /**
     * 出生点内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpawnPoint {
        private BigDecimal x;
        private BigDecimal z;
    }

    /**
     * 互动点配置内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InteractivePointConfig {
        private String id;
        private String type;
        private BigDecimal x;
        private BigDecimal y;
        private BigDecimal z;
        private String actionType;
        private String description;
        private BigDecimal radius;
    }

    /**
     * 装饰物配置内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DecorationConfig {
        private String id;
        private String type;
        private BigDecimal x;
        private BigDecimal y;
        private BigDecimal z;
        private BigDecimal scale;
        private BigDecimal rotationY;
    }
}
