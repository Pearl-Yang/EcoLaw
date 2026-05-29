package com.lvfat.controller;

import com.lvfat.common.result.Result;
import com.lvfat.entity.Task;
import com.lvfat.entity.User;
import com.lvfat.repository.UserMapper;
import com.lvfat.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 任务管理控制器
 */
@Tag(name = "任务管理")
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    
    private final TaskService taskService;
    private final UserMapper userMapper;
    
    @Operation(summary = "获取任务列表")
    @GetMapping
    public Result<List<Task>> listTasks(
            @Parameter(description = "页码") @RequestParam(required = false) Integer page,
            @Parameter(description = "每页数量") @RequestParam(required = false) Integer pageSize,
            @Parameter(description = "类型") @RequestParam(required = false) String type,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "组织ID") @RequestParam(required = false) String organizationId,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "开始日期") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) String endDate
    ) {
        return Result.success(taskService.listTasks(page, pageSize, type, status, organizationId, keyword, startDate, endDate));
    }
    
    @Operation(summary = "获取任务详情")
    @GetMapping("/{id}")
    public Result<Task> getTask(@PathVariable String id) {
        return Result.success(taskService.getById(id));
    }
    
    @Operation(summary = "创建任务")
    @PostMapping
    public Result<Void> createTask(@RequestBody Task task) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userMapper.findByUsername(username);
        task.setCreatorId(user != null ? user.getId() : null);
        task.setStatus("pending");
        taskService.save(task);
        return Result.success("任务创建成功");
    }
    
    @Operation(summary = "更新任务")
    @PutMapping("/{id}")
    public Result<Void> updateTask(@PathVariable String id, @RequestBody Task task) {
        task.setId(id);
        taskService.updateById(task);
        return Result.success("任务更新成功");
    }
    
    @Operation(summary = "删除任务")
    @DeleteMapping("/{id}")
    public Result<Void> deleteTask(@PathVariable String id) {
        taskService.removeById(id);
        return Result.success("任务删除成功");
    }
    
    @Operation(summary = "派发任务")
    @PostMapping("/{id}/dispatch")
    public Result<Void> dispatchTask(
            @PathVariable String id,
            @RequestBody List<String> organizationIds
    ) {
        Task task = taskService.getById(id);
        if (task != null) {
            task.setStatus("in_progress");
            taskService.updateById(task);
        }
        return Result.success("任务已派发");
    }
    
    @Operation(summary = "完成任务")
    @PostMapping("/{id}/complete")
    public Result<Void> completeTask(
            @PathVariable String id,
            @RequestBody Map<String, Object> data
    ) {
        Task task = taskService.getById(id);
        if (task != null) {
            task.setStatus("completed");
            task.setCompletedCount(task.getTargetCount());
            taskService.updateById(task);
        }
        return Result.success("任务已完成");
    }
    
    @Operation(summary = "上报进度")
    @PostMapping("/{id}/progress")
    public Result<Void> reportProgress(
            @PathVariable String id,
            @RequestBody Map<String, Object> data
    ) {
        Task task = taskService.getById(id);
        if (task != null && data.containsKey("progress")) {
            Integer progress = (Integer) data.get("progress");
            task.setCompletedCount((int) (task.getTargetCount() * progress / 100.0));
            if (progress >= 100) {
                task.setStatus("completed");
            }
            taskService.updateById(task);
        }
        return Result.success("进度已上报");
    }
    
    @Operation(summary = "获取任务统计")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(
            @Parameter(description = "组织ID") @RequestParam(required = false) String organizationId,
            @Parameter(description = "开始日期") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) String endDate
    ) {
        return Result.success(taskService.getStatistics(organizationId, startDate, endDate));
    }
}
