package com.lvfat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 等级信息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LevelInfoDTO {

    /**
     * 当前等级
     */
    private Integer currentLevel;

    /**
     * 当前经验
     */
    private Integer currentExp;

    /**
     * 当前等级称号
     */
    private String levelTitle;

    /**
     * 当前经验加成倍率
     */
    private Double expBonusRate;

    /**
     * 当前金币加成倍率
     */
    private Double coinBonusRate;

    /**
     * 下一级所需经验
     */
    private Integer expToNextLevel;

    /**
     * 下一级等级
     */
    private Integer nextLevel;

    /**
     * 下一级称号
     */
    private String nextLevelTitle;

    /**
     * 升级进度百分比
     */
    private Double progressPercent;

    /**
     * 是否满级
     */
    private Boolean isMaxLevel;

    /**
     * 解锁的技能
     */
    private String unlockedSkill;

    /**
     * 解锁的场景
     */
    private String unlockedScene;

    /**
     * 解锁的好友需求
     */
    private Integer requiredFriends;

    /**
     * 解锁的任务需求
     */
    private Integer requiredTasks;

    /**
     * 头像框样式
     */
    private String avatarFrame;
}
