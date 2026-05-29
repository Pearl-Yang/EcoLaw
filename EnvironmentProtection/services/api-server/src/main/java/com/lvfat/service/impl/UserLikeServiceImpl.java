package com.lvfat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lvfat.entity.UserLike;
import com.lvfat.repository.UserLikeMapper;
import com.lvfat.service.UserLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 用户点赞服务实现
 */
@Service
@RequiredArgsConstructor
public class UserLikeServiceImpl extends ServiceImpl<UserLikeMapper, UserLike> implements UserLikeService {

    @Override
    public boolean isLiked(String userId, String targetType, String targetId) {
        QueryWrapper<UserLike> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("target_type", targetType)
                .eq("target_id", targetId);
        return count(wrapper) > 0;
    }

    @Override
    public void like(String userId, String targetType, String targetId) {
        if (!isLiked(userId, targetType, targetId)) {
            UserLike userLike = new UserLike();
            userLike.setUserId(userId);
            userLike.setTargetType(targetType);
            userLike.setTargetId(targetId);
            save(userLike);
        }
    }

    @Override
    public void unlike(String userId, String targetType, String targetId) {
        QueryWrapper<UserLike> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("target_type", targetType)
                .eq("target_id", targetId);
        remove(wrapper);
    }
}
