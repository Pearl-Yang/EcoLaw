package com.lvfat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 宠物场景状态DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetSceneStateDTO {

    /**
     * 空间ID
     */
    private Long spaceId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 宠物ID
     */
    private Long petId;

    /**
     * 宠物名称
     */
    private String petName;

    /**
     * 宠物等级
     */
    private Integer petLevel;

    /**
     * 宠物经验值
     */
    private Integer petExp;

    /**
     * 当前场景代码
     */
    private String currentScene;

    /**
     * 当前场景名称
     */
    private String currentSceneName;

    /**
     * 宠物当前位置X
     */
    private BigDecimal positionX;

    /**
     * 宠物当前位置Y
     */
    private BigDecimal positionY;

    /**
     * 宠物当前位置Z
     */
    private BigDecimal positionZ;

    /**
     * 宠物朝向角度
     */
    private Float directionAngle;

    /**
     * 空间等级
     */
    private Integer spaceLevel;

    /**
     * 已解锁的场景列表
     */
    private List<SceneInfo> unlockedScenes;

    /**
     * 今日互动次数
     */
    private Integer todayInteractionCount;

    /**
     * 今日获得经验
     */
    private Integer todayExpGained;

    /**
     * 宠物能量值
     */
    private Integer energy;

    /**
     * 宠物情绪值
     */
    private Integer emotion;

    /**
     * 场景装饰物列表
     */
    private List<DecorationInfo> decorations;

    /**
     * 互动点列表
     */
    private List<InteractivePoint> interactivePoints;

    /**
     * 场景信息内部类
     */
    @Data
    public static class SceneInfo {
        private String sceneCode;
        private String sceneName;
        private String thumbnailUrl;
        private Boolean isUnlocked;
        private Integer requiredLevel;
    }

    /**
     * 装饰物信息内部类
     */
    @Data
    public static class DecorationInfo {
        private String id;
        private String type;
        private BigDecimal positionX;
        private BigDecimal positionY;
        private BigDecimal positionZ;
        private String modelUrl;
    }

    /**
     * 互动点信息内部类
     */
    @Data
    public static class InteractivePoint {
        private String id;
        private String pointType;
        private BigDecimal positionX;
        private BigDecimal positionY;
        private BigDecimal positionZ;
        private String actionType;
        private String description;
    }
}
