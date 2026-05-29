package com.lvfat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lvfat.common.result.Result;
import com.lvfat.dto.PageResult;
import com.lvfat.entity.Comment;
import com.lvfat.service.CommentService;
import com.lvfat.service.NewsService;
import com.lvfat.service.UserLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论管理
 */
@Tag(name = "评论管理")
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserLikeService userLikeService;
    private final NewsService newsService;

    @Operation(summary = "获取目标评论列表（小程序端用）")
    @GetMapping
    public Result<List<Comment>> listComments(
            @Parameter(description = "目标类型: news, training, case_info") @RequestParam String targetType,
            @Parameter(description = "目标ID") @RequestParam String targetId
    ) {
        return Result.success(commentService.listByTarget(targetType, targetId));
    }

    @Operation(summary = "获取评论列表（管理端用，支持分页）")
    @GetMapping("/page")
    public Result<PageResult<Comment>> listCommentsPage(
            @Parameter(description = "页码") @RequestParam(required = false, defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @Parameter(description = "目标类型") @RequestParam(required = false) String targetType,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态") @RequestParam(required = false) String status
    ) {
        IPage<Comment> result = commentService.listCommentsPage(page, pageSize, targetType, keyword, status);
        return Result.success(PageResult.of(result.getRecords(), result.getTotal(), (int) result.getCurrent(), (int) result.getSize()));
    }

    @Operation(summary = "获取评论的回复列表")
    @GetMapping("/{id}/replies")
    public Result<List<Comment>> listReplies(@PathVariable String id) {
        return Result.success(commentService.listReplies(id));
    }

    @Operation(summary = "发表评论")
    @PostMapping
    public Result<Comment> createComment(@RequestBody Comment comment) {
        commentService.save(comment);

        if (comment.getParentId() != null) {
            commentService.incrementReplyCount(comment.getParentId());
            if ("news".equals(comment.getTargetType())) {
                newsService.incrementCommentCount(comment.getTargetId());
            }
        }

        return Result.success(comment);
    }

    @Operation(summary = "点赞评论")
    @PostMapping("/{id}/like")
    public Result<Void> likeComment(
            @PathVariable String id,
            @Parameter(description = "用户ID") @RequestParam String userId
    ) {
        if (userLikeService.isLiked(userId, "comment", id)) {
            userLikeService.unlike(userId, "comment", id);
            commentService.decrementLikeCount(id);
            return Result.success("取消点赞成功");
        } else {
            userLikeService.like(userId, "comment", id);
            commentService.incrementLikeCount(id);
            return Result.success("点赞成功");
        }
    }

    @Operation(summary = "检查评论是否已点赞")
    @GetMapping("/{id}/like/status")
    public Result<Boolean> isLiked(
            @PathVariable String id,
            @Parameter(description = "用户ID") @RequestParam String userId
    ) {
        return Result.success(userLikeService.isLiked(userId, "comment", id));
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(@PathVariable String id) {
        Comment comment = commentService.getById(id);
        if (comment != null && comment.getParentId() != null) {
            commentService.decrementReplyCount(comment.getParentId());
        }
        commentService.removeById(id);
        return Result.success("评论删除成功");
    }
}
