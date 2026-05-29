package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 组织类型配置实体
 */
@Data
@TableName("organization_type")
public class OrganizationType {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 类型编码: government-政府, enterprise-企业, education-教育, law_firm-律所
     */
    private String code;
    
    /**
     * 类型名称
     */
    private String name;
    
    /**
     * 图标
     */
    private String icon;
    
    /**
     * 颜色
     */
    private String color;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 排序
     */
    private Integer sortOrder;
    
    /**
     * 状态: 1-启用, 0-禁用
     */
    private String status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
