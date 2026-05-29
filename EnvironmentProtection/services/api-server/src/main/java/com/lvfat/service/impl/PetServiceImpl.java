package com.lvfat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lvfat.common.exception.BusinessException;
import com.lvfat.dto.*;
import com.lvfat.entity.PetInteractLog;
import com.lvfat.entity.PetLevelRule;
import com.lvfat.entity.ScenePoint;
import com.lvfat.entity.UserPet;
import com.lvfat.repository.PetInteractLogMapper;
import com.lvfat.repository.PetLevelRuleMapper;
import com.lvfat.repository.ScenePointMapper;
import com.lvfat.repository.UserPetMapper;
import com.lvfat.service.PetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 宠物养成服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PetServiceImpl implements PetService {

    private final UserPetMapper userPetMapper;
    private final ScenePointMapper scenePointMapper;
    private final PetInteractLogMapper petInteractLogMapper;
    private final PetLevelRuleMapper petLevelRuleMapper;

    @Override
    public PetInfoDTO getPetInfo(String userId) {
        UserPet pet = userPetMapper.selectOne(
            new LambdaQueryWrapper<UserPet>()
                .eq(UserPet::getUserId, userId)
                .eq(UserPet::getDeleted, 0)
        );

        if (pet != null) {
            PetInfoDTO dto = PetInfoDTO.builder()
                .petId(Long.valueOf(pet.getId()))
                .petName(pet.getPetName())
                .petType(pet.getPetType())
                .petThumbnailUrl(pet.getPetImage())
                .level(pet.getLevel())
                .exp(pet.getExp())
                .energy(pet.getEnergy())
                .totalEnergy(pet.getTotalEnergy())
                .build();

            // 获取下一级所需经验
            Integer expToNext = petLevelRuleMapper.getExpRequiredByLevel(pet.getLevel() + 1);
            dto.setNextLevelExp(expToNext != null ? expToNext : 999999);

            return dto;
        } else {
            // 返回默认值
            return PetInfoDTO.builder()
                .petName("绿小宝")
                .level(1)
                .exp(0)
                .energy(0)
                .nextLevelExp(100)
                .build();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PetInfoDTO createPet(CreatePetDTO dto) {
        // 检查用户是否已有宠物
        UserPet existPet = userPetMapper.selectOne(
            new LambdaQueryWrapper<UserPet>()
                .eq(UserPet::getUserId, dto.getUserId())
                .eq(UserPet::getDeleted, 0)
        );

        if (existPet != null) {
            throw new BusinessException("用户已有宠物，无法重复创建");
        }

        // 创建新宠物
        UserPet pet = new UserPet();
        pet.setUserId(dto.getUserId());
        pet.setPetName(dto.getPetName() != null ? dto.getPetName() : "绿小宝");
        pet.setPetType(dto.getPetType() != null ? dto.getPetType() : UserPet.PET_TYPE_LEAF_BALANCE);
        pet.setLevel(1);
        pet.setExp(0);
        pet.setEnergy(0);
        pet.setTotalEnergy(0);
        pet.setCurrentX(375.0);
        pet.setCurrentY(600.0);
        pet.setDirection(UserPet.DIRECTION_DOWN);
        pet.setSceneId(1);
        pet.setStatus(UserPet.STATUS_ONLINE);
        pet.setLastActiveTime(LocalDateTime.now());

        userPetMapper.insert(pet);

        log.info("用户[{}]创建宠物成功，宠物ID: {}", dto.getUserId(), pet.getId());

        return getPetInfo(dto.getUserId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePosition(UpdatePositionDTO dto) {
        UserPet pet = userPetMapper.selectOne(
            new LambdaQueryWrapper<UserPet>()
                .eq(UserPet::getUserId, dto.getUserId())
                .eq(UserPet::getDeleted, 0)
        );

        if (pet == null) {
            log.warn("更新宠物位置失败，用户[{}]没有宠物", dto.getUserId());
            return;
        }

        pet.setCurrentX(dto.getX());
        pet.setCurrentY(dto.getY());
        if (dto.getDirection() != null) {
            pet.setDirection(dto.getDirection());
        }
        pet.setLastActiveTime(LocalDateTime.now());

        userPetMapper.updateById(pet);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InteractionResultDTO completeInteraction(CompleteInteractionDTO dto) {
        InteractionResultDTO result = new InteractionResultDTO();
        result.setSuccess(true);

        // 获取宠物
        UserPet pet = userPetMapper.selectOne(
            new LambdaQueryWrapper<UserPet>()
                .eq(UserPet::getUserId, dto.getUserId())
                .eq(UserPet::getDeleted, 0)
        );

        if (pet == null) {
            throw new BusinessException("宠物不存在");
        }

        // 获取互动点信息
        ScenePoint point = scenePointMapper.selectById(dto.getPointId());
        if (point == null) {
            throw new BusinessException("互动点不存在");
        }

        // 检查每日限制
        if (point.getDailyLimit() > 0) {
            int todayCount = scenePointMapper.getTodayInteractCount(dto.getPointId());
            if (todayCount >= point.getDailyLimit()) {
                throw new BusinessException("今日互动次数已用完，请明天再来");
            }
        }

        // 计算奖励
        int rewardEnergy = point.getRewardEnergy();
        int rewardExp = point.getRewardExp();
        if (dto.getRewardEnergy() != null) {
            rewardEnergy = dto.getRewardEnergy();
        }

        // 更新宠物能量和经验
        pet.setEnergy(pet.getEnergy() + rewardEnergy);
        pet.setTotalEnergy(pet.getTotalEnergy() + rewardEnergy);
        pet.setExp(pet.getExp() + rewardExp);
        pet.setLastActiveTime(LocalDateTime.now());

        // 检查升级
        Integer expToNext = petLevelRuleMapper.getExpRequiredByLevel(pet.getLevel() + 1);
        boolean leveledUp = false;
        String unlockedSkill = null;

        if (expToNext != null && pet.getExp() >= expToNext) {
            pet.setLevel(pet.getLevel() + 1);
            pet.setExp(pet.getExp() - expToNext);
            leveledUp = true;

            // 获取升级信息
            PetLevelRule levelRule = petLevelRuleMapper.getByLevel(pet.getLevel());
            if (levelRule != null && levelRule.getUnlockSkill() != null) {
                unlockedSkill = levelRule.getUnlockSkill();
            }

            log.info("用户[{}]的宠物升级到Lv.{}", dto.getUserId(), pet.getLevel());
        }

        userPetMapper.updateById(pet);

        // 记录互动日志
        PetInteractLog log = new PetInteractLog();
        log.setUserId(dto.getUserId());
        log.setPetId(pet.getId());
        log.setPointId(dto.getPointId());
        log.setPointType(dto.getPointType());
        log.setRewardEnergy(rewardEnergy);
        log.setRewardExp(rewardExp);
        log.setLevelUp(leveledUp ? pet.getLevel() : 0);
        log.setTaskId(dto.getTaskId());
        log.setCompleteStatus(PetInteractLog.COMPLETE_STATUS_YES);
        petInteractLogMapper.insert(log);

        // 构建结果
        result.setExpGained(rewardExp);
        result.setCoinGained(0);
        result.setActualExpGained(rewardExp);
        result.setActualCoinGained(0);
        result.setCurrentExp(pet.getExp());
        result.setNextLevelExp(expToNext != null ? expToNext : 999999);
        result.setLevelUp(leveledUp);
        result.setNewLevel(pet.getLevel());
        result.setUnlockedScene(unlockedSkill);

        if (leveledUp) {
            result.setErrorMessage("恭喜！宠物升级到 Lv." + pet.getLevel() +
                (unlockedSkill != null ? "，解锁技能：" + unlockedSkill : ""));
        } else {
            result.setErrorMessage("获得 " + rewardEnergy + " 绿色能量和 " + rewardExp + " 经验");
        }

        return result;
    }

    @Override
    public Object getScene(Integer sceneId) {
        // 获取场景信息
        List<ScenePoint> points = scenePointMapper.getBySceneId(sceneId);

        Map<String, Object> sceneData = new HashMap<>();
        sceneData.put("sceneId", sceneId);
        sceneData.put("points", points);

        return sceneData;
    }
}
