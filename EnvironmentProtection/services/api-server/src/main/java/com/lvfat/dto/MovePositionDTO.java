package com.lvfat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 移动位置DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovePositionDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 宠物ID
     */
    private Long petId;

    /**
     * 目标位置X
     */
    private BigDecimal targetX;

    /**
     * 目标位置Y
     */
    private BigDecimal targetY;

    /**
     * 目标位置Z
     */
    private BigDecimal targetZ;

    /**
     * 移动类型：click(点击移动)、joystick(摇杆移动)
     */
    private String moveType;

    /**
     * 客户端时间戳
     */
    private Long clientTimestamp;
}
