package com.lvfat.service;

import com.lvfat.entity.Task;

import java.util.List;
import java.util.Map;

public interface TaskService {
    List<Task> listTasks(Integer page, Integer pageSize, String type, String status, String organizationId, String keyword, String startDate, String endDate);
    Task getById(String id);
    void save(Task task);
    void update(Task task);
    void updateById(Task task);
    void removeById(String id);
    void complete(String id);
    void claim(String id);
    Map<String, Object> getStatistics(String organizationId, String startDate, String endDate);
}
