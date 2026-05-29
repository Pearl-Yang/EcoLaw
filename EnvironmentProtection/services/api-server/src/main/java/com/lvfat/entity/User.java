package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@TableName("sys_user")
public class User {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    private String username;
    
    private String password;
    
    private String nickname;
    
    private String avatar;
    
    private String phone;
    
    private String email;
    
    /**
     * 角色: super_admin, government, enterprise, law_firm, user
     */
    private String role;
    
    private String organizationId;
    
    private String status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}