package com.lvfat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lvfat.entity.Task;
import com.lvfat.repository.TaskMapper;
import com.lvfat.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskMapper taskMapper;

    @Override
    public List<Task> listTasks(Integer page, Integer pageSize, String type, String status, String organizationId, String keyword, String startDate, String endDate) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        if (type != null && !type.isEmpty()) {
            wrapper.eq(Task::getType, type);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Task::getStatus, status);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Task::getTitle, keyword).or().like(Task::getContent, keyword));
        }
        if (organizationId != null && !organizationId.isEmpty()) {
            wrapper.eq(Task::getOrganizationId, organizationId);
        }
        wrapper.orderByDesc(Task::getCreateTime);
        
        if (page != null && pageSize != null) {
            IPage<Task> result = taskMapper.selectPage(new Page<>(page, pageSize), wrapper);
            return result.getRecords();
        }
        return taskMapper.selectList(wrapper);
    }

    @Override
    public Task getById(String id) {
        return taskMapper.selectById(id);
    }

    @Override
    public void save(Task task) {
        if (task.getStatus() == null) {
            task.setStatus("pending");
        }
        if (task.getCompletedCount() == null) {
            task.setCompletedCount(0);
        }
        if (task.getTargetCount() == null) {
            task.setTargetCount(0);
        }
        taskMapper.insert(task);
    }

    @Override
    public void update(Task task) {
        taskMapper.updateById(task);
    }

    @Override
    public void updateById(Task task) {
        taskMapper.updateById(task);
    }

    @Override
    public void removeById(String id) {
        taskMapper.deleteById(id);
    }

    @Override
    public void complete(String id) {
        Task task = taskMapper.selectById(id);
        if (task != null) {
            task.setStatus("completed");
            taskMapper.updateById(task);
        }
    }

    @Override
    public void claim(String id) {
        Task task = taskMapper.selectById(id);
        if (task != null) {
            task.setStatus("in_progress");
            taskMapper.updateById(task);
        }
    }

    @Override
    public Map<String, Object> getStatistics(String organizationId, String startDate, String endDate) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        if (organizationId != null && !organizationId.isEmpty()) {
            wrapper.eq(Task::getOrganizationId, organizationId);
        }
        
        List<Task> allTasks = taskMapper.selectList(wrapper);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", allTasks.size());
        stats.put("pending", allTasks.stream().filter(t -> "pending".equals(t.getStatus())).count());
        stats.put("in_progress", allTasks.stream().filter(t -> "in_progress".equals(t.getStatus())).count());
        stats.put("completed", allTasks.stream().filter(t -> "completed".equals(t.getStatus())).count());
        stats.put("overdue", allTasks.stream().filter(t -> "overdue".equals(t.getStatus())).count());
        
        return stats;
    }
}
