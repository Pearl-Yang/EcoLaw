package com.lvfat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lvfat.common.exception.BusinessException;
import com.lvfat.entity.User;
import com.lvfat.repository.UserMapper;
import com.lvfat.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;

    @Override
    public User getCurrentUser() {
        String username = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return getOne(new QueryWrapper<User>().eq("username", username));
    }

    @Override
    public java.util.List<User> listUsers(String role, String organizationId) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (role != null) {
            wrapper.eq("role", role);
        }
        if (organizationId != null) {
            wrapper.eq("organization_id", organizationId);
        }
        return list(wrapper);
    }

    @Override
    public void resetPassword(String userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(passwordEncoder.encode("123456"));
        updateById(user);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        User currentUser = getCurrentUser();
        if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        currentUser.setPassword(passwordEncoder.encode(newPassword));
        updateById(currentUser);
    }
}