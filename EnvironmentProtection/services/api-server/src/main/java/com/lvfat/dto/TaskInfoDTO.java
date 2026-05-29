package com.lvfat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 任务信息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskInfoDTO {

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
     * 任务类型显示名称
     */
    private String taskTypeName;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 任务图标URL
     */
    private String iconUrl;

    /**
     * 经验奖励
     */
    private Integer expReward;

    /**
     * 金币奖励
     */
    private Integer coinReward;

    /**
     * 物品奖励JSON
     */
    private String itemRewardJson;

    /**
     * 冷却时间（秒）
     */
    private Integer cooldownSeconds;

    /**
     * 最低宠物等级要求
     */
    private Integer minPetLevel;

    /**
     * 体力消耗
     */
    private Integer energyCost;

    /**
     * 互动持续时间（秒）
     */
    private Integer durationSeconds;

    /**
     * 动画类型
     */
    private String animationType;

    /**
     * 是否在冷却中
     */
    private Boolean inCooldown;

    /**
     * 冷却剩余时间（秒）
     */
    private Integer cooldownRemaining;

    /**
     * 上次完成时间
     */
    private LocalDateTime lastCompletedAt;

    /**
     * 今日完成次数
     */
    private Integer todayCompletedCount;

    /**
     * 任务是否可用
     */
    private Boolean available;

    /**
     * 不可用原因
     */
    private String unavailableReason;
}
