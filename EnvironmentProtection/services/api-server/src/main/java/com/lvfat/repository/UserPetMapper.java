package com.lvfat.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.UserPet;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户宠物 Mapper
 */
@Mapper
public interface UserPetMapper extends BaseMapper<UserPet> {
}
