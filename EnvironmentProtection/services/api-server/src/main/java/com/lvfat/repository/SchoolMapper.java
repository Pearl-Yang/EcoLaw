package com.lvfat.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lvfat.entity.Organization;
import com.lvfat.entity.School;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 学校 Mapper
 */
@Mapper
public interface SchoolMapper extends BaseMapper<School> {
    
    /**
     * 根据教育局ID获取学校列表
     */
    @Select("SELECT * FROM school WHERE education_bureau_id = #{bureauId} AND deleted = 0 ORDER BY create_time DESC")
    List<School> findByEducationBureauId(@Param("bureauId") String bureauId);
    
    /**
     * 根据组织ID获取学校
     */
    @Select("SELECT * FROM school WHERE organization_id = #{organizationId} AND deleted = 0")
    School findByOrganizationId(@Param("organizationId") String organizationId);
    
    /**
     * 根据学校代码获取学校
     */
    @Select("SELECT * FROM school WHERE school_code = #{schoolCode} AND deleted = 0")
    School findBySchoolCode(@Param("schoolCode") String schoolCode);
    
    /**
     * 根据学校类型获取学校列表
     */
    @Select("SELECT * FROM school WHERE school_type = #{schoolType} AND deleted = 0 ORDER BY create_time DESC")
    List<School> findBySchoolType(@Param("schoolType") String schoolType);
    
    /**
     * 分页查询学校
     */
    IPage<School> selectSchoolPage(Page<School> page, @Param("schoolName") String schoolName, 
                                   @Param("schoolType") String schoolType, 
                                   @Param("educationBureauId") String educationBureauId);
    
    /**
     * 获取学校的完整管辖链(从学校到省级教育局)
     */
    @Select("""
        SELECT o.* FROM sys_organization o 
        WHERE o.id IN (
            SELECT id FROM sys_organization 
            WHERE parent_chain LIKE CONCAT('%', (
                SELECT parent_chain FROM sys_organization 
                WHERE id = #{schoolOrgId}
            ), '%')
        ) AND deleted = 0
        ORDER BY level
    """)
    List<Organization> getJurisdictionChain(@Param("schoolOrgId") String schoolOrgId);
}
