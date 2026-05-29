package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 宠物场景实体
 */
@Data
@TableName("pet_scene")
public class PetScene {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 场景名称
     */
    private String sceneName;

    /**
     * 场景编码
     */
    private String sceneCode;

    /**
     * 场景背景图
     */
    private String sceneImage;

    /**
     * 场景宽度
     */
    private Integer sceneWidth;

    /**
     * 场景高度
     */
    private Integer sceneHeight;

    /**
     * 描述
     */
    private String description;

    /**
     * 解锁等级
     */
    private Integer unlockLevel;

    /**
     * 状态: 0-禁用, 1-启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
