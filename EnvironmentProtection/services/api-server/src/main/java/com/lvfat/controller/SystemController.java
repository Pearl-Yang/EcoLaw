package com.lvfat.controller;

import com.lvfat.common.result.Result;
import com.lvfat.entity.Organization;
import com.lvfat.entity.User;
import com.lvfat.entity.UserRole;
import com.lvfat.entity.Role;
import com.lvfat.service.OrganizationService;
import com.lvfat.service.UserService;
import com.lvfat.repository.RoleMapper;
import com.lvfat.repository.UserRoleMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统管理控制器 - 用户/组织/角色管理
 */
@Tag(name = "系统管理")
@RestController
@RequestMapping("/system")
@RequiredArgsConstructor
public class SystemController {
    
    private final UserService userService;
    private final OrganizationService organizationService;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;
    
    // ==================== 用户管理 ====================
    
    @Operation(summary = "获取用户列表")
    @GetMapping("/users")
    public Result<List<User>> listUsers(
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "角色") @RequestParam(required = false) String role,
            @Parameter(description = "组织ID") @RequestParam(required = false) String organizationId,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "页码") @RequestParam(required = false) Integer page,
            @Parameter(description = "每页数量") @RequestParam(required = false) Integer pageSize
    ) {
        List<User> users = userService.listUsers(role, organizationId);
        return Result.success(users);
    }
    
    @Operation(summary = "获取用户详情")
    @GetMapping("/users/{id}")
    public Result<User> getUser(@PathVariable String id) {
        return Result.success(userService.getById(id));
    }
    
    @Operation(summary = "创建用户")
    @PostMapping("/users")
    public Result<Void> createUser(@RequestBody User user) {
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode("123456"));
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (user.getStatus() == null) {
            user.setStatus("1");
        }
        userService.save(user);
        return Result.success("用户创建成功");
    }
    
    @Operation(summary = "更新用户")
    @PutMapping("/users/{id}")
    public Result<Void> updateUser(@PathVariable String id, @RequestBody User user) {
        user.setId(id);
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userService.updateById(user);
        return Result.success("用户更新成功");
    }
    
    @Operation(summary = "删除用户")
    @DeleteMapping("/users/{id}")
    public Result<Void> deleteUser(@PathVariable String id) {
        userService.removeById(id);
        return Result.success("用户删除成功");
    }
    
    @Operation(summary = "重置密码")
    @PostMapping("/users/{id}/reset-password")
    public Result<Void> resetPassword(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "123456") String password
    ) {
        userService.resetPassword(id);
        return Result.success("密码已重置为: " + password);
    }
    
    @Operation(summary = "修改用户状态")
    @PostMapping("/users/{id}/status")
    public Result<Void> changeUserStatus(
            @PathVariable String id,
            @RequestParam Integer status
    ) {
        User user = userService.getById(id);
        if (user != null) {
            user.setStatus(status == 1 ? "1" : "0");
            userService.updateById(user);
        }
        return Result.success("状态修改成功");
    }
    
    // ==================== 角色管理 ====================
    
    @Operation(summary = "获取角色列表")
    @GetMapping("/roles")
    public Result<List<Role>> listRoles() {
        return Result.success(roleMapper.selectList(null));
    }
    
    @Operation(summary = "获取角色详情")
    @GetMapping("/roles/{id}")
    public Result<Role> getRole(@PathVariable String id) {
        return Result.success(roleMapper.selectById(id));
    }
    
    @Operation(summary = "创建角色")
    @PostMapping("/roles")
    public Result<Void> createRole(@RequestBody Role role) {
        if (role.getStatus() == null) {
            role.setStatus("1");
        }
        roleMapper.insert(role);
        return Result.success("角色创建成功");
    }
    
    @Operation(summary = "更新角色")
    @PutMapping("/roles/{id}")
    public Result<Void> updateRole(@PathVariable String id, @RequestBody Role role) {
        role.setId(id);
        roleMapper.updateById(role);
        return Result.success("角色更新成功");
    }
    
    @Operation(summary = "删除角色")
    @DeleteMapping("/roles/{id}")
    public Result<Void> deleteRole(@PathVariable String id) {
        roleMapper.deleteById(id);
        return Result.success("角色删除成功");
    }
    
    @Operation(summary = "分配用户角色")
    @PostMapping("/users/{userId}/roles")
    public Result<Void> assignRoles(
            @PathVariable String userId,
            @RequestBody List<String> roleIds
    ) {
        // 先删除原有角色
        userRoleMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<UserRole>()
                .eq("user_id", userId));
        
        // 添加新角色
        for (String roleId : roleIds) {
            UserRole ur = new UserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            userRoleMapper.insert(ur);
        }
        return Result.success("角色分配成功");
    }
    
    // ==================== 组织管理 ====================
    
    @Operation(summary = "获取组织树")
    @GetMapping("/organizations/tree")
    public Result<List<Organization>> getOrganizationTree() {
        return Result.success(organizationService.getTree());
    }
    
    @Operation(summary = "获取组织列表")
    @GetMapping("/organizations")
    public Result<List<Organization>> listOrganizations(
            @Parameter(description = "父级ID") @RequestParam(required = false) String parentId,
            @Parameter(description = "层级") @RequestParam(required = false) Integer level
    ) {
        return Result.success(organizationService.getByParentId(parentId));
    }
    
    @Operation(summary = "获取组织详情")
    @GetMapping("/organizations/{id}")
    public Result<Organization> getOrganization(@PathVariable String id) {
        return Result.success(organizationService.getById(id));
    }
    
    @Operation(summary = "创建组织")
    @PostMapping("/organizations")
    public Result<Void> createOrganization(@RequestBody Organization organization) {
        organizationService.save(organization);
        return Result.success("组织创建成功");
    }
    
    @Operation(summary = "更新组织")
    @PutMapping("/organizations/{id}")
    public Result<Void> updateOrganization(@PathVariable String id, @RequestBody Organization organization) {
        organization.setId(id);
        organizationService.updateById(organization);
        return Result.success("组织更新成功");
    }
    
    @Operation(summary = "删除组织")
    @DeleteMapping("/organizations/{id}")
    public Result<Void> deleteOrganization(@PathVariable String id) {
        organizationService.removeById(id);
        return Result.success("组织删除成功");
    }
    
    @Operation(summary = "获取组织用户")
    @GetMapping("/organizations/{id}/users")
    public Result<List<User>> getOrganizationUsers(@PathVariable String id) {
        Organization org = organizationService.getWithUsers(id);
        return Result.success(org != null && org.getUsers() != null ? org.getUsers() : List.of());
    }
}
