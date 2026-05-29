package com.lvfat.controller;

import com.lvfat.common.result.Result;
import com.lvfat.entity.Training;
import com.lvfat.entity.TrainingRecord;
import com.lvfat.entity.User;
import com.lvfat.repository.UserMapper;
import com.lvfat.service.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 培训管理控制器
 */
@Tag(name = "培训管理")
@RestController
@RequestMapping("/trainings")
@RequiredArgsConstructor
public class TrainingController {
    
    private final TrainingService trainingService;
    private final UserMapper userMapper;
    
    @Operation(summary = "获取培训列表")
    @GetMapping
    public Result<List<Training>> listTrainings(
            @Parameter(description = "页码") @RequestParam(required = false) Integer page,
            @Parameter(description = "每页数量") @RequestParam(required = false) Integer pageSize,
            @Parameter(description = "类型") @RequestParam(required = false) String type,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "组织ID") @RequestParam(required = false) String organizationId,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword
    ) {
        return Result.success(trainingService.listTrainings(page, pageSize, type, status, organizationId, keyword));
    }
    
    @Operation(summary = "获取培训详情")
    @GetMapping("/{id}")
    public Result<Training> getTraining(@PathVariable String id) {
        return Result.success(trainingService.getById(id));
    }
    
    @Operation(summary = "创建培训")
    @PostMapping
    public Result<Void> createTraining(@RequestBody Training training) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userMapper.findByUsername(username);
        training.setCreatorId(user != null ? user.getId() : null);
        trainingService.createTraining(training);
        return Result.success("培训创建成功");
    }
    
    @Operation(summary = "更新培训")
    @PutMapping("/{id}")
    public Result<Void> updateTraining(@PathVariable String id, @RequestBody Training training) {
        training.setId(id);
        trainingService.updateById(training);
        return Result.success("培训更新成功");
    }
    
    @Operation(summary = "删除培训")
    @DeleteMapping("/{id}")
    public Result<Void> deleteTraining(@PathVariable String id) {
        trainingService.removeById(id);
        return Result.success("培训删除成功");
    }
    
    @Operation(summary = "派发培训")
    @PostMapping("/{id}/dispatch")
    public Result<Void> dispatchTraining(
            @PathVariable String id,
            @RequestBody List<String> organizationIds
    ) {
        trainingService.dispatchTraining(id, organizationIds);
        return Result.success("培训已派发");
    }
    
    @Operation(summary = "获取培训统计")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(
            @Parameter(description = "组织ID") @RequestParam(required = false) String organizationId
    ) {
        return Result.success(trainingService.getStatistics(organizationId));
    }
    
    @Operation(summary = "获取培训记录")
    @GetMapping("/records")
    public Result<List<TrainingRecord>> listRecords(
            @Parameter(description = "页码") @RequestParam(required = false) Integer page,
            @Parameter(description = "每页数量") @RequestParam(required = false) Integer pageSize,
            @Parameter(description = "培训ID") @RequestParam(required = false) String trainingId,
            @Parameter(description = "用户ID") @RequestParam(required = false) String userId
    ) {
        return Result.success(trainingService.listRecords(page, pageSize, trainingId, userId));
    }
}
