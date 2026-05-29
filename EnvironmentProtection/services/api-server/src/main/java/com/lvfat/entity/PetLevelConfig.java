package com.lvfat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 宠物等级配置实体
 */
@Data
@TableName("pet_level_config")
public class PetLevelConfig {

    /**
     * 等级
     */
    @TableId
    private Integer level;

    /**
     * 该等级最低经验值
     */
    private Integer minExp;

    /**
     * 该等级最高经验值，NULL表示无上限
     */
    private Integer maxExp;

    /**
     * 等级称号
     */
    private String levelTitle;

    /**
     * 解锁的技能（逗号分隔）
     */
    private String unlockSkill;

    /**
     * 解锁的场景
     */
    private String unlockScene;

    /**
     * 经验加成倍率
     */
    private BigDecimal bonusExpRate;

    /**
     * 金币加成倍率
     */
    private BigDecimal bonusCoinRate;

    /**
     * 需要的好友数量
     */
    private Integer requiredFriends;

    /**
     * 需要完成的任务数量
     */
    private Integer requiredTasks;

    /**
     * 头像框样式
     */
    private String avatarFrame;

    /**
     * 称号背景颜色
     */
    private String titleBgColor;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 判断当前经验是否达到该等级
     */
    public boolean isInLevel(int currentExp) {
        return currentExp >= minExp && (maxExp == null || currentExp <= maxExp);
    }

    /**
     * 计算升级到下一级需要的经验
     */
    public int getExpToNextLevel() {
        if (maxExp == null) {
            return Integer.MAX_VALUE;
        }
        return maxExp - minExp + 1;
    }
}
