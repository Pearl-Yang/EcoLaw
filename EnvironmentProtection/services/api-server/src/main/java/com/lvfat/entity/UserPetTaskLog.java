package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户宠物任务日志实体
 */
@Data
@TableName("user_pet_task_log")
public class UserPetTaskLog {

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
     * 完成时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime completedAt;

    /**
     * 获得经验值
     */
    private Integer expGained;

    /**
     * 获得金币
     */
    private Integer coinGained;

    /**
     * 获得的物品JSON
     */
    private String itemRewardJson;

    /**
     * 完成耗时（毫秒）
     */
    private Integer durationMs;

    /**
     * 操作IP地址
     */
    private String ipAddress;

    /**
     * 设备信息
     */
    private String deviceInfo;
}
