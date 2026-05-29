package com.lvfat.dto;

import lombok.Data;

/**
 * 更新宠物位置请求DTO
 */
@Data
public class UpdatePositionDTO {
    /**
     * 用户ID
     */
    private String userId;

    /**
     * X坐标
     */
    private Double x;

    /**
     * Y坐标
     */
    private Double y;

    /**
     * 朝向
     */
    private String direction;
}
