package com.lvfat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lvfat.entity.Notification;
import java.util.List;

/**
 * 通知服务接口
 */
public interface NotificationService extends IService<Notification> {
    /**
     * 获取用户通知列表
     */
    List<Notification> listByUser(String userId, String type, Integer page, Integer size);

    /**
     * 获取通知列表（管理端用，支持分页）
     */
    IPage<Notification> listNotificationsPage(Integer page, Integer pageSize, String type, String keyword, String status);

    /**
     * 获取用户未读通知数量
     */
    Long getUnreadCount(String userId);

    /**
     * 标记通知为已读
     */
    void markAsRead(String id, String userId);

    /**
     * 标记所有通知为已读
     */
    void markAllAsRead(String userId);
}
