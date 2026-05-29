package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 新闻/热点实体
 */
@Data
@TableName("news")
public class News {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 新闻标题
     */
    private String title;

    /**
     * 摘要
     */
    private String summary;

    /**
     * 新闻内容
     */
    private String content;

    /**
     * 封面图片URL
     */
    private String coverUrl;

    /**
     * 类型: news-新闻, hotspot-热点, policy-政策解读, activity-活动
     */
    private String type;

    /**
     * 来源
     */
    private String source;

    /**
     * 作者
     */
    private String author;

    /**
     * 标签(JSON数组)
     */
    private String tags;

    /**
     * 是否置顶: 0-否, 1-是
     */
    private Integer isTop;

    /**
     * 是否热门: 0-否, 1-是
     */
    private Integer isHot;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 点赞次数
     */
    private Integer likeCount;

    /**
     * 评论次数
     */
    private Integer commentCount;

    /**
     * 状态: draft-草稿, published-已发布, archived-已归档
     */
    private String status;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 创建人ID
     */
    private String creatorId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
