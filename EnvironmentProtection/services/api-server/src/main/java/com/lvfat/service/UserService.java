package com.lvfat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lvfat.entity.User;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    
    /**
     * 获取当前登录用户
     */
    User getCurrentUser();
    
    /**
     * 查询用户列表
     */
    List<User> listUsers(String role, String organizationId);
    
    /**
     * 重置密码
     */
    void resetPassword(String userId);
    
    /**
     * 修改密码
     */
    void changePassword(String oldPassword, String newPassword);
}