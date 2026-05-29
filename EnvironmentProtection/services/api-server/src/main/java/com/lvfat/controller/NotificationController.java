package com.lvfat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lvfat.common.result.Result;
import com.lvfat.dto.PageResult;
import com.lvfat.entity.Notification;
import com.lvfat.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知管理
 */
@Tag(name = "通知管理")
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "获取我的通知列表")
    @GetMapping
    public Result<List<Notification>> listMyNotifications(
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "类型") @RequestParam(required = false) String type,
            @Parameter(description = "页码") @RequestParam(required = false, defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        return Result.success(notificationService.listByUser(userId, type, page, size));
    }

    @Operation(summary = "获取通知列表（管理端用，支持分页）")
    @GetMapping("/page")
    public Result<PageResult<Notification>> listNotificationsPage(
            @Parameter(description = "页码") @RequestParam(required = false, defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @Parameter(description = "类型") @RequestParam(required = false) String type,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态") @RequestParam(required = false) String status
    ) {
        IPage<Notification> result = notificationService.listNotificationsPage(page, pageSize, type, keyword, status);
        return Result.success(PageResult.of(result.getRecords(), result.getTotal(), (int) result.getCurrent(), (int) result.getSize()));
    }

    @Operation(summary = "获取未读通知数量")
    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount(
            @Parameter(description = "用户ID") @RequestParam String userId
    ) {
        return Result.success(notificationService.getUnreadCount(userId));
    }

    @Operation(summary = "标记通知为已读")
    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(
            @PathVariable String id,
            @Parameter(description = "用户ID") @RequestParam String userId
    ) {
        notificationService.markAsRead(id, userId);
        return Result.success("标记已读成功");
    }

    @Operation(summary = "标记所有通知为已读")
    @PutMapping("/read-all")
    public Result<Void> markAllAsRead(
            @Parameter(description = "用户ID") @RequestParam String userId
    ) {
        notificationService.markAllAsRead(userId);
        return Result.success("全部标记已读成功");
    }

    @Operation(summary = "获取通知详情")
    @GetMapping("/{id}")
    public Result<Notification> getNotification(@PathVariable String id) {
        return Result.success(notificationService.getById(id));
    }

    @Operation(summary = "创建通知")
    @PostMapping
    public Result<Void> createNotification(@RequestBody Notification notification) {
        notificationService.save(notification);
        return Result.success("通知创建成功");
    }

    @Operation(summary = "更新通知")
    @PutMapping("/{id}")
    public Result<Void> updateNotification(@PathVariable String id, @RequestBody Notification notification) {
        notification.setId(id);
        notificationService.updateById(notification);
        return Result.success("通知更新成功");
    }

    @Operation(summary = "删除通知")
    @DeleteMapping("/{id}")
    public Result<Void> deleteNotification(@PathVariable String id) {
        notificationService.removeById(id);
        return Result.success("通知删除成功");
    }
}
