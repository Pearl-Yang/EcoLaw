package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 互动任务实体
 */
@Data
@TableName("pet_interaction_task")
public class PetInteractionTask {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 任务代码，唯一标识
     */
    private String taskCode;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务类型：feed(喂食)、pet(抚摸)、clean(清洁)、train(训练)、play(玩耍)
     */
    private String taskType;

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
     * 是否启用
     */
    private Boolean isActive;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

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

    // ==================== 任务类型常量 ====================
    public static final String TYPE_FEED = "feed";
    public static final String TYPE_PET = "pet";
    public static final String TYPE_CLEAN = "clean";
    public static final String TYPE_TRAIN = "train";
    public static final String TYPE_PLAY = "play";

    // ==================== 动画类型常量 ====================
    public static final String ANIM_IDLE = "idle";
    public static final String ANIM_WALK = "walk";
    public static final String ANIM_EAT = "eat";
    public static final String ANIM_PET = "pet";
    public static final String ANIM_CLEAN = "clean";
    public static final String ANIM_PLAY = "play";
    public static final String ANIM_TRAIN = "train";
}
