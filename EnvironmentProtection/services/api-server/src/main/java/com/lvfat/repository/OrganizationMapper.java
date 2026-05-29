package com.lvfat.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.Organization;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 组织架构 Mapper
 */
@Mapper
public interface OrganizationMapper extends BaseMapper<Organization> {
    
    @Select("SELECT * FROM sys_organization WHERE parent_id = #{parentId} ORDER BY level, create_time")
    List<Organization> findByParentId(String parentId);
    
    @Select("SELECT * FROM sys_organization WHERE level = #{level} ORDER BY create_time")
    List<Organization> findByLevel(Integer level);
}
