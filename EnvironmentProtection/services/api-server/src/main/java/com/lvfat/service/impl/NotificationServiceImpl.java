package com.lvfat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lvfat.entity.Notification;
import com.lvfat.entity.UserNotification;
import com.lvfat.repository.NotificationMapper;
import com.lvfat.repository.UserNotificationMapper;
import com.lvfat.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知服务实现
 */
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    private final UserNotificationMapper userNotificationMapper;

    @Override
    public List<Notification> listByUser(String userId, String type, Integer page, Integer size) {
        QueryWrapper<Notification> wrapper = new QueryWrapper<>();
        wrapper.and(w -> w
                .eq("target_type", "all")
                .or()
                .apply("FIND_IN_SET('" + userId + "', target_value)")
                .or()
                .eq("user_id", userId)
        );
        wrapper.and(w -> w
                .isNull("expire_time")
                .or()
                .gt("expire_time", LocalDateTime.now())
        );
        wrapper.eq("status", "published");
        if (StringUtils.hasText(type)) {
            wrapper.eq("type", type);
        }
        wrapper.orderByDesc("create_time");

        Page<Notification> pageObj = new Page<>(page != null ? page : 1, size != null ? size : 20);
        Page<Notification> result = page(pageObj, wrapper);
        return result.getRecords();
    }

    @Override
    public IPage<Notification> listNotificationsPage(Integer page, Integer pageSize, String type, String keyword, String status) {
        QueryWrapper<Notification> wrapper = new QueryWrapper<>();
        // 如果没有指定状态，返回所有状态
        if (StringUtils.hasText(status)) {
            wrapper.eq("status", status);
        }
        if (StringUtils.hasText(type)) {
            wrapper.eq("type", type);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like("title", keyword).or().like("content", keyword));
        }
        wrapper.orderByDesc("create_time");
        return page(new Page<>(page != null ? page : 1, pageSize != null ? pageSize : 10), wrapper);
    }

    @Override
    public Long getUnreadCount(String userId) {
        QueryWrapper<Notification> wrapper = new QueryWrapper<>();
        wrapper.and(w -> w
                .eq("target_type", "all")
                .or()
                .apply("FIND_IN_SET('" + userId + "', target_value)")
                .or()
                .eq("user_id", userId)
        );
        wrapper.and(w -> w
                .isNull("expire_time")
                .or()
                .gt("expire_time", LocalDateTime.now())
        );
        wrapper.eq("status", "published");
        wrapper.eq("is_read", 0);
        return count(wrapper);
    }

    @Override
    public void markAsRead(String id, String userId) {
        Notification notification = getById(id);
        if (notification != null) {
            notification.setIsRead(1);
            notification.setReadTime(LocalDateTime.now());
            updateById(notification);
        }
    }

    @Override
    public void markAllAsRead(String userId) {
        QueryWrapper<Notification> wrapper = new QueryWrapper<>();
        wrapper.and(w -> w
                .eq("target_type", "all")
                .or()
                .apply("FIND_IN_SET('" + userId + "', target_value)")
                .or()
                .eq("user_id", userId)
        );
        wrapper.eq("is_read", 0);
        Notification notification = new Notification();
        notification.setIsRead(1);
        notification.setReadTime(LocalDateTime.now());
        update(notification, wrapper);
    }
}
