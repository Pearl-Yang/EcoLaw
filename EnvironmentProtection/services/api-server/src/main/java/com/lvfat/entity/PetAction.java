package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 宠物动作实体
 */
@Data
@TableName("pet_action")
public class PetAction {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 动作代码
     */
    private String actionCode;

    /**
     * 动作名称
     */
    private String actionName;

    /**
     * 3D动画名称
     */
    private String animationName;

    /**
     * 动作持续时间（秒）
     */
    private BigDecimal durationSeconds;

    /**
     * 循环次数，-1表示无限循环
     */
    private Integer loopCount;

    /**
     * 混合模式：default、additive、override
     */
    private String blendMode;

    /**
     * 是否可被中断
     */
    private Boolean canInterrupt;

    /**
     * 音效URL
     */
    private String soundEffectUrl;

    /**
     * 粒子特效配置
     */
    private String particleEffect;

    /**
     * 情绪值变化
     */
    private Integer emotionChange;

    /**
     * 体力值变化
     */
    private Integer energyChange;

    /**
     * 是否启用
     */
    private Boolean isActive;

    /**
     * 动作描述
     */
    private String description;

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

    // ==================== 动作代码常量 ====================
    public static final String ACTION_IDLE = "idle";
    public static final String ACTION_WALK = "walk";
    public static final String ACTION_RUN = "run";
    public static final String ACTION_JUMP = "jump";
    public static final String ACTION_EAT = "eat";
    public static final String ACTION_PET = "pet";
    public static final String ACTION_CLEAN = "clean";
    public static final String ACTION_PLAY = "play";
    public static final String ACTION_TRAIN = "train";
    public static final String ACTION_SLEEP = "sleep";
    public static final String ACTION_HAPPY = "happy";
    public static final String ACTION_SAD = "sad";
    public static final String ACTION_ANGRY = "angry";
    public static final String ACTION_ROLL = "roll";
    public static final String ACTION_FLY = "fly";

    // ==================== 混合模式常量 ====================
    public static final String BLEND_DEFAULT = "default";
    public static final String BLEND_ADDITIVE = "additive";
    public static final String BLEND_OVERRIDE = "override";
}
