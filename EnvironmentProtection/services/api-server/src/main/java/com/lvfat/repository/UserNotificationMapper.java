package com.lvfat.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.UserNotification;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户通知关联 Mapper
 */
@Mapper
public interface UserNotificationMapper extends BaseMapper<UserNotification> {
}
