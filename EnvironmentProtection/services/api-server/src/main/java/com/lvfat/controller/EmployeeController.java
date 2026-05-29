package com.lvfat.controller;

import com.lvfat.common.result.Result;
import com.lvfat.entity.Employee;
import com.lvfat.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "职员管理")
@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {
    
    private final EmployeeService employeeService;
    
    @Operation(summary = "获取职员列表")
    @GetMapping
    public Result<List<Employee>> listEmployees(
            @Parameter(description = "组织ID") @RequestParam(required = false) String organizationId,
            @Parameter(description = "关键词(姓名/工号/手机号)") @RequestParam(required = false) String keyword,
            @Parameter(description = "职位") @RequestParam(required = false) String position,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "组织类型: government-政府 enterprise-企业 platform-平台") @RequestParam(required = false) String organizationType
    ) {
        return Result.success(employeeService.listEmployees(organizationId, keyword, position, status, organizationType));
    }
    
    @Operation(summary = "获取职员详情")
    @GetMapping("/{id}")
    public Result<Employee> getEmployee(@PathVariable String id) {
        return Result.success(employeeService.getEmployeeDetail(id));
    }
    
    @Operation(summary = "获取职员树(包含下属)")
    @GetMapping("/{id}/tree")
    public Result<Employee> getEmployeeTree(@PathVariable String id) {
        return Result.success(employeeService.getEmployeeTree(id));
    }
    
    @Operation(summary = "获取组织所有职员")
    @GetMapping("/organization/{organizationId}")
    public Result<List<Employee>> getOrganizationEmployees(
            @PathVariable String organizationId,
            @Parameter(description = "包含下级组织") @RequestParam(defaultValue = "false") boolean includeChildren
    ) {
        return Result.success(employeeService.getOrganizationEmployees(organizationId, includeChildren));
    }
    
    @Operation(summary = "获取可下发目标列表")
    @GetMapping("/dispatch-targets")
    public Result<List<Object>> getDispatchableTargets(@RequestParam String organizationId) {
        return Result.success(employeeService.getDispatchableTargets(organizationId));
    }
    
    @Operation(summary = "创建职员")
    @PostMapping
    public Result<Void> createEmployee(@RequestBody Employee employee) {
        employeeService.saveEmployee(employee);
        return Result.success("职员创建成功");
    }
    
    @Operation(summary = "批量创建职员")
    @PostMapping("/batch")
    public Result<Integer> batchCreateEmployees(
            @RequestParam String organizationId,
            @RequestBody List<Employee> employees
    ) {
        int count = employeeService.batchImportEmployees(employees, organizationId);
        return Result.success("成功导入" + count + "条职员记录", count);
    }
    
    @Operation(summary = "更新职员")
    @PutMapping("/{id}")
    public Result<Void> updateEmployee(@PathVariable String id, @RequestBody Employee employee) {
        employee.setId(id);
        employeeService.updateEmployee(employee);
        return Result.success("职员更新成功");
    }
    
    @Operation(summary = "删除职员")
    @DeleteMapping("/{id}")
    public Result<Void> deleteEmployee(@PathVariable String id) {
        employeeService.removeById(id);
        return Result.success("职员删除成功");
    }
    
    @Operation(summary = "调岗")
    @PostMapping("/{id}/transfer")
    public Result<Void> transferEmployee(
            @PathVariable String id,
            @RequestParam String targetOrganizationId,
            @RequestParam(required = false) String newPosition
    ) {
        employeeService.transferEmployee(id, targetOrganizationId, newPosition);
        return Result.success("调岗成功");
    }
    
    @Operation(summary = "获取汇报链")
    @GetMapping("/{id}/reporting-chain")
    public Result<List<Employee>> getReportingChain(@PathVariable String id) {
        return Result.success(employeeService.getReportingChain(id));
    }
    
    @Operation(summary = "根据用户ID获取职员")
    @GetMapping("/user/{userId}")
    public Result<Employee> getByUserId(@PathVariable String userId) {
        return Result.success(employeeService.getByUserId(userId));
    }
    
    @Operation(summary = "获取职员数量")
    @GetMapping("/count/{organizationId}")
    public Result<Integer> getEmployeeCount(@PathVariable String organizationId) {
        return Result.success(employeeService.countByOrganizationId(organizationId));
    }
}
