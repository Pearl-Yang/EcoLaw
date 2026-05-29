package com.lvfat.dto;

import lombok.Data;

/**
 * 创建宠物请求DTO
 */
@Data
public class CreatePetDTO {
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 宠物名称
     */
    private String petName;

    /**
     * 宠物类型
     */
    private String petType;
}
