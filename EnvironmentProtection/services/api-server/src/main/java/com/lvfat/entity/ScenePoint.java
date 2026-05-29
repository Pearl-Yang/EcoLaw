package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 场景互动点实体
 */
@Data
@TableName("scene_point")
public class ScenePoint {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 场景ID
     */
    private Integer sceneId;

    /**
     * 互动点名称
     */
    private String pointName;

    /**
     * 类型: energy-能量点, task-任务点, law-普法点
     */
    private String pointType;

    /**
     * 图标
     */
    private String pointIcon;

    /**
     * X坐标
     */
    private Double x;

    /**
     * Y坐标
     */
    private Double y;

    /**
     * 触发半径
     */
    private Double radius;

    /**
     * 奖励能量
     */
    private Integer rewardEnergy;

    /**
     * 奖励经验
     */
    private Integer rewardExp;

    /**
     * 描述
     */
    private String description;

    /**
     * 关联内容ID
     */
    private String contentId;

    /**
     * 每日限制次数
     */
    private Integer dailyLimit;

    /**
     * 冷却时间(秒)
     */
    private Integer cooldownSeconds;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态: 0-禁用, 1-启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // ==================== 常量定义 ====================
    public static final String TYPE_ENERGY = "energy";
    public static final String TYPE_TASK = "task";
    public static final String TYPE_LAW = "law";

    public static final Integer STATUS_DISABLED = 0;
    public static final Integer STATUS_ENABLED = 1;
}
