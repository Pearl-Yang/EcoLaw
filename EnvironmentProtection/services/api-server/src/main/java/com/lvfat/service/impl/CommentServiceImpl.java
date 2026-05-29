package com.lvfat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lvfat.entity.Comment;
import com.lvfat.repository.CommentMapper;
import com.lvfat.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 评论服务实现
 */
@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Override
    public List<Comment> listByTarget(String targetType, String targetId) {
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("target_type", targetType)
                .eq("target_id", targetId)
                .eq("status", "published")
                .isNull("parent_id")
                .orderByDesc("create_time");
        return list(wrapper);
    }

    @Override
    public IPage<Comment> listCommentsPage(Integer page, Integer pageSize, String targetType, String keyword, String status) {
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        // 如果没有指定状态，返回所有状态（包括待审核）
        if (StringUtils.hasText(status)) {
            wrapper.eq("status", status);
        }
        if (StringUtils.hasText(targetType)) {
            wrapper.eq("target_type", targetType);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like("content", keyword).or().like("user_nickname", keyword));
        }
        wrapper.orderByDesc("create_time");
        return page(new Page<>(page != null ? page : 1, pageSize != null ? pageSize : 10), wrapper);
    }

    @Override
    public List<Comment> listReplies(String parentId) {
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", parentId)
                .eq("status", "published")
                .orderByAsc("create_time");
        return list(wrapper);
    }

    @Override
    public void incrementLikeCount(String id) {
        Comment comment = getById(id);
        if (comment != null) {
            comment.setLikeCount(comment.getLikeCount() + 1);
            updateById(comment);
        }
    }

    @Override
    public void decrementLikeCount(String id) {
        Comment comment = getById(id);
        if (comment != null && comment.getLikeCount() > 0) {
            comment.setLikeCount(comment.getLikeCount() - 1);
            updateById(comment);
        }
    }

    @Override
    public void incrementReplyCount(String id) {
        Comment comment = getById(id);
        if (comment != null) {
            comment.setReplyCount(comment.getReplyCount() + 1);
            updateById(comment);
        }
    }

    @Override
    public void decrementReplyCount(String id) {
        Comment comment = getById(id);
        if (comment != null && comment.getReplyCount() > 0) {
            comment.setReplyCount(comment.getReplyCount() - 1);
            updateById(comment);
        }
    }
}
