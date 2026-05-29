package com.lvfat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lvfat.entity.UserLike;

/**
 * 用户点赞服务接口
 */
public interface UserLikeService extends IService<UserLike> {
    /**
     * 检查用户是否已点赞
     */
    boolean isLiked(String userId, String targetType, String targetId);

    /**
     * 点赞
     */
    void like(String userId, String targetType, String targetId);

    /**
     * 取消点赞
     */
    void unlike(String userId, String targetType, String targetId);
}
