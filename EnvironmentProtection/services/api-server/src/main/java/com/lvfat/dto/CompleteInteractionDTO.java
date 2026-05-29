package com.lvfat.dto;

import lombok.Data;

/**
 * 完成互动请求DTO
 */
@Data
public class CompleteInteractionDTO {
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 互动点ID
     */
    private String pointId;

    /**
     * 互动点类型
     */
    private String pointType;

    /**
     * 奖励能量
     */
    private Integer rewardEnergy;

    /**
     * 任务ID（可选）
     */
    private String taskId;
}
