package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论实体
 */
@Data
@TableName("comment")
public class Comment {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 目标类型: news-新闻, training-培训, case_info-案例
     */
    private String targetType;

    /**
     * 目标ID
     */
    private String targetId;

    /**
     * 父评论ID(用于回复)
     */
    private String parentId;

    /**
     * 根评论ID(一级评论ID)
     */
    private String rootId;

    /**
     * 回复数量
     */
    private Integer replyCount;

    /**
     * 点赞次数
     */
    private Integer likeCount;

    /**
     * 状态: pending-待审核, published-已发布, deleted-已删除
     */
    private String status;

    /**
     * 评论用户ID
     */
    private String userId;

    /**
     * 评论用户昵称
     */
    private String userNickname;

    /**
     * 评论用户头像
     */
    private String userAvatar;

    /**
     * 审核人ID
     */
    private String reviewerId;

    /**
     * 审核时间
     */
    private LocalDateTime reviewTime;

    /**
     * 审核备注
     */
    private String reviewRemark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
