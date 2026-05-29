package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户宠物实体
 */
@Data
@TableName("user_pet")
public class UserPet {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 宠物名称
     */
    private String petName;

    /**
     * 宠物类型: leaf_balance-叶子天平, energy_leaf-能源叶子, law_tree-法律之树
     */
    private String petType;

    /**
     * 宠物形象URL
     */
    private String petImage;

    /**
     * 等级
     */
    private Integer level;

    /**
     * 经验值
     */
    private Integer exp;

    /**
     * 绿色能量
     */
    private Integer energy;

    /**
     * 累计获得能量
     */
    private Integer totalEnergy;

    /**
     * 当前X坐标
     */
    private Double currentX;

    /**
     * 当前Y坐标
     */
    private Double currentY;

    /**
     * 朝向: up, down, left, right
     */
    private String direction;

    /**
     * 场景ID
     */
    private Integer sceneId;

    /**
     * 状态: 0-离线, 1-在线
     */
    private Integer status;

    /**
     * 最后活跃时间
     */
    private LocalDateTime lastActiveTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer deleted;

    // ==================== 常量定义 ====================
    public static final String PET_TYPE_LEAF_BALANCE = "leaf_balance";
    public static final String PET_TYPE_ENERGY_LEAF = "energy_leaf";
    public static final String PET_TYPE_LAW_TREE = "law_tree";

    public static final String DIRECTION_UP = "up";
    public static final String DIRECTION_DOWN = "down";
    public static final String DIRECTION_LEFT = "left";
    public static final String DIRECTION_RIGHT = "right";

    public static final Integer STATUS_OFFLINE = 0;
    public static final Integer STATUS_ONLINE = 1;
}
