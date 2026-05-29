package com.lvfat.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.OrganizationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 组织类型 Mapper
 */
@Mapper
public interface OrganizationTypeMapper extends BaseMapper<OrganizationType> {
    
    @Select("SELECT * FROM organization_type WHERE deleted = 0 ORDER BY sort_order, create_time")
    List<OrganizationType> findAll();
    
    @Select("SELECT * FROM organization_type WHERE code = #{code} AND deleted = 0 LIMIT 1")
    OrganizationType findByCode(@Param("code") String code);
    
    @Select("SELECT * FROM organization_type WHERE status = '1' AND deleted = 0 ORDER BY sort_order, create_time")
    List<OrganizationType> findActive();
}
