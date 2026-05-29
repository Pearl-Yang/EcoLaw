package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户点赞记录实体
 */
@Data
@TableName("user_like")
public class UserLike {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 目标类型: news-新闻, comment-评论
     */
    private String targetType;

    /**
     * 目标ID
     */
    private String targetId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
