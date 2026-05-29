package com.lvfat.service;

import com.lvfat.dto.*;

/**
 * 宠物养成服务接口
 */
public interface PetService {

    /**
     * 获取宠物信息
     * @param userId 用户ID
     * @return 宠物信息
     */
    PetInfoDTO getPetInfo(String userId);

    /**
     * 创建宠物
     * @param dto 创建宠物请求
     * @return 宠物信息
     */
    PetInfoDTO createPet(CreatePetDTO dto);

    /**
     * 更新宠物位置
     * @param dto 位置信息
     */
    void updatePosition(UpdatePositionDTO dto);

    /**
     * 完成互动
     * @param dto 互动请求
     * @return 互动结果
     */
    InteractionResultDTO completeInteraction(CompleteInteractionDTO dto);

    /**
     * 获取场景信息
     * @param sceneId 场景ID
     * @return 场景信息
     */
    Object getScene(Integer sceneId);
}
