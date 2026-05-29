package com.lvfat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 宠物信息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetInfoDTO {

    /**
     * 宠物ID
     */
    private Long petId;

    /**
     * 宠物名称
     */
    private String petName;

    /**
     * 宠物类型
     */
    private String petType;

    /**
     * 宠物模型URL
     */
    private String petModelUrl;

    /**
     * 宠物缩略图URL
     */
    private String petThumbnailUrl;

    /**
     * 当前等级
     */
    private Integer level;

    /**
     * 当前经验
     */
    private Integer exp;

    /**
     * 下一级所需经验
     */
    private Integer nextLevelExp;

    /**
     * 当前等级称号
     */
    private String levelTitle;

    /**
     * 经验加成倍率
     */
    private BigDecimal expBonusRate;

    /**
     * 金币加成倍率
     */
    private BigDecimal coinBonusRate;

    /**
     * 能量值
     */
    private Integer energy;

    /**
     * 情绪值
     */
    private Integer emotion;

    /**
     * 累计获得能量
     */
    private Integer totalEnergy;

    /**
     * 累计完成任务数
     */
    private Integer totalTasksCompleted;

    /**
     * 累计获得经验
     */
    private Integer totalExpGained;

    /**
     * 累计获得金币
     */
    private Integer totalCoinGained;

    /**
     * 已解锁技能列表
     */
    private List<String> unlockedSkills;

    /**
     * 已解锁场景列表
     */
    private List<String> unlockedScenes;

    /**
     * 已解锁成就数量
     */
    private Integer unlockedAchievementCount;

    /**
     * 头像框样式
     */
    private String avatarFrame;

    /**
     * 最后互动时间
     */
    private String lastInteractionTime;

    /**
     * 当前动作
     */
    private String currentAction;

    /**
     * 动作剩余时间（毫秒）
     */
    private Long actionRemainingMs;
}
