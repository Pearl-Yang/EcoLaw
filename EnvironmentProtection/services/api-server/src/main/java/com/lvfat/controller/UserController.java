package com.lvfat.controller;

import com.lvfat.common.result.Result;
import com.lvfat.entity.User;
import com.lvfat.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/me")
    public Result<User> getCurrentUser() {
        return Result.success(userService.getCurrentUser());
    }

    @Operation(summary = "获取用户列表")
    @GetMapping
    public Result<List<User>> listUsers(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String role,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String organizationId
    ) {
        return Result.success(userService.listUsers(role, organizationId));
    }

    @Operation(summary = "获取用户详情")
    @GetMapping("/{id}")
    public Result<User> getUser(@PathVariable String id) {
        return Result.success(userService.getById(id));
    }

    @Operation(summary = "创建用户")
    @PostMapping
    public Result<Void> createUser(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode("123456"));
        userService.save(user);
        return Result.success("用户创建成功");
    }

    @Operation(summary = "更新用户")
    @PutMapping("/{id}")
    public Result<Void> updateUser(@PathVariable String id, @RequestBody User user) {
        user.setId(id);
        userService.updateById(user);
        return Result.success("用户更新成功");
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable String id) {
        userService.removeById(id);
        return Result.success("用户删除成功");
    }

    @Operation(summary = "重置密码")
    @PostMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable String id) {
        userService.resetPassword(id);
        return Result.success("密码已重置为123456");
    }

    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<Void> changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword
    ) {
        userService.changePassword(oldPassword, newPassword);
        return Result.success("密码修改成功");
    }
}