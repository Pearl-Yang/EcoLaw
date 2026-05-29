package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知实体
 */
@Data
@TableName("notification")
public class Notification {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 类型: system-系统通知, task-任务通知, training-培训通知, activity-活动通知, report-举报通知
     */
    private String type;

    /**
     * 优先级: low-低, normal-普通, high-高, urgent-紧急
     */
    private String priority;

    /**
     * 目标类型: all-全体, user-指定用户, role-指定角色, organization-指定组织
     */
    private String targetType;

    /**
     * 目标值(用户ID/角色/组织ID，多个用逗号分隔)
     */
    private String targetValue;

    /**
     * 点击跳转URL
     */
    private String actionUrl;

    /**
     * 跳转参数(JSON)
     */
    private String actionParams;

    /**
     * 封面图片URL
     */
    private String coverUrl;

    /**
     * 是否已读(针对个人通知)
     */
    private Integer isRead;

    /**
     * 阅读时间
     */
    private LocalDateTime readTime;

    /**
     * 接收用户ID(针对个人通知)
     */
    private String userId;

    /**
     * 发送人ID
     */
    private String senderId;

    /**
     * 发送人名称
     */
    private String senderName;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 状态: draft-草稿, published-已发布, cancelled-已取消
     */
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
