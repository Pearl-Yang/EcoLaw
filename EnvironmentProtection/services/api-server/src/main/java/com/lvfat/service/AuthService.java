package com.lvfat.service;

import com.lvfat.dto.LoginDTO;
import com.lvfat.entity.User;
import com.lvfat.vo.LoginVO;
import com.lvfat.vo.UserInfoVO;

public interface AuthService {
    LoginVO login(LoginDTO loginDTO);
    UserInfoVO getUserInfo();
    void logout();
    String refreshToken();
}
