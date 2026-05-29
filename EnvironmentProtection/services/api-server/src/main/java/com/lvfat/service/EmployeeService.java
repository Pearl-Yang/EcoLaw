package com.lvfat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lvfat.entity.Employee;

import java.util.List;

/**
 * 职员管理服务接口
 */
public interface EmployeeService extends IService<Employee> {
    
    /**
     * 获取职员列表
     *
     * @param organizationType 组织类型: government / enterprise / platform 等，与组织档案 type 字段一致；空则不限
     */
    List<Employee> listEmployees(String organizationId, String keyword, String position, String status, String organizationType);
    
    /**
     * 获取职员详情(含组织和上级信息)
     */
    Employee getEmployeeDetail(String id);
    
    /**
     * 获取职员树形结构(包含下属)
     */
    Employee getEmployeeTree(String employeeId);
    
    /**
     * 获取组织下所有职员
     */
    List<Employee> getOrganizationEmployees(String organizationId, boolean includeChildren);
    
    /**
     * 获取可下发任务的目标列表(下级组织和职员)
     */
    List<Object> getDispatchableTargets(String organizationId);
    
    /**
     * 批量导入职员
     */
    int batchImportEmployees(List<Employee> employees, String organizationId);
    
    /**
     * 调岗
     */
    boolean transferEmployee(String employeeId, String targetOrganizationId, String newPosition);
    
    /**
     * 获取汇报链
     */
    List<Employee> getReportingChain(String employeeId);
    
    /**
     * 保存职员
     */
    void saveEmployee(Employee employee);
    
    /**
     * 更新职员
     */
    void updateEmployee(Employee employee);
    
    /**
     * 根据用户ID获取职员
     */
    Employee getByUserId(String userId);
    
    /**
     * 获取职员数量统计
     */
    int countByOrganizationId(String organizationId);
}
