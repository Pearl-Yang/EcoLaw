package com.lvfat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lvfat.entity.*;
import com.lvfat.repository.*;
import com.lvfat.service.TaskDispatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskDispatchServiceImpl extends ServiceImpl<TaskDispatchMapper, TaskDispatch> implements TaskDispatchService {

    private final TaskDispatchMapper taskDispatchMapper;
    private final OrganizationMapper organizationMapper;
    private final TaskMapper taskMapper;
    private final EmployeeMapper employeeMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public List<TaskDispatch> createAndDispatch(String taskId, List<String> targetOrganizationIds, String notes) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        
        Organization sourceOrg = organizationMapper.selectById(task.getOrganizationId());
        if (sourceOrg == null) {
            throw new RuntimeException("下发方组织不存在");
        }
        
        List<TaskDispatch> dispatches = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (String targetOrgId : targetOrganizationIds) {
            TaskDispatch dispatch = new TaskDispatch();
            dispatch.setTaskId(taskId);
            dispatch.setSourceOrganizationId(task.getOrganizationId());
            dispatch.setTargetOrganizationId(targetOrgId);
            dispatch.setDispatchLevel(1);
            dispatch.setStatus("pending");
            dispatch.setTargetCount(task.getTargetCount());
            dispatch.setCompletedCount(0);
            dispatch.setProgress(0);
            dispatch.setDeadline(task.getEndTime());
            dispatch.setNotes(notes);
            dispatch.setDispatchTime(now);
            taskDispatchMapper.insert(dispatch);
            dispatches.add(dispatch);
        }
        
        return dispatches;
    }

    @Override
    @Transactional
    public List<TaskDispatch> delegateTask(String dispatchId, List<String> targetOrganizationIds, String notes) {
        TaskDispatch parentDispatch = taskDispatchMapper.selectById(dispatchId);
        if (parentDispatch == null) {
            throw new RuntimeException("下发记录不存在");
        }
        
        List<TaskDispatch> dispatches = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (String targetOrgId : targetOrganizationIds) {
            TaskDispatch dispatch = new TaskDispatch();
            dispatch.setTaskId(parentDispatch.getTaskId());
            dispatch.setParentDispatchId(dispatchId);
            dispatch.setSourceOrganizationId(parentDispatch.getTargetOrganizationId());
            dispatch.setTargetOrganizationId(targetOrgId);
            dispatch.setDispatchLevel(parentDispatch.getDispatchLevel() + 1);
            dispatch.setStatus("pending");
            dispatch.setTargetCount(parentDispatch.getTargetCount());
            dispatch.setCompletedCount(0);
            dispatch.setProgress(0);
            dispatch.setDeadline(parentDispatch.getDeadline());
            dispatch.setNotes(notes);
            dispatch.setDispatchTime(now);
            taskDispatchMapper.insert(dispatch);
            dispatches.add(dispatch);
        }
        
        // 更新父记录状态为已转发
        parentDispatch.setStatus("delegated");
        taskDispatchMapper.updateById(parentDispatch);
        
        return dispatches;
    }

    @Override
    @Transactional
    public List<TaskDispatch> delegateToEmployees(String dispatchId, List<String> employeeIds, String notes) {
        TaskDispatch parentDispatch = taskDispatchMapper.selectById(dispatchId);
        if (parentDispatch == null) {
            throw new RuntimeException("下发记录不存在");
        }
        
        List<TaskDispatch> dispatches = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (String employeeId : employeeIds) {
            Employee employee = employeeMapper.selectById(employeeId);
            if (employee == null) {
                continue;
            }
            
            TaskDispatch dispatch = new TaskDispatch();
            dispatch.setTaskId(parentDispatch.getTaskId());
            dispatch.setParentDispatchId(dispatchId);
            dispatch.setSourceOrganizationId(parentDispatch.getTargetOrganizationId());
            dispatch.setTargetOrganizationId(employee.getOrganizationId());
            dispatch.setTargetEmployeeId(employeeId);
            dispatch.setDispatchLevel(parentDispatch.getDispatchLevel() + 1);
            dispatch.setStatus("pending");
            dispatch.setTargetCount(1);
            dispatch.setCompletedCount(0);
            dispatch.setProgress(0);
            dispatch.setDeadline(parentDispatch.getDeadline());
            dispatch.setNotes(notes);
            dispatch.setDispatchTime(now);
            taskDispatchMapper.insert(dispatch);
            dispatches.add(dispatch);
        }
        
        return dispatches;
    }

    @Override
    public List<TaskDispatch> getDispatchChain(String taskId) {
        List<TaskDispatch> allDispatches = taskDispatchMapper.findByTaskId(taskId);
        return buildDispatchTree(allDispatches, null);
    }

    private List<TaskDispatch> buildDispatchTree(List<TaskDispatch> all, String parentId) {
        return all.stream()
                .filter(d -> (parentId == null && d.getParentDispatchId() == null) 
                        || (parentId != null && parentId.equals(d.getParentDispatchId())))
                .peek(d -> {
                    List<TaskDispatch> children = buildDispatchTree(all, d.getId());
                    if (!children.isEmpty()) {
                        d.setChildren(children);
                    }
                    // 填充关联信息
                    fillDispatchInfo(d);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDispatch> getChildDispatches(String dispatchId) {
        List<TaskDispatch> children = taskDispatchMapper.findByParentDispatchId(dispatchId);
        children.forEach(this::fillDispatchInfo);
        return children;
    }

    @Override
    @Transactional
    public boolean acceptDispatch(String dispatchId) {
        TaskDispatch dispatch = taskDispatchMapper.selectById(dispatchId);
        if (dispatch == null) {
            return false;
        }
        dispatch.setStatus("accepted");
        dispatch.setAcceptTime(LocalDateTime.now());
        taskDispatchMapper.updateById(dispatch);
        return true;
    }

    @Override
    @Transactional
    public boolean rejectDispatch(String dispatchId, String reason) {
        TaskDispatch dispatch = taskDispatchMapper.selectById(dispatchId);
        if (dispatch == null) {
            return false;
        }
        dispatch.setStatus("rejected");
        dispatch.setRejectReason(reason);
        taskDispatchMapper.updateById(dispatch);
        return true;
    }

    @Override
    @Transactional
    public boolean reportProgress(String dispatchId, Integer completedCount, String notes) {
        TaskDispatch dispatch = taskDispatchMapper.selectById(dispatchId);
        if (dispatch == null) {
            return false;
        }
        
        dispatch.setCompletedCount(completedCount);
        if (dispatch.getTargetCount() != null && dispatch.getTargetCount() > 0) {
            int progress = (int) (completedCount * 100.0 / dispatch.getTargetCount());
            dispatch.setProgress(Math.min(progress, 100));
        }
        
        if (notes != null) {
            dispatch.setNotes(notes);
        }
        
        // 如果进度达到100%，自动完成
        if (dispatch.getProgress() >= 100) {
            dispatch.setStatus("completed");
            dispatch.setCompleteTime(LocalDateTime.now());
        }
        
        taskDispatchMapper.updateById(dispatch);
        return true;
    }

    @Override
    @Transactional
    public boolean completeDispatch(String dispatchId) {
        TaskDispatch dispatch = taskDispatchMapper.selectById(dispatchId);
        if (dispatch == null) {
            return false;
        }
        dispatch.setStatus("completed");
        dispatch.setCompletedCount(dispatch.getTargetCount());
        dispatch.setProgress(100);
        dispatch.setCompleteTime(LocalDateTime.now());
        taskDispatchMapper.updateById(dispatch);
        return true;
    }

    @Override
    public Map<String, Object> getDispatchStatistics(String organizationId, String taskId) {
        Map<String, Object> statistics = new HashMap<>();
        
        LambdaQueryWrapper<TaskDispatch> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TaskDispatch::getSourceOrganizationId, organizationId);
        if (StringUtils.hasText(taskId)) {
            wrapper.eq(TaskDispatch::getTaskId, taskId);
        }
        
        List<TaskDispatch> dispatches = taskDispatchMapper.selectList(wrapper);
        
        statistics.put("totalDispatches", dispatches.size());
        statistics.put("totalTarget", dispatches.stream().mapToInt(TaskDispatch::getTargetCount).sum());
        statistics.put("totalCompleted", dispatches.stream().mapToInt(TaskDispatch::getCompletedCount).sum());
        statistics.put("pendingCount", dispatches.stream().filter(d -> "pending".equals(d.getStatus())).count());
        statistics.put("acceptedCount", dispatches.stream().filter(d -> "accepted".equals(d.getStatus())).count());
        statistics.put("inProgressCount", dispatches.stream().filter(d -> "in_progress".equals(d.getStatus())).count());
        statistics.put("completedCount", dispatches.stream().filter(d -> "completed".equals(d.getStatus())).count());
        statistics.put("rejectedCount", dispatches.stream().filter(d -> "rejected".equals(d.getStatus())).count());
        
        return statistics;
    }

    @Override
    public List<TaskDispatch> getPendingDispatches(String organizationId, String userId) {
        List<TaskDispatch> dispatches;
        if (StringUtils.hasText(userId)) {
            dispatches = taskDispatchMapper.findByTargetUserId(userId);
        } else {
            dispatches = taskDispatchMapper.findPendingByTargetOrganizationId(organizationId);
        }
        dispatches.forEach(this::fillDispatchInfo);
        return dispatches;
    }

    @Override
    public List<Organization> getDispatchableTargets(String organizationId) {
        return organizationMapper.findByParentId(organizationId);
    }

    @Override
    public TaskDispatch getDispatchDetail(String dispatchId) {
        TaskDispatch dispatch = taskDispatchMapper.selectById(dispatchId);
        if (dispatch != null) {
            fillDispatchInfo(dispatch);
        }
        return dispatch;
    }

    @Override
    public void updateDispatch(TaskDispatch dispatch) {
        taskDispatchMapper.updateById(dispatch);
    }

    @Override
    public void deleteDispatch(String dispatchId) {
        taskDispatchMapper.deleteById(dispatchId);
    }

    @Override
    public List<TaskDispatch> getMyDispatchedTasks(String organizationId) {
        List<TaskDispatch> dispatches = taskDispatchMapper.findBySourceOrganizationId(organizationId);
        dispatches.forEach(this::fillDispatchInfo);
        return dispatches;
    }

    @Override
    public List<TaskDispatch> getMyReceivedTasks(String organizationId, String userId) {
        List<TaskDispatch> dispatches = taskDispatchMapper.findByTargetOrganizationId(organizationId);
        dispatches.forEach(this::fillDispatchInfo);
        return dispatches;
    }

    private void fillDispatchInfo(TaskDispatch dispatch) {
        // 填充任务信息
        if (dispatch.getTaskId() != null) {
            Task task = taskMapper.selectById(dispatch.getTaskId());
            if (task != null) {
                dispatch.setTaskTitle(task.getTitle());
            }
        }
        
        // 填充下发方组织信息
        if (dispatch.getSourceOrganizationId() != null) {
            Organization sourceOrg = organizationMapper.selectById(dispatch.getSourceOrganizationId());
            if (sourceOrg != null) {
                dispatch.setSourceOrgName(sourceOrg.getName());
            }
        }
        
        // 填充接收方组织信息
        if (dispatch.getTargetOrganizationId() != null) {
            Organization targetOrg = organizationMapper.selectById(dispatch.getTargetOrganizationId());
            if (targetOrg != null) {
                dispatch.setTargetOrgName(targetOrg.getName());
            }
        }
        
        // 填充接收方职员信息
        if (dispatch.getTargetEmployeeId() != null) {
            Employee employee = employeeMapper.selectById(dispatch.getTargetEmployeeId());
            if (employee != null) {
                dispatch.setTargetEmployeeName(employee.getName());
            }
        }
    }
}
