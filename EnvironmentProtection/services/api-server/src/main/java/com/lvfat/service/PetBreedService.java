package com.lvfat.service;

import com.lvfat.dto.*;

/**
 * 宠物养成服务接口
 */
public interface PetBreedService {

    // ==================== 空间相关 ====================

    /**
     * 获取宠物养成空间状态
     */
    PetSceneStateDTO getPetSceneState(Long userId, Long petId);

    /**
     * 创建或获取宠物养成空间
     */
    PetSceneStateDTO getOrCreateBreedSpace(Long userId, Long petId);

    /**
     * 更新宠物位置
     */
    void updatePetPosition(MovePositionDTO positionDTO);

    // ==================== 任务相关 ====================

    /**
     * 获取可用互动任务列表
     */
    java.util.List<TaskInfoDTO> getAvailableTasks(Long userId, Long petId);

    /**
     * 获取任务详情
     */
    TaskInfoDTO getTaskDetail(String taskCode);

    /**
     * 完成互动任务
     */
    InteractionResultDTO completeTask(TaskCompletionDTO completionDTO);

    /**
     * 获取任务完成历史
     */
    java.util.List<InteractionResultDTO> getTaskHistory(Long userId, Long petId, Integer page, Integer size);

    // ==================== 等级相关 ====================

    /**
     * 获取宠物等级信息
     */
    LevelInfoDTO getPetLevelInfo(Long petId);

    /**
     * 获取宠物信息
     */
    PetInfoDTO getPetInfo(Long userId, Long petId);

    // ==================== 场景相关 ====================

    /**
     * 获取所有场景列表
     */
    java.util.List<SceneInfoDTO> getAllScenes();

    /**
     * 获取场景详情
     */
    SceneInfoDTO getSceneDetail(String sceneCode);

    /**
     * 解锁场景
     */
    SceneInfoDTO unlockScene(Long userId, Long petId, String sceneCode);

    /**
     * 切换场景
     */
    PetSceneStateDTO switchScene(Long userId, Long petId, String sceneCode);

    // ==================== 成就相关 ====================

    /**
     * 检查并解锁成就
     */
    java.util.List<InteractionResultDTO.AchievementInfo> checkAndUnlockAchievements(Long userId, Long petId);

    // ==================== 统计相关 ====================

    /**
     * 获取今日统计数据
     */
    java.util.Map<String, Object> getTodayStats(Long userId, Long petId);

    /**
     * 获取累计统计数据
     */
    java.util.Map<String, Object> getTotalStats(Long userId, Long petId);
}
