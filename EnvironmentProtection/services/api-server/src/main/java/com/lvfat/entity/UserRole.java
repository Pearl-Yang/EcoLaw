package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户角色关联实体
 */
@Data
@TableName("sys_user_role")
public class UserRole {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    private String userId;
    
    private String roleId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
