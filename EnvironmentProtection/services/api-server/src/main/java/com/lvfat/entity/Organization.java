package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 组织架构实体
 */
@Data
@TableName("sys_organization")
public class Organization {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    private String name;
    
    /**
     * 层级: 1-省级, 2-县级, 3-乡镇级, 4-村级
     */
    private Integer level;
    
    private String parentId;
    
    private String code;
    
    private String description;
    
    /**
     * 组织类型: government-政府, enterprise-企业, platform-平台运营, education-教育, law_firm-律所
     */
    private String type;
    
    /**
     * 组织分类: province-省级, city-市级, county-县级, town-乡镇
     * enterprise: headquarters-总公司, branch-分公司, department-部门
     * education: bureau-教育局, school-学校, grade-年级, class-班级
     */
    private String category;
    
    /**
     * 行政区划代码
     */
    private String regionCode;
    
    /**
     * 负责人姓名
     */
    private String leaderName;
    
    /**
     * 负责人电话
     */
    private String leaderPhone;
    
    /**
     * 联系地址
     */
    private String contactAddress;
    
    /**
     * 排序序号
     */
    private Integer sortOrder;
    
    /**
     * 父级链路径, 格式: /id1/id2/id3/
     */
    private String parentChain;
    
    /**
     * 层级路径, 格式: 省级-市级-县级
     */
    private String levelPath;
    
    /**
     * 职员/员工人数
     */
    private Integer staffCount;
    
    /**
     * 学生人数(教育体系专用)
     */
    private Integer studentCount;
    
    @TableField(exist = false)
    private List<Organization> children;
    
    @TableField(exist = false)
    private List<User> users;
    
    @TableField(exist = false)
    private List<Employee> employees;
    
    @TableField(exist = false)
    private String parentName;
    
    @TableField(exist = false)
    private String typeName;
    
    @TableField(exist = false)
    private String categoryName;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
