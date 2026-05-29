package com.lvfat.controller;

import com.lvfat.common.result.Result;
import com.lvfat.dto.*;
import com.lvfat.service.PetBreedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 宠物养成控制器
 */
@Slf4j
@RestController
@RequestMapping("/pet/breed")
@RequiredArgsConstructor
@Tag(name = "宠物养成", description = "宠物养成空间相关接口")
public class PetBreedController {

    private final PetBreedService petBreedService;

    // ==================== 空间相关接口 ====================

    @Operation(summary = "获取宠物养成空间状态", description = "获取用户指定宠物的养成空间状态")
    @GetMapping("/space/{userId}/{petId}")
    public Result<PetSceneStateDTO> getPetSceneState(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "宠物ID") @PathVariable Long petId) {
        try {
            PetSceneStateDTO sceneState = petBreedService.getPetSceneState(userId, petId);
            return Result.success(sceneState);
        } catch (Exception e) {
            log.error("获取宠物养成空间状态失败: userId={}, petId={}", userId, petId, e);
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "获取或创建宠物养成空间", description = "获取用户宠物的养成空间，不存在则创建")
    @PostMapping("/space/create")
    public Result<PetSceneStateDTO> getOrCreateBreedSpace(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "宠物ID") @RequestParam Long petId) {
        try {
            PetSceneStateDTO sceneState = petBreedService.getOrCreateBreedSpace(userId, petId);
            return Result.success(sceneState);
        } catch (Exception e) {
            log.error("创建宠物养成空间失败: userId={}, petId={}", userId, petId, e);
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "更新宠物位置", description = "更新宠物在养成空间中的位置")
    @PostMapping("/move")
    public Result<Void> updatePetPosition(@RequestBody MovePositionDTO positionDTO) {
        try {
            petBreedService.updatePetPosition(positionDTO);
            return Result.success(null);
        } catch (Exception e) {
            log.error("更新宠物位置失败: {}", positionDTO, e);
            return Result.error(e.getMessage());
        }
    }

    // ==================== 任务相关接口 ====================

    @Operation(summary = "获取可用互动任务列表", description = "获取当前宠物可完成的互动任务列表")
    @GetMapping("/tasks")
    public Result<List<TaskInfoDTO>> getAvailableTasks(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "宠物ID") @RequestParam Long petId) {
        try {
            List<TaskInfoDTO> tasks = petBreedService.getAvailableTasks(userId, petId);
            return Result.success(tasks);
        } catch (Exception e) {
            log.error("获取可用任务列表失败: userId={}, petId={}", userId, petId, e);
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "获取任务详情", description = "根据任务代码获取任务详细信息")
    @GetMapping("/task/{taskCode}")
    public Result<TaskInfoDTO> getTaskDetail(
            @Parameter(description = "任务代码") @PathVariable String taskCode) {
        try {
            TaskInfoDTO task = petBreedService.getTaskDetail(taskCode);
            if (task == null) {
                return Result.fail("任务不存在");
            }
            return Result.success(task);
        } catch (Exception e) {
            log.error("获取任务详情失败: taskCode={}", taskCode, e);
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "完成互动任务", description = "完成指定的互动任务并获得奖励")
    @PostMapping("/complete-task")
    public Result<InteractionResultDTO> completeTask(@RequestBody TaskCompletionDTO completionDTO) {
        try {
            InteractionResultDTO result = petBreedService.completeTask(completionDTO);
            if (result.getSuccess() != null && !result.getSuccess()) {
                return Result.fail(result.getErrorMessage());
            }
            return Result.success(result);
        } catch (Exception e) {
            log.error("完成互动任务失败: {}", completionDTO, e);
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "获取任务完成历史", description = "获取用户宠物完成任务的历史记录")
    @GetMapping("/task-history/{userId}/{petId}")
    public Result<List<InteractionResultDTO>> getTaskHistory(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "宠物ID") @PathVariable Long petId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") Integer size) {
        try {
            List<InteractionResultDTO> history = petBreedService.getTaskHistory(userId, petId, page, size);
            return Result.success(history);
        } catch (Exception e) {
            log.error("获取任务历史失败: userId={}, petId={}", userId, petId, e);
            return Result.error(e.getMessage());
        }
    }

    // ==================== 等级相关接口 ====================

    @Operation(summary = "获取宠物等级信息", description = "获取宠物的等级、经验等信息")
    @GetMapping("/level-info/{petId}")
    public Result<LevelInfoDTO> getPetLevelInfo(
            @Parameter(description = "宠物ID") @PathVariable Long petId) {
        try {
            LevelInfoDTO levelInfo = petBreedService.getPetLevelInfo(petId);
            return Result.success(levelInfo);
        } catch (Exception e) {
            log.error("获取宠物等级信息失败: petId={}", petId, e);
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "获取宠物信息", description = "获取宠物的详细信息")
    @GetMapping("/pet-info")
    public Result<PetInfoDTO> getPetInfo(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "宠物ID") @RequestParam Long petId) {
        try {
            PetInfoDTO petInfo = petBreedService.getPetInfo(userId, petId);
            return Result.success(petInfo);
        } catch (Exception e) {
            log.error("获取宠物信息失败: userId={}, petId={}", userId, petId, e);
            return Result.error(e.getMessage());
        }
    }

    // ==================== 场景相关接口 ====================

    @Operation(summary = "获取所有场景列表", description = "获取所有可用的宠物养成场景")
    @GetMapping("/scenes")
    public Result<List<SceneInfoDTO>> getAllScenes() {
        try {
            List<SceneInfoDTO> scenes = petBreedService.getAllScenes();
            return Result.success(scenes);
        } catch (Exception e) {
            log.error("获取场景列表失败", e);
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "获取场景详情", description = "根据场景代码获取场景详细信息")
    @GetMapping("/scene/{sceneCode}")
    public Result<SceneInfoDTO> getSceneDetail(
            @Parameter(description = "场景代码") @PathVariable String sceneCode) {
        try {
            SceneInfoDTO scene = petBreedService.getSceneDetail(sceneCode);
            if (scene == null) {
                return Result.fail("场景不存在");
            }
            return Result.success(scene);
        } catch (Exception e) {
            log.error("获取场景详情失败: sceneCode={}", sceneCode, e);
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "解锁场景", description = "使用金币解锁指定场景")
    @PostMapping("/scene/unlock")
    public Result<SceneInfoDTO> unlockScene(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "宠物ID") @RequestParam Long petId,
            @Parameter(description = "场景代码") @RequestParam String sceneCode) {
        try {
            SceneInfoDTO scene = petBreedService.unlockScene(userId, petId, sceneCode);
            return Result.success(scene);
        } catch (Exception e) {
            log.error("解锁场景失败: userId={}, petId={}, sceneCode={}", userId, petId, sceneCode, e);
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "切换场景", description = "切换宠物到指定场景")
    @PostMapping("/scene/switch")
    public Result<PetSceneStateDTO> switchScene(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "宠物ID") @RequestParam Long petId,
            @Parameter(description = "场景代码") @RequestParam String sceneCode) {
        try {
            PetSceneStateDTO sceneState = petBreedService.switchScene(userId, petId, sceneCode);
            return Result.success(sceneState);
        } catch (Exception e) {
            log.error("切换场景失败: userId={}, petId={}, sceneCode={}", userId, petId, sceneCode, e);
            return Result.error(e.getMessage());
        }
    }

    // ==================== 统计相关接口 ====================

    @Operation(summary = "获取今日统计数据", description = "获取宠物今日的互动次数、经验等统计")
    @GetMapping("/stats/today")
    public Result<Map<String, Object>> getTodayStats(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "宠物ID") @RequestParam Long petId) {
        try {
            Map<String, Object> stats = petBreedService.getTodayStats(userId, petId);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取今日统计失败: userId={}, petId={}", userId, petId, e);
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "获取累计统计数据", description = "获取宠物累计的互动次数、经验等统计")
    @GetMapping("/stats/total")
    public Result<Map<String, Object>> getTotalStats(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "宠物ID") @RequestParam Long petId) {
        try {
            Map<String, Object> stats = petBreedService.getTotalStats(userId, petId);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取累计统计失败: userId={}, petId={}", userId, petId, e);
            return Result.error(e.getMessage());
        }
    }
}
