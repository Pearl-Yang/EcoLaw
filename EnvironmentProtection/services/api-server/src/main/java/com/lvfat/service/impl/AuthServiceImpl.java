package com.lvfat.service.impl;

import com.lvfat.dto.LoginDTO;
import com.lvfat.entity.Organization;
import com.lvfat.entity.User;
import com.lvfat.repository.OrganizationMapper;
import com.lvfat.repository.UserMapper;
import com.lvfat.security.JwtTokenUtil;
import com.lvfat.service.AuthService;
import com.lvfat.vo.LoginVO;
import com.lvfat.vo.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrganizationMapper organizationMapper;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 认证
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 获取用户信息
        User user = userMapper.findByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 生成Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("role", user.getRole());
        claims.put("organizationId", user.getOrganizationId());
        String token = jwtTokenUtil.generateToken(user.getUsername(), claims);

        // 构建返回结果
        Organization org = organizationMapper.selectById(user.getOrganizationId());
        UserInfoVO userInfo = UserInfoVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .role(user.getRole())
                .roleName(getRoleName(user.getRole()))
                .organizationId(user.getOrganizationId())
                .organizationName(org != null ? org.getName() : "")
                .permissions(getPermissionsByRole(user.getRole()))
                .build();

        return LoginVO.builder()
                .token(token)
                .userInfo(userInfo)
                .build();
    }

    @Override
    public UserInfoVO getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("用户未登录");
        }

        String username = authentication.getName();
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        Organization org = organizationMapper.selectById(user.getOrganizationId());

        return UserInfoVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .role(user.getRole())
                .roleName(getRoleName(user.getRole()))
                .organizationId(user.getOrganizationId())
                .organizationName(org != null ? org.getName() : "")
                .permissions(getPermissionsByRole(user.getRole()))
                .build();
    }

    @Override
    public void logout() {
        SecurityContextHolder.clearContext();
    }

    @Override
    public String refreshToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("用户未登录");
        }

        String username = authentication.getName();
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("role", user.getRole());
        claims.put("organizationId", user.getOrganizationId());

        return jwtTokenUtil.generateToken(user.getUsername(), claims);
    }

    private String getRoleName(String role) {
        return switch (role) {
            case "super_admin" -> "超级管理员";
            case "government" -> "政府监管员";
            case "enterprise" -> "企业管理员";
            case "law_firm" -> "律所管理员";
            case "user" -> "普通用户";
            default -> role;
        };
    }

    private List<String> getPermissionsByRole(String role) {
        return switch (role) {
            case "super_admin" -> List.of("*");
            case "government" -> List.of("task:view", "task:create", "task:dispatch", "training:view", 
                    "report:view", "report:handle", "dashboard:view", "user:view");
            case "enterprise" -> List.of("training:view", "training:create", "user:view", "user:manage",
                    "report:view", "dashboard:view");
            case "law_firm" -> List.of("case:view", "case:create", "case:manage", "report:view",
                    "dashboard:view", "user:view");
            case "user" -> List.of("task:view", "training:view", "law:view", "report:create");
            default -> List.of();
        };
    }
}
