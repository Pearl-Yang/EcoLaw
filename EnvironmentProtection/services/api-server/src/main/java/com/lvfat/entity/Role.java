package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色实体
 */
@Data
@TableName("sys_role")
public class Role {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    private String code;
    
    private String name;
    
    private String description;
    
    private Integer sort;
    
    private String status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
