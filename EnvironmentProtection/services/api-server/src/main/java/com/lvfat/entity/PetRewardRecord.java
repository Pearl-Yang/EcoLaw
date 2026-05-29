package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 宠物奖励记录实体
 */
@Data
@TableName("pet_reward_record")
public class PetRewardRecord {

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
     * 奖励类型：exp(经验)、coin(金币)、item(物品)
     */
    private String rewardType;

    /**
     * 奖励值
     */
    private Integer rewardValue;

    /**
     * 奖励名称（如物品名称）
     */
    private String rewardName;

    /**
     * 奖励来源：task_complete、daily_login、achievement、level_up
     */
    private String source;

    /**
     * 来源ID（如任务ID）
     */
    private Long sourceId;

    /**
     * 来源名称（如任务名称）
     */
    private String sourceName;

    /**
     * 变化前宠物等级
     */
    private Integer petLevelBefore;

    /**
     * 变化后宠物等级
     */
    private Integer petLevelAfter;

    /**
     * 变化前宠物经验
     */
    private Integer petExpBefore;

    /**
     * 变化后宠物经验
     */
    private Integer petExpAfter;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    // ==================== 奖励类型常量 ====================
    public static final String TYPE_EXP = "exp";
    public static final String TYPE_COIN = "coin";
    public static final String TYPE_ITEM = "item";

    // ==================== 来源常量 ====================
    public static final String SOURCE_TASK_COMPLETE = "task_complete";
    public static final String SOURCE_DAILY_LOGIN = "daily_login";
    public static final String SOURCE_ACHIEVEMENT = "achievement";
    public static final String SOURCE_LEVEL_UP = "level_up";
}
