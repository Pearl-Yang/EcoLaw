package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户宠物成就记录实体
 */
@Data
@TableName("user_pet_achievement")
public class UserPetAchievement {

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
     * 成就ID
     */
    private Long achievementId;

    /**
     * 成就代码
     */
    private String achievementCode;

    /**
     * 解锁时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime unlockedAt;

    /**
     * 是否已发送通知
     */
    private Boolean notificationSent;
}
