package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户通知关联实体
 */
@Data
@TableName("user_notification")
public class UserNotification {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 通知ID
     */
    private String notificationId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 是否已读: 0-否, 1-是
     */
    private Integer isRead;

    /**
     * 阅读时间
     */
    private LocalDateTime readTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
