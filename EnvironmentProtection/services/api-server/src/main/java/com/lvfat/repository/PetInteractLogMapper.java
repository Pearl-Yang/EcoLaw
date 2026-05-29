package com.lvfat.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.PetInteractLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 宠物互动日志 Mapper
 */
@Mapper
public interface PetInteractLogMapper extends BaseMapper<PetInteractLog> {
}
