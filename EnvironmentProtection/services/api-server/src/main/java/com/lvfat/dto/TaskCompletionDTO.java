package com.lvfat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 任务完成请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCompletionDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 宠物ID
     */
    private Long petId;

    /**
     * 任务代码
     */
    private String taskCode;

    /**
     * 互动持续时间（毫秒）
     */
    private Integer durationMs;

    /**
     * 客户端时间戳
     */
    private Long clientTimestamp;

    /**
     * 客户端设备信息
     */
    private String deviceInfo;
}
