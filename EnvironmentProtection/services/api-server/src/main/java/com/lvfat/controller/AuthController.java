package com.lvfat.controller;

import com.lvfat.common.result.Result;
import com.lvfat.dto.LoginDTO;
import com.lvfat.service.AuthService;
import com.lvfat.vo.LoginVO;
import com.lvfat.vo.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginVO result = authService.login(loginDTO);
        return Result.success(result);
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/userinfo")
    public Result<UserInfoVO> getUserInfo() {
        UserInfoVO userInfo = authService.getUserInfo();
        return Result.success(userInfo);
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }

    @Operation(summary = "刷新Token")
    @PostMapping("/refresh")
    public Result<Map<String, String>> refreshToken() {
        String token = authService.refreshToken();
        Map<String, String> result = new HashMap<>();
        result.put("token", token);
        return Result.success(result);
    }

    @Operation(summary = "获取验证码")
    @GetMapping("/captcha")
    public Result<Map<String, String>> getCaptcha() {
        // 生成简单的验证码ID和图片
        String captchaId = String.valueOf(System.currentTimeMillis());
        // 实际项目中应该生成真实的验证码图片
        Map<String, String> result = new HashMap<>();
        result.put("captchaId", captchaId);
        result.put("captchaImage", "data:image/svg+xml;base64,..."); // 占位符
        return Result.success(result);
    }
}
