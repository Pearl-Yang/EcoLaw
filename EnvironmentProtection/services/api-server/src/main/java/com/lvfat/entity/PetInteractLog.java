package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 宠物互动日志实体
 */
@Data
@TableName("pet_interact_log")
public class PetInteractLog {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 宠物ID
     */
    private String petId;

    /**
     * 互动点ID
     */
    private String pointId;

    /**
     * 互动点类型
     */
    private String pointType;

    /**
     * 获得能量
     */
    private Integer rewardEnergy;

    /**
     * 获得经验
     */
    private Integer rewardExp;

    /**
     * 是否升级: 0-否, 升级后等级
     */
    private Integer levelUp;

    /**
     * 关联任务ID
     */
    private String taskId;

    /**
     * 完成状态: 0-未完成, 1-已完成
     */
    private Integer completeStatus;

    /**
     * 互动时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime interactTime;

    // ==================== 常量定义 ====================
    public static final Integer COMPLETE_STATUS_NO = 0;
    public static final Integer COMPLETE_STATUS_YES = 1;
}
