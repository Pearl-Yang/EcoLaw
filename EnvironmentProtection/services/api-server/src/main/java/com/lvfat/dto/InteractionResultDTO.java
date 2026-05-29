package com.lvfat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 互动结果DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InteractionResultDTO {

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 获得的经验值
     */
    private Integer expGained;

    /**
     * 获得的金币
     */
    private Integer coinGained;

    /**
     * 获得的经验值（实际，考虑加成）
     */
    private Integer actualExpGained;

    /**
     * 获得的金币（实际，考虑加成）
     */
    private Integer actualCoinGained;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 任务代码
     */
    private String taskCode;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务类型
     */
    private String taskType;

    /**
     * 是否有升级
     */
    private Boolean levelUp;

    /**
     * 升级后等级
     */
    private Integer newLevel;

    /**
     * 当前经验值
     */
    private Integer currentExp;

    /**
     * 下一级所需经验
     */
    private Integer nextLevelExp;

    /**
     * 当前等级称号
     */
    private String levelTitle;

    /**
     * 获得的物品JSON
     */
    private String itemRewards;

    /**
     * 剩余体力
     */
    private Integer remainingEnergy;

    /**
     * 宠物当前情绪值
     */
    private Integer currentEmotion;

    /**
     * 解锁的成就列表
     */
    private java.util.List<AchievementInfo> unlockedAchievements;

    /**
     * 解锁的场景
     */
    private String unlockedScene;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 成就信息内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AchievementInfo {
        private String achievementCode;
        private String achievementName;
        private Integer expReward;
        private Integer coinReward;
    }
}
