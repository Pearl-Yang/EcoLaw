package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 宠物升级规则实体
 */
@Data
@TableName("pet_level_rule")
public class PetLevelRule {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 等级
     */
    private Integer level;

    /**
     * 所需经验
     */
    private Integer expRequired;

    /**
     * 解锁技能
     */
    private String unlockSkill;

    /**
     * 升级描述
     */
    private String description;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
