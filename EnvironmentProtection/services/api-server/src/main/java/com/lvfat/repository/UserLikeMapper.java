package com.lvfat.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.UserLike;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户点赞 Mapper
 */
@Mapper
public interface UserLikeMapper extends BaseMapper<UserLike> {
}
