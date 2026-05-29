package com.lvfat.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用户 Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    @Select("SELECT * FROM sys_user WHERE username = #{username} AND deleted = 0 LIMIT 1")
    User findByUsername(String username);
}