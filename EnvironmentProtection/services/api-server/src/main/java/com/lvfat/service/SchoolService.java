package com.lvfat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lvfat.entity.Organization;
import com.lvfat.entity.School;

import java.util.List;
import java.util.Map;

/**
 * 学校服务接口
 */
public interface SchoolService {
    
    /**
     * 分页查询学校
     */
    IPage<School> pageSchools(Page<School> page, String schoolName, String schoolType, String educationBureauId);
    
    /**
     * 获取学校详情
     */
    School getById(String id);
    
    /**
     * 根据组织ID获取学校
     */
    School getByOrganizationId(String organizationId);
    
    /**
     * 根据学校代码获取学校
     */
    School getBySchoolCode(String schoolCode);
    
    /**
     * 获取教育局下的学校列表
     */
    List<School> getByEducationBureauId(String bureauId);
    
    /**
     * 获取学校列表
     */
    List<School> listSchools(String schoolType);
    
    /**
     * 创建学校
     */
    void create(School school);
    
    /**
     * 更新学校
     */
    void update(School school);
    
    /**
     * 删除学校
     */
    void delete(String id);
    
    /**
     * 获取学校的管辖链
     */
    List<Organization> getJurisdictionChain(String schoolOrgId);
    
    /**
     * 获取学校统计信息
     */
    Map<String, Object> getStatistics(String id);
    
    /**
     * 同步学校学生数到组织表
     */
    void syncStudentCountToOrganization(String schoolId);
}
