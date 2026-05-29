package com.lvfat.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.Employee;
import com.lvfat.entity.Organization;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 职员 Mapper
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
    
    @Select("SELECT * FROM employee WHERE organization_id = #{organizationId} AND deleted = 0 ORDER BY is_leader DESC, create_time")
    List<Employee> findByOrganizationId(@Param("organizationId") String organizationId);
    
    @Select("SELECT * FROM employee WHERE parent_employee_id = #{parentEmployeeId} AND deleted = 0 ORDER BY create_time")
    List<Employee> findByParentEmployeeId(@Param("parentEmployeeId") String parentEmployeeId);
    
    @Select("SELECT * FROM employee WHERE organization_id = #{organizationId} AND status = 'active' AND deleted = 0 ORDER BY is_leader DESC, create_time")
    List<Employee> findActiveByOrganizationId(@Param("organizationId") String organizationId);
    
    @Select("SELECT * FROM v_employee_with_org WHERE organization_id = #{organizationId} AND deleted = 0 ORDER BY is_leader DESC, create_time")
    List<Employee> findWithOrgByOrganizationId(@Param("organizationId") String organizationId);
    
    @Select("SELECT * FROM v_employee_with_org WHERE deleted = 0 ORDER BY organization_id, is_leader DESC, create_time")
    List<Employee> findAllWithOrg();
    
    @Select("SELECT COUNT(*) FROM employee WHERE organization_id = #{organizationId} AND status = 'active' AND deleted = 0")
    int countActiveByOrganizationId(@Param("organizationId") String organizationId);
    
    @Select("SELECT * FROM employee WHERE user_id = #{userId} AND deleted = 0 LIMIT 1")
    Employee findByUserId(@Param("userId") String userId);
}
