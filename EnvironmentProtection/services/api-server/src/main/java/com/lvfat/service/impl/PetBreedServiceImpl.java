package com.lvfat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lvfat.entity.*;
import com.lvfat.mapper.*;
import com.lvfat.service.PetBreedService;
import com.lvfat.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 宠物养成服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PetBreedServiceImpl implements PetBreedService {

    private final PetBreedSpaceMapper petBreedSpaceMapper;
    private final PetInteractionTaskMapper petInteractionTaskMapper;
    private final UserPetTaskLogMapper userPetTaskLogMapper;
    private final PetLevelConfigMapper petLevelConfigMapper;
    private final PetRewardRecordMapper petRewardRecordMapper;
    private final PetSceneExtMapper petSceneExtMapper;
    private final PetActionMapper petActionMapper;
    private final PetAchievementMapper petAchievementMapper;
    private final UserPetAchievementMapper userPetAchievementMapper;

    // ==================== 空间相关实现 ====================

    @Override
    public PetSceneStateDTO getPetSceneState(Long userId, Long petId) {
        PetBreedSpace space = petBreedSpaceMapper.findByUserIdAndPetId(userId, petId);
        if (space == null) {
            return getOrCreateBreedSpace(userId, petId);
        }
        return convertToSceneStateDTO(space);
    }

    @Override
    @Transactional
    public PetSceneStateDTO getOrCreateBreedSpace(Long userId, Long petId) {
        PetBreedSpace space = petBreedSpaceMapper.findByUserIdAndPetId(userId, petId);
        
        if (space == null) {
            space = new PetBreedSpace();
            space.setUserId(userId);
            space.setPetId(petId);
            space.setCurrentScene(PetBreedSpace.SCENE_GARDEN);
            space.setLastPositionX(BigDecimal.ZERO);
            space.setLastPositionY(BigDecimal.ZERO);
            space.setLastPositionZ(BigDecimal.ZERO);
            space.setSpaceLevel(1);
            space.setSpaceExp(0);
            space.setUnlockedScenes(PetBreedSpace.SCENE_GARDEN);
            petBreedSpaceMapper.insert(space);
        }
        
        return convertToSceneStateDTO(space);
    }

    @Override
    @Transactional
    public void updatePetPosition(MovePositionDTO positionDTO) {
        PetBreedSpace space = petBreedSpaceMapper.findByUserIdAndPetId(
            positionDTO.getUserId(), positionDTO.getPetId());
        
        if (space != null) {
            space.setLastPositionX(positionDTO.getTargetX());
            space.setLastPositionY(positionDTO.getTargetY());
            space.setLastPositionZ(positionDTO.getTargetZ());
            petBreedSpaceMapper.updateById(space);
        }
    }

    // ==================== 任务相关实现 ====================

    @Override
    public List<TaskInfoDTO> getAvailableTasks(Long userId, Long petId) {
        // 获取宠物等级
        int petLevel = getPetLevel(petId);
        
        // 获取所有可用任务
        List<PetInteractionTask> allTasks = petInteractionTaskMapper.findAvailableTasks(petLevel);
        
        // 获取今日完成记录
        LocalDateTime todayStart = LocalDateTime.now().with(LocalTime.MIN);
        List<UserPetTaskLog> todayLogs = userPetTaskLogMapper.findByUserIdAfter(userId, todayStart);
        Map<String, LocalDateTime> lastCompletedMap = todayLogs.stream()
            .collect(Collectors.toMap(UserPetTaskLog::getTaskCode, UserPetTaskLog::getCompletedAt,
                (existing, replacement) -> existing.isAfter(replacement) ? existing : replacement));
        
        return allTasks.stream()
            .map(task -> convertToTaskInfoDTO(task, lastCompletedMap.get(task.getTaskCode())))
            .collect(Collectors.toList());
    }

    @Override
    public TaskInfoDTO getTaskDetail(String taskCode) {
        PetInteractionTask task = petInteractionTaskMapper.findByTaskCode(taskCode);
        if (task == null) {
            return null;
        }
        return convertToTaskInfoDTO(task, null);
    }

    @Override
    @Transactional
    public InteractionResultDTO completeTask(TaskCompletionDTO completionDTO) {
        // 获取任务信息
        PetInteractionTask task = petInteractionTaskMapper.findByTaskCode(completionDTO.getTaskCode());
        if (task == null) {
            return InteractionResultDTO.builder()
                .success(false)
                .errorMessage("任务不存在")
                .build();
        }
        
        // 检查冷却时间
        UserPetTaskLog lastLog = userPetTaskLogMapper.findLastByUserIdAndPetIdAndTaskCode(
            completionDTO.getUserId(), completionDTO.getPetId(), completionDTO.getTaskCode());
        
        if (lastLog != null && task.getCooldownSeconds() > 0) {
            LocalDateTime cooldownEnd = lastLog.getCompletedAt().plusSeconds(task.getCooldownSeconds());
            if (LocalDateTime.now().isBefore(cooldownEnd)) {
                long remaining = Duration.between(LocalDateTime.now(), cooldownEnd).getSeconds();
                return InteractionResultDTO.builder()
                    .success(false)
                    .errorMessage("任务冷却中，剩余 " + remaining + " 秒")
                    .build();
            }
        }
        
        // 获取宠物等级信息计算加成
        PetLevelConfig levelConfig = petLevelConfigMapper.findByLevel(getPetLevel(completionDTO.getPetId()));
        BigDecimal expBonus = levelConfig != null ? 
            (levelConfig.getBonusExpRate() != null ? levelConfig.getBonusExpRate() : BigDecimal.ONE) : BigDecimal.ONE;
        BigDecimal coinBonus = levelConfig != null ? 
            (levelConfig.getBonusCoinRate() != null ? levelConfig.getBonusCoinRate() : BigDecimal.ONE) : BigDecimal.ONE;
        
        // 计算实际奖励
        int actualExp = new BigDecimal(task.getExpReward()).multiply(expBonus).intValue();
        int actualCoin = new BigDecimal(task.getCoinReward()).multiply(coinBonus).intValue();
        
        // 更新宠物经验（这里简化处理，实际可能需要更新user_pet表）
        int oldLevel = getPetLevel(completionDTO.getPetId());
        int oldExp = getPetExp(completionDTO.getPetId());
        int newExp = oldExp + actualExp;
        int newLevel = calculateLevel(newExp);
        boolean levelUp = newLevel > oldLevel;
        
        // 记录任务日志
        UserPetTaskLog taskLog = new UserPetTaskLog();
        taskLog.setUserId(completionDTO.getUserId());
        taskLog.setPetId(completionDTO.getPetId());
        taskLog.setTaskId(task.getId());
        taskLog.setTaskCode(task.getTaskCode());
        taskLog.setTaskName(task.getTaskName());
        taskLog.setExpGained(actualExp);
        taskLog.setCoinGained(actualCoin);
        taskLog.setDurationMs(completionDTO.getDurationMs());
        taskLog.setDeviceInfo(completionDTO.getDeviceInfo());
        userPetTaskLogMapper.insert(taskLog);
        
        // 记录奖励
        recordReward(completionDTO.getUserId(), completionDTO.getPetId(), 
            PetRewardRecord.TYPE_EXP, actualExp, task.getTaskCode(), task.getTaskName(),
            oldLevel, newLevel, oldExp, newExp);
        recordReward(completionDTO.getUserId(), completionDTO.getPetId(), 
            PetRewardRecord.TYPE_COIN, actualCoin, task.getTaskCode(), task.getTaskName(),
            oldLevel, newLevel, oldExp, newExp);
        
        // 检查成就
        List<InteractionResultDTO.AchievementInfo> unlockedAchievements = checkAndUnlockAchievements(
            completionDTO.getUserId(), completionDTO.getPetId());
        
        // 检查是否解锁新场景
        String unlockedScene = checkAndUnlockScene(newLevel);
        
        // 获取下一级信息
        PetLevelConfig nextLevelConfig = petLevelConfigMapper.findNextLevel(newLevel);
        int nextLevelExp = nextLevelConfig != null ? nextLevelConfig.getMinExp() : newExp;
        
        return InteractionResultDTO.builder()
            .success(true)
            .expGained(task.getExpReward())
            .coinGained(task.getCoinReward())
            .actualExpGained(actualExp)
            .actualCoinGained(actualCoin)
            .taskId(task.getId())
            .taskCode(task.getTaskCode())
            .taskName(task.getTaskName())
            .taskType(task.getTaskType())
            .levelUp(levelUp)
            .newLevel(newLevel)
            .currentExp(newExp)
            .nextLevelExp(nextLevelExp)
            .levelTitle(levelConfig != null ? levelConfig.getLevelTitle() : "")
            .itemRewards(task.getItemRewardJson())
            .unlockedAchievements(unlockedAchievements)
            .unlockedScene(unlockedScene)
            .build();
    }

    @Override
    public List<InteractionResultDTO> getTaskHistory(Long userId, Long petId, Integer page, Integer size) {
        List<UserPetTaskLog> logs = userPetTaskLogMapper.findByUserIdAndPetId(userId, petId);
        
        // 分页
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, logs.size());
        
        if (fromIndex >= logs.size()) {
            return Collections.emptyList();
        }
        
        return logs.subList(fromIndex, toIndex).stream()
            .map(log -> InteractionResultDTO.builder()
                .success(true)
                .expGained(log.getExpGained())
                .coinGained(log.getCoinGained())
                .taskId(log.getTaskId())
                .taskCode(log.getTaskCode())
                .taskName(log.getTaskName())
                .build())
            .collect(Collectors.toList());
    }

    // ==================== 等级相关实现 ====================

    @Override
    public LevelInfoDTO getPetLevelInfo(Long petId) {
        int level = getPetLevel(petId);
        int exp = getPetExp(petId);
        
        PetLevelConfig currentConfig = petLevelConfigMapper.findByLevel(level);
        PetLevelConfig nextConfig = petLevelConfigMapper.findNextLevel(level);
        PetLevelConfig byExpConfig = petLevelConfigMapper.findByExp(exp);
        
        int nextLevelExp = nextConfig != null ? nextConfig.getMinExp() : exp;
        double progressPercent = nextConfig != null ? 
            (double) (exp - currentConfig.getMinExp()) / (nextConfig.getMinExp() - currentConfig.getMinExp()) * 100 : 100;
        
        return LevelInfoDTO.builder()
            .currentLevel(level)
            .currentExp(exp)
            .levelTitle(currentConfig != null ? currentConfig.getLevelTitle() : "")
            .expBonusRate(currentConfig != null && currentConfig.getBonusExpRate() != null ? 
                currentConfig.getBonusExpRate().doubleValue() : 1.0)
            .coinBonusRate(currentConfig != null && currentConfig.getBonusCoinRate() != null ? 
                currentConfig.getBonusCoinRate().doubleValue() : 1.0)
            .expToNextLevel(nextLevelExp)
            .nextLevel(nextConfig != null ? nextConfig.getLevel() : level)
            .nextLevelTitle(nextConfig != null ? nextConfig.getLevelTitle() : "")
            .progressPercent(Math.min(progressPercent, 100))
            .isMaxLevel(nextConfig == null)
            .unlockedSkill(currentConfig != null ? currentConfig.getUnlockSkill() : null)
            .unlockedScene(currentConfig != null ? currentConfig.getUnlockScene() : null)
            .requiredFriends(currentConfig != null ? currentConfig.getRequiredFriends() : 0)
            .requiredTasks(currentConfig != null ? currentConfig.getRequiredTasks() : 0)
            .avatarFrame(currentConfig != null ? currentConfig.getAvatarFrame() : null)
            .build();
    }

    @Override
    public PetInfoDTO getPetInfo(Long userId, Long petId) {
        int level = getPetLevel(petId);
        int exp = getPetExp(petId);
        
        PetLevelConfig levelConfig = petLevelConfigMapper.findByLevel(level);
        int totalExpGained = userPetTaskLogMapper.sumExpByUserIdAndPetId(userId, petId);
        int totalCoinGained = userPetTaskLogMapper.sumCoinByUserIdAndPetId(userId, petId);
        int totalTasksCompleted = userPetTaskLogMapper.countByUserIdAndPetId(userId, petId);
        int achievementCount = userPetAchievementMapper.countByUserIdAndPetId(userId, petId);
        
        // 获取解锁的技能
        List<String> skills = new ArrayList<>();
        if (levelConfig != null && levelConfig.getUnlockSkill() != null) {
            skills = Arrays.asList(levelConfig.getUnlockSkill().split(","));
        }
        
        // 获取解锁的场景
        PetBreedSpace space = petBreedSpaceMapper.findByUserIdAndPetId(userId, petId);
        List<String> scenes = space != null && space.getUnlockedScenes() != null ? 
            Arrays.asList(space.getUnlockedScenes().split(",")) : Collections.singletonList(PetBreedSpace.SCENE_GARDEN);
        
        return PetInfoDTO.builder()
            .petId(petId)
            .petName("我的宠物") // 实际应该从user_pet表获取
            .level(level)
            .exp(exp)
            .nextLevelExp(levelConfig != null && levelConfig.getMaxExp() != null ? 
                levelConfig.getMaxExp() : exp)
            .levelTitle(levelConfig != null ? levelConfig.getLevelTitle() : "")
            .expBonusRate(levelConfig != null && levelConfig.getBonusExpRate() != null ? 
                levelConfig.getBonusExpRate() : BigDecimal.ONE)
            .coinBonusRate(levelConfig != null && levelConfig.getBonusCoinRate() != null ? 
                levelConfig.getBonusCoinRate() : BigDecimal.ONE)
            .energy(100) // 默认值
            .emotion(100) // 默认值
            .totalEnergy(0)
            .totalTasksCompleted(totalTasksCompleted)
            .totalExpGained(totalExpGained)
            .totalCoinGained(totalCoinGained)
            .unlockedSkills(skills)
            .unlockedScenes(scenes)
            .unlockedAchievementCount(achievementCount)
            .avatarFrame(levelConfig != null ? levelConfig.getAvatarFrame() : null)
            .build();
    }

    // ==================== 场景相关实现 ====================

    @Override
    public List<SceneInfoDTO> getAllScenes() {
        List<PetSceneExt> scenes = petSceneExtMapper.findAllActive();
        return scenes.stream()
            .map(this::convertToSceneInfoDTO)
            .collect(Collectors.toList());
    }

    @Override
    public SceneInfoDTO getSceneDetail(String sceneCode) {
        PetSceneExt scene = petSceneExtMapper.findBySceneCode(sceneCode);
        if (scene == null) {
            return null;
        }
        return convertToSceneInfoDTO(scene);
    }

    @Override
    @Transactional
    public SceneInfoDTO unlockScene(Long userId, Long petId, String sceneCode) {
        PetSceneExt scene = petSceneExtMapper.findBySceneCode(sceneCode);
        if (scene == null) {
            throw new RuntimeException("场景不存在");
        }
        
        int petLevel = getPetLevel(petId);
        if (petLevel < scene.getRequiredLevel()) {
            throw new RuntimeException("宠物等级不足，需要 " + scene.getRequiredLevel() + " 级");
        }
        
        PetBreedSpace space = petBreedSpaceMapper.findByUserIdAndPetId(userId, petId);
        if (space == null) {
            getOrCreateBreedSpace(userId, petId);
            space = petBreedSpaceMapper.findByUserIdAndPetId(userId, petId);
        }
        
        space.unlockScene(sceneCode);
        petBreedSpaceMapper.updateById(space);
        
        return convertToSceneInfoDTO(scene);
    }

    @Override
    @Transactional
    public PetSceneStateDTO switchScene(Long userId, Long petId, String sceneCode) {
        PetBreedSpace space = petBreedSpaceMapper.findByUserIdAndPetId(userId, petId);
        if (space == null) {
            return getOrCreateBreedSpace(userId, petId);
        }
        
        if (!space.hasUnlockedScene(sceneCode)) {
            throw new RuntimeException("场景未解锁");
        }
        
        space.setCurrentScene(sceneCode);
        // 重置位置到新场景的出生点
        PetSceneExt scene = petSceneExtMapper.findBySceneCode(sceneCode);
        if (scene != null) {
            space.setLastPositionX(scene.getSpawnPointX() != null ? scene.getSpawnPointX() : BigDecimal.ZERO);
            space.setLastPositionZ(scene.getSpawnPointZ() != null ? scene.getSpawnPointZ() : BigDecimal.ZERO);
            space.setLastPositionY(BigDecimal.ZERO);
        }
        
        petBreedSpaceMapper.updateById(space);
        return convertToSceneStateDTO(space);
    }

    // ==================== 成就相关实现 ====================

    @Override
    public List<InteractionResultDTO.AchievementInfo> checkAndUnlockAchievements(Long userId, Long petId) {
        List<InteractionResultDTO.AchievementInfo> unlocked = new ArrayList<>();
        
        // 获取用户宠物的统计信息
        int totalTasks = userPetTaskLogMapper.countByUserIdAndPetId(userId, petId);
        int level = getPetLevel(petId);
        
        // 获取所有成就
        List<PetAchievement> achievements = petAchievementMapper.findAllActive();
        
        for (PetAchievement achievement : achievements) {
            // 检查是否已解锁
            int count = userPetAchievementMapper.countByUserIdAndPetIdAndAchievementCode(
                userId, petId, achievement.getAchievementCode());
            if (count > 0) continue;
            
            // 检查达成条件
            boolean unlockedNow = false;
            switch (achievement.getConditionType()) {
                case PetAchievement.COND_TASK_COUNT:
                    unlockedNow = totalTasks >= achievement.getConditionValue();
                    break;
                case PetAchievement.COND_LEVEL:
                    unlockedNow = level >= achievement.getConditionValue();
                    break;
            }
            
            if (unlockedNow) {
                // 记录解锁
                UserPetAchievement userAchievement = new UserPetAchievement();
                userAchievement.setUserId(userId);
                userAchievement.setPetId(petId);
                userAchievement.setAchievementId(achievement.getId());
                userAchievement.setAchievementCode(achievement.getAchievementCode());
                userPetAchievementMapper.insert(userAchievement);
                
                // 发放奖励
                if (achievement.getExpReward() > 0) {
                    recordReward(userId, petId, PetRewardRecord.TYPE_EXP, 
                        achievement.getExpReward(), achievement.getAchievementCode(), achievement.getAchievementName(),
                        level, level, getPetExp(petId), getPetExp(petId) + achievement.getExpReward());
                }
                if (achievement.getCoinReward() > 0) {
                    recordReward(userId, petId, PetRewardRecord.TYPE_COIN, 
                        achievement.getCoinReward(), achievement.getAchievementCode(), achievement.getAchievementName(),
                        level, level, getPetExp(petId), getPetExp(petId));
                }
                
                unlocked.add(InteractionResultDTO.AchievementInfo.builder()
                    .achievementCode(achievement.getAchievementCode())
                    .achievementName(achievement.getAchievementName())
                    .expReward(achievement.getExpReward())
                    .coinReward(achievement.getCoinReward())
                    .build());
            }
        }
        
        return unlocked;
    }

    // ==================== 统计相关实现 ====================

    @Override
    public Map<String, Object> getTodayStats(Long userId, Long petId) {
        LocalDateTime todayStart = LocalDateTime.now().with(LocalTime.MIN);
        List<UserPetTaskLog> todayLogs = userPetTaskLogMapper.findByUserIdAfter(userId, todayStart);
        
        int todayExp = todayLogs.stream().mapToInt(UserPetTaskLog::getExpGained).sum();
        int todayCoin = todayLogs.stream().mapToInt(UserPetTaskLog::getCoinGained).sum();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("todayInteractionCount", todayLogs.size());
        stats.put("todayExpGained", todayExp);
        stats.put("todayCoinGained", todayCoin);
        
        return stats;
    }

    @Override
    public Map<String, Object> getTotalStats(Long userId, Long petId) {
        int totalExp = userPetTaskLogMapper.sumExpByUserIdAndPetId(userId, petId);
        int totalCoin = userPetTaskLogMapper.sumCoinByUserIdAndPetId(userId, petId);
        int totalTasks = userPetTaskLogMapper.countByUserIdAndPetId(userId, petId);
        int achievementCount = userPetAchievementMapper.countByUserIdAndPetId(userId, petId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalExpGained", totalExp);
        stats.put("totalCoinGained", totalCoin);
        stats.put("totalTasksCompleted", totalTasks);
        stats.put("unlockedAchievements", achievementCount);
        
        return stats;
    }

    // ==================== 私有辅助方法 ====================

    private int getPetLevel(Long petId) {
        // 简化处理，实际应该从user_pet表获取
        return 1;
    }

    private int getPetExp(Long petId) {
        // 简化处理，实际应该从user_pet表获取
        return 0;
    }

    private int calculateLevel(int exp) {
        PetLevelConfig config = petLevelConfigMapper.findByExp(exp);
        return config != null ? config.getLevel() : 1;
    }

    private void recordReward(Long userId, Long petId, String rewardType, int rewardValue,
                              String source, String sourceName, int oldLevel, int newLevel, int oldExp, int newExp) {
        PetRewardRecord record = new PetRewardRecord();
        record.setUserId(userId);
        record.setPetId(petId);
        record.setRewardType(rewardType);
        record.setRewardValue(rewardValue);
        record.setSource(source);
        record.setSourceName(sourceName);
        record.setPetLevelBefore(oldLevel);
        record.setPetLevelAfter(newLevel);
        record.setPetExpBefore(oldExp);
        record.setPetExpAfter(newExp);
        petRewardRecordMapper.insert(record);
    }

    private String checkAndUnlockScene(int level) {
        // 根据等级自动解锁场景
        if (level >= 5) return PetBreedSpace.SCENE_FOREST;
        if (level >= 10) return PetBreedSpace.SCENE_BEACH;
        if (level >= 15) return PetBreedSpace.SCENE_MOUNTAIN;
        if (level >= 20) return PetBreedSpace.SCENE_SNOWLAND;
        if (level >= 25) return PetBreedSpace.SCENE_NIGHT;
        return null;
    }

    private PetSceneStateDTO convertToSceneStateDTO(PetBreedSpace space) {
        PetSceneExt scene = petSceneExtMapper.findBySceneCode(space.getCurrentScene());
        
        List<PetSceneExt> allScenes = petSceneExtMapper.findAllActive();
        List<PetSceneStateDTO.SceneInfo> sceneInfos = allScenes.stream()
            .map(s -> {
                PetSceneStateDTO.SceneInfo info = new PetSceneStateDTO.SceneInfo();
                info.setSceneCode(s.getSceneCode());
                info.setSceneName(s.getSceneName());
                info.setThumbnailUrl(s.getThumbnailUrl());
                info.setIsUnlocked(space.hasUnlockedScene(s.getSceneCode()));
                info.setRequiredLevel(s.getRequiredLevel());
                return info;
            })
            .collect(Collectors.toList());
        
        Map<String, Object> todayStats = getTodayStats(space.getUserId(), space.getPetId());
        
        return PetSceneStateDTO.builder()
            .spaceId(space.getId())
            .userId(space.getUserId())
            .petId(space.getPetId())
            .currentScene(space.getCurrentScene())
            .currentSceneName(scene != null ? scene.getSceneName() : "")
            .positionX(space.getLastPositionX())
            .positionY(space.getLastPositionY())
            .positionZ(space.getLastPositionZ())
            .spaceLevel(space.getSpaceLevel())
            .unlockedScenes(sceneInfos)
            .todayInteractionCount((Integer) todayStats.get("todayInteractionCount"))
            .todayExpGained((Integer) todayStats.get("todayExpGained"))
            .build();
    }

    private TaskInfoDTO convertToTaskInfoDTO(PetInteractionTask task, LocalDateTime lastCompleted) {
        TaskInfoDTO.TaskInfoDTOBuilder builder = TaskInfoDTO.builder()
            .taskId(task.getId())
            .taskCode(task.getTaskCode())
            .taskName(task.getTaskName())
            .taskType(task.getTaskType())
            .taskTypeName(getTaskTypeName(task.getTaskType()))
            .description(task.getDescription())
            .iconUrl(task.getIconUrl())
            .expReward(task.getExpReward())
            .coinReward(task.getCoinReward())
            .itemRewardJson(task.getItemRewardJson())
            .cooldownSeconds(task.getCooldownSeconds())
            .minPetLevel(task.getMinPetLevel())
            .energyCost(task.getEnergyCost())
            .durationSeconds(task.getDurationSeconds())
            .animationType(task.getAnimationType())
            .lastCompletedAt(lastCompleted);
        
        if (lastCompleted != null && task.getCooldownSeconds() > 0) {
            LocalDateTime cooldownEnd = lastCompleted.plusSeconds(task.getCooldownSeconds());
            if (LocalDateTime.now().isBefore(cooldownEnd)) {
                long remaining = Duration.between(LocalDateTime.now(), cooldownEnd).getSeconds();
                builder.inCooldown(true);
                builder.cooldownRemaining((int) remaining);
            } else {
                builder.inCooldown(false);
                builder.cooldownRemaining(0);
            }
        } else {
            builder.inCooldown(false);
            builder.cooldownRemaining(0);
        }
        
        return builder.build();
    }

    private String getTaskTypeName(String taskType) {
        switch (taskType) {
            case PetInteractionTask.TYPE_FEED: return "喂食";
            case PetInteractionTask.TYPE_PET: return "抚摸";
            case PetInteractionTask.TYPE_CLEAN: return "清洁";
            case PetInteractionTask.TYPE_TRAIN: return "训练";
            case PetInteractionTask.TYPE_PLAY: return "玩耍";
            default: return taskType;
        }
    }

    private SceneInfoDTO convertToSceneInfoDTO(PetSceneExt scene) {
        return SceneInfoDTO.builder()
            .sceneCode(scene.getSceneCode())
            .sceneName(scene.getSceneName())
            .description(scene.getSceneDescription())
            .thumbnailUrl(scene.getThumbnailUrl())
            .modelUrl(scene.getModelUrl())
            .backgroundUrl(scene.getBackgroundUrl())
            .musicUrl(scene.getMusicUrl())
            .particleEffect(scene.getParticleEffect())
            .requiredLevel(scene.getRequiredLevel())
            .requiredCoin(scene.getRequiredCoin())
            .isDefault(scene.getIsDefault())
            .mapBounds(SceneInfoDTO.MapBounds.builder()
                .xMin(scene.getMapBoundsXMin())
                .xMax(scene.getMapBoundsXMax())
                .zMin(scene.getMapBoundsZMin())
                .zMax(scene.getMapBoundsZMax())
                .build())
            .spawnPoint(SceneInfoDTO.SpawnPoint.builder()
                .x(scene.getSpawnPointX())
                .z(scene.getSpawnPointZ())
                .build())
            .build();
    }
}
