package com.lvfat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lvfat.entity.Comment;
import java.util.List;

/**
 * 评论服务接口
 */
public interface CommentService extends IService<Comment> {
    /**
     * 获取目标评论列表
     */
    List<Comment> listByTarget(String targetType, String targetId);

    /**
     * 获取评论列表（管理端用，支持分页）
     */
    IPage<Comment> listCommentsPage(Integer page, Integer pageSize, String targetType, String keyword, String status);

    /**
     * 获取评论的回复列表
     */
    List<Comment> listReplies(String parentId);

    /**
     * 增加点赞次数
     */
    void incrementLikeCount(String id);

    /**
     * 减少点赞次数
     */
    void decrementLikeCount(String id);

    /**
     * 增加回复数量
     */
    void incrementReplyCount(String id);

    /**
     * 减少回复数量
     */
    void decrementReplyCount(String id);
}
