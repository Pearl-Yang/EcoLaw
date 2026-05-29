package com.lvfat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lvfat.entity.Employee;
import com.lvfat.entity.Organization;
import com.lvfat.entity.User;
import com.lvfat.repository.EmployeeMapper;
import com.lvfat.repository.OrganizationMapper;
import com.lvfat.repository.UserMapper;
import com.lvfat.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    private final EmployeeMapper employeeMapper;
    private final OrganizationMapper organizationMapper;
    private final UserMapper userMapper;

    @Override
    public List<Employee> listEmployees(String organizationId, String keyword, String position, String status, String organizationType) {
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(organizationId)) {
            wrapper.eq(Employee::getOrganizationId, organizationId);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Employee::getName, keyword)
                    .or().like(Employee::getEmployeeNo, keyword)
                    .or().like(Employee::getPhone, keyword));
        }
        if (StringUtils.hasText(position)) {
            wrapper.eq(Employee::getPosition, position);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Employee::getStatus, status);
        }
        wrapper.orderByDesc(Employee::getIsLeader).orderByAsc(Employee::getCreateTime);
        
        List<Employee> employees = employeeMapper.selectList(wrapper);
        // 填充组织信息
        fillOrganizationInfo(employees);
        if (StringUtils.hasText(organizationType)) {
            final String ot = organizationType.trim();
            employees = employees.stream()
                    .filter(e -> ot.equalsIgnoreCase(e.getOrganizationType()))
                    .collect(Collectors.toList());
        }
        return employees;
    }

    @Override
    public Employee getEmployeeDetail(String id) {
        Employee employee = employeeMapper.selectById(id);
        if (employee != null) {
            fillEmployeeDetails(employee);
        }
        return employee;
    }

    @Override
    public Employee getEmployeeTree(String employeeId) {
        Employee root = employeeMapper.selectById(employeeId);
        if (root == null) {
            return null;
        }
        fillEmployeeDetails(root);
        // 递归获取下属
        List<Employee> subordinates = getSubordinates(employeeId);
        root.setSubordinates(subordinates);
        return root;
    }

    private List<Employee> getSubordinates(String parentId) {
        List<Employee> children = employeeMapper.findByParentEmployeeId(parentId);
        for (Employee child : children) {
            fillEmployeeDetails(child);
            List<Employee> subSubordinates = getSubordinates(child.getId());
            child.setSubordinates(subSubordinates);
        }
        return children;
    }

    @Override
    public List<Employee> getOrganizationEmployees(String organizationId, boolean includeChildren) {
        List<Employee> result = new ArrayList<>();
        
        if (includeChildren) {
            // 获取所有下级组织ID
            List<String> orgIds = getChildOrganizationIds(organizationId);
            LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(Employee::getOrganizationId, orgIds);
            wrapper.eq(Employee::getStatus, "active");
            wrapper.orderByDesc(Employee::getIsLeader).orderByAsc(Employee::getCreateTime);
            result = employeeMapper.selectList(wrapper);
        } else {
            result = employeeMapper.findActiveByOrganizationId(organizationId);
        }
        
        fillOrganizationInfo(result);
        return result;
    }

    private List<String> getChildOrganizationIds(String parentId) {
        List<String> ids = new ArrayList<>();
        ids.add(parentId);
        List<Organization> children = getChildOrganizations(parentId);
        for (Organization child : children) {
            ids.add(child.getId());
            ids.addAll(getChildOrganizationIds(child.getId()));
        }
        return ids;
    }

    private List<Organization> getChildOrganizations(String parentId) {
        return organizationMapper.findByParentId(parentId);
    }

    @Override
    public List<Object> getDispatchableTargets(String organizationId) {
        List<Object> targets = new ArrayList<>();
        
        // 获取下级组织
        List<Organization> childOrgs = getChildOrganizations(organizationId);
        for (Organization org : childOrgs) {
            Map<String, Object> target = new HashMap<>();
            target.put("type", "organization");
            target.put("id", org.getId());
            target.put("name", org.getName());
            target.put("category", org.getCategory());
            target.put("staffCount", org.getStaffCount());
            targets.add(target);
        }
        
        // 获取当前组织的职员
        List<Employee> employees = employeeMapper.findActiveByOrganizationId(organizationId);
        for (Employee emp : employees) {
            Map<String, Object> target = new HashMap<>();
            target.put("type", "employee");
            target.put("id", emp.getId());
            target.put("name", emp.getName());
            target.put("position", emp.getPosition());
            targets.add(target);
        }
        
        return targets;
    }

    @Override
    public int batchImportEmployees(List<Employee> employees, String organizationId) {
        int count = 0;
        for (Employee employee : employees) {
            employee.setOrganizationId(organizationId);
            if (employee.getStatus() == null) {
                employee.setStatus("active");
            }
            employeeMapper.insert(employee);
            count++;
        }
        return count;
    }

    @Override
    public boolean transferEmployee(String employeeId, String targetOrganizationId, String newPosition) {
        Employee employee = employeeMapper.selectById(employeeId);
        if (employee == null) {
            return false;
        }
        employee.setOrganizationId(targetOrganizationId);
        employee.setStatus("transfer");
        employeeMapper.updateById(employee);
        
        // 创建新记录
        Employee newEmployee = new Employee();
        newEmployee.setId(UUID.randomUUID().toString());
        newEmployee.setUserId(employee.getUserId());
        newEmployee.setOrganizationId(targetOrganizationId);
        newEmployee.setParentEmployeeId(employee.getParentEmployeeId());
        newEmployee.setEmployeeNo(employee.getEmployeeNo());
        newEmployee.setName(employee.getName());
        newEmployee.setGender(employee.getGender());
        newEmployee.setPhone(employee.getPhone());
        newEmployee.setIdCard(employee.getIdCard());
        newEmployee.setPosition(newPosition != null ? newPosition : employee.getPosition());
        newEmployee.setPositionLevel(employee.getPositionLevel());
        newEmployee.setStatus("active");
        newEmployee.setIsLeader(0);
        employeeMapper.insert(newEmployee);
        
        return true;
    }

    @Override
    public List<Employee> getReportingChain(String employeeId) {
        List<Employee> chain = new ArrayList<>();
        Employee current = employeeMapper.selectById(employeeId);
        
        while (current != null && current.getParentEmployeeId() != null) {
            current = employeeMapper.selectById(current.getParentEmployeeId());
            if (current != null) {
                chain.add(current);
            }
        }
        
        return chain;
    }

    @Override
    public void saveEmployee(Employee employee) {
        if (employee.getStatus() == null) {
            employee.setStatus("active");
        }
        employeeMapper.insert(employee);
    }

    @Override
    public void updateEmployee(Employee employee) {
        employeeMapper.updateById(employee);
    }

    @Override
    public Employee getByUserId(String userId) {
        return employeeMapper.findByUserId(userId);
    }

    @Override
    public int countByOrganizationId(String organizationId) {
        return employeeMapper.countActiveByOrganizationId(organizationId);
    }

    private void fillOrganizationInfo(List<Employee> employees) {
        Set<String> orgIds = employees.stream()
                .map(Employee::getOrganizationId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        
        Map<String, Organization> orgMap = new HashMap<>();
        for (String orgId : orgIds) {
            Organization org = organizationMapper.selectById(orgId);
            if (org != null) {
                orgMap.put(orgId, org);
            }
        }
        
        for (Employee employee : employees) {
            Organization org = orgMap.get(employee.getOrganizationId());
            if (org != null) {
                employee.setOrganizationName(org.getName());
                employee.setOrganizationType(org.getType());
            }
        }
    }

    private void fillEmployeeDetails(Employee employee) {
        // 填充组织信息
        if (employee.getOrganizationId() != null) {
            Organization org = organizationMapper.selectById(employee.getOrganizationId());
            if (org != null) {
                employee.setOrganizationName(org.getName());
                employee.setOrganizationType(org.getType());
            }
        }
        
        // 填充上级信息
        if (employee.getParentEmployeeId() != null) {
            Employee parent = employeeMapper.selectById(employee.getParentEmployeeId());
            if (parent != null) {
                employee.setParentEmployeeName(parent.getName());
                employee.setParentPosition(parent.getPosition());
            }
        }
        
        // 填充用户信息
        if (employee.getUserId() != null) {
            User user = userMapper.selectById(employee.getUserId());
            if (user != null) {
                employee.setUser(user);
            }
        }
    }
}
