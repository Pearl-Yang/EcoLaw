package com.lvfat.controller;

import com.lvfat.common.result.Result;
import com.lvfat.entity.TaskDispatch;
import com.lvfat.entity.Organization;
import com.lvfat.service.TaskDispatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "任务下发管理")
@RestController
@RequestMapping("/task-dispatches")
@RequiredArgsConstructor
public class TaskDispatchController {
    
    private final TaskDispatchService taskDispatchService;
    
    @Operation(summary = "创建并下发任务到组织")
    @PostMapping("/dispatch-to-orgs")
    public Result<List<TaskDispatch>> dispatchToOrganizations(
            @RequestParam String taskId,
            @RequestBody List<String> organizationIds
    ) {
        return Result.success(taskDispatchService.createAndDispatch(taskId, organizationIds, null));
    }
    
    @Operation(summary = "向下级组织转发任务")
    @PostMapping("/{dispatchId}/delegate")
    public Result<List<TaskDispatch>> delegateTask(
            @PathVariable String dispatchId,
            @RequestBody List<String> organizationIds
    ) {
        return Result.success(taskDispatchService.delegateTask(dispatchId, organizationIds, null));
    }
    
    @Operation(summary = "向指定职员下发任务")
    @PostMapping("/{dispatchId}/delegate-to-employees")
    public Result<List<TaskDispatch>> delegateToEmployees(
            @PathVariable String dispatchId,
            @RequestBody List<String> employeeIds
    ) {
        return Result.success(taskDispatchService.delegateToEmployees(dispatchId, employeeIds, null));
    }
    
    @Operation(summary = "获取任务下发链路")
    @GetMapping("/chain/{taskId}")
    public Result<List<TaskDispatch>> getDispatchChain(@PathVariable String taskId) {
        return Result.success(taskDispatchService.getDispatchChain(taskId));
    }
    
    @Operation(summary = "获取下发记录的子记录")
    @GetMapping("/{dispatchId}/children")
    public Result<List<TaskDispatch>> getChildDispatches(@PathVariable String dispatchId) {
        return Result.success(taskDispatchService.getChildDispatches(dispatchId));
    }
    
    @Operation(summary = "获取下发记录详情")
    @GetMapping("/{dispatchId}")
    public Result<TaskDispatch> getDispatchDetail(@PathVariable String dispatchId) {
        return Result.success(taskDispatchService.getDispatchDetail(dispatchId));
    }
    
    @Operation(summary = "接收任务")
    @PostMapping("/{dispatchId}/accept")
    public Result<Void> acceptDispatch(@PathVariable String dispatchId) {
        taskDispatchService.acceptDispatch(dispatchId);
        return Result.success("任务已接收");
    }
    
    @Operation(summary = "拒绝任务")
    @PostMapping("/{dispatchId}/reject")
    public Result<Void> rejectDispatch(
            @PathVariable String dispatchId,
            @RequestParam String reason
    ) {
        taskDispatchService.rejectDispatch(dispatchId, reason);
        return Result.success("任务已拒绝");
    }
    
    @Operation(summary = "上报进度")
    @PostMapping("/{dispatchId}/progress")
    public Result<Void> reportProgress(
            @PathVariable String dispatchId,
            @RequestParam Integer completedCount,
            @RequestParam(required = false) String notes
    ) {
        taskDispatchService.reportProgress(dispatchId, completedCount, notes);
        return Result.success("进度已上报");
    }
    
    @Operation(summary = "完成任务")
    @PostMapping("/{dispatchId}/complete")
    public Result<Void> completeDispatch(@PathVariable String dispatchId) {
        taskDispatchService.completeDispatch(dispatchId);
        return Result.success("任务已完成");
    }
    
    @Operation(summary = "获取下发统计")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(
            @RequestParam String organizationId,
            @RequestParam(required = false) String taskId
    ) {
        return Result.success(taskDispatchService.getDispatchStatistics(organizationId, taskId));
    }
    
    @Operation(summary = "获取待处理任务")
    @GetMapping("/pending")
    public Result<List<TaskDispatch>> getPendingDispatches(
            @RequestParam String organizationId,
            @RequestParam(required = false) String userId
    ) {
        return Result.success(taskDispatchService.getPendingDispatches(organizationId, userId));
    }
    
    @Operation(summary = "获取可下发目标组织列表")
    @GetMapping("/targets")
    public Result<List<Organization>> getDispatchableTargets(@RequestParam String organizationId) {
        return Result.success(taskDispatchService.getDispatchableTargets(organizationId));
    }
    
    @Operation(summary = "更新下发记录")
    @PutMapping("/{dispatchId}")
    public Result<Void> updateDispatch(
            @PathVariable String dispatchId,
            @RequestBody TaskDispatch dispatch
    ) {
        dispatch.setId(dispatchId);
        taskDispatchService.updateDispatch(dispatch);
        return Result.success("更新成功");
    }
    
    @Operation(summary = "删除下发记录")
    @DeleteMapping("/{dispatchId}")
    public Result<Void> deleteDispatch(@PathVariable String dispatchId) {
        taskDispatchService.deleteDispatch(dispatchId);
        return Result.success("删除成功");
    }
    
    @Operation(summary = "获取我下发的任务列表")
    @GetMapping("/dispatched")
    public Result<List<TaskDispatch>> getMyDispatchedTasks(@RequestParam String organizationId) {
        return Result.success(taskDispatchService.getMyDispatchedTasks(organizationId));
    }
    
    @Operation(summary = "获取下发给我的任务列表")
    @GetMapping("/received")
    public Result<List<TaskDispatch>> getMyReceivedTasks(
            @RequestParam String organizationId,
            @RequestParam(required = false) String userId
    ) {
        return Result.success(taskDispatchService.getMyReceivedTasks(organizationId, userId));
    }
}
