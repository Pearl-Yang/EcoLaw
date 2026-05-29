package com.lvfat.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoVO {
    private String id;
    private String username;
    private String nickname;
    private String avatar;
    private String role;
    private String roleName;
    private String organizationId;
    private String organizationName;
    private List<String> permissions;
}
