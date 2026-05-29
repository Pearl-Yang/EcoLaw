package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 宠物成就实体
 */
@Data
@TableName("pet_achievement")
public class PetAchievement {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 成就代码
     */
    private String achievementCode;

    /**
     * 成就名称
     */
    private String achievementName;

    /**
     * 成就类型：interaction(互动)、level(等级)、time(时间)、collection(收集)
     */
    private String achievementType;

    /**
     * 成就描述
     */
    private String description;

    /**
     * 图标URL
     */
    private String iconUrl;

    /**
     * 达成条件类型
     */
    private String conditionType;

    /**
     * 达成条件值
     */
    private Integer conditionValue;

    /**
     * 达成条件参数（如特定任务代码）
     */
    private String conditionParam;

    /**
     * 经验奖励
     */
    private Integer expReward;

    /**
     * 金币奖励
     */
    private Integer coinReward;

    /**
     * 称号奖励
     */
    private String titleReward;

    /**
     * 头像框奖励
     */
    private String avatarFrameReward;

    /**
     * 是否隐藏成就
     */
    private Boolean isHidden;

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

    // ==================== 成就类型常量 ====================
    public static final String TYPE_INTERACTION = "interaction";
    public static final String TYPE_LEVEL = "level";
    public static final String TYPE_TIME = "time";
    public static final String TYPE_COLLECTION = "collection";

    // ==================== 条件类型常量 ====================
    public static final String COND_TASK_COUNT = "task_count";
    public static final String COND_LEVEL = "level";
    public static final String COND_LOGIN_DAYS = "login_days";
    public static final String COND_SCENE_COUNT = "scene_count";
    public static final String COND_TASK_TYPE_COUNT = "task_type_count";
}
