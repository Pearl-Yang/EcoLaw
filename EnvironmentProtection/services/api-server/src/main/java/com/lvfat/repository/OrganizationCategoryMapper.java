package com.lvfat.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.OrganizationCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 组织分类 Mapper
 */
@Mapper
public interface OrganizationCategoryMapper extends BaseMapper<OrganizationCategory> {
    
    @Select("SELECT * FROM organization_category WHERE deleted = 0 ORDER BY type_code, level, sort_order")
    List<OrganizationCategory> findAll();
    
    @Select("SELECT * FROM organization_category WHERE type_code = #{typeCode} AND deleted = 0 ORDER BY level, sort_order")
    List<OrganizationCategory> findByTypeCode(@Param("typeCode") String typeCode);
    
    @Select("SELECT * FROM organization_category WHERE type_code = #{typeCode} AND status = '1' AND deleted = 0 ORDER BY level, sort_order")
    List<OrganizationCategory> findActiveByTypeCode(@Param("typeCode") String typeCode);
    
    @Select("SELECT * FROM organization_category WHERE parent_category_code = #{parentCategoryCode} AND deleted = 0 ORDER BY sort_order")
    List<OrganizationCategory> findByParentCategoryCode(@Param("parentCategoryCode") String parentCategoryCode);
    
    @Select("SELECT * FROM organization_category WHERE type_code = #{typeCode} AND code = #{code} AND deleted = 0 LIMIT 1")
    OrganizationCategory findByTypeCodeAndCode(@Param("typeCode") String typeCode, @Param("code") String code);
}
