package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 组织分类配置实体
 */
@Data
@TableName("organization_category")
public class OrganizationCategory {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 所属类型编码
     */
    private String typeCode;
    
    /**
     * 分类编码
     */
    private String code;
    
    /**
     * 分类名称
     */
    private String name;
    
    /**
     * 层级序号
     */
    private Integer level;
    
    /**
     * 上级分类编码
     */
    private String parentCategoryCode;
    
    /**
     * 是否管理职员: 0-否, 1-是
     */
    private Integer hasEmployee;
    
    /**
     * 是否管理学生: 0-否, 1-是
     */
    private Integer hasStudent;
    
    /**
     * 是否可以下发任务: 0-否, 1-是
     */
    private Integer canDispatchTask;
    
    /**
     * 是否可以接收任务: 0-否, 1-是
     */
    private Integer canReceiveTask;
    
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
