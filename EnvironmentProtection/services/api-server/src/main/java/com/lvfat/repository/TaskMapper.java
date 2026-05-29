package com.lvfat.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 任务 Mapper
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task> {
    
    @Select("SELECT status, COUNT(*) as count FROM task WHERE deleted = 0 GROUP BY status")
    List<Map<String, Object>> countByStatus();
    
    @Select("SELECT COUNT(*) FROM task WHERE deleted = 0")
    int countAll();
    
    @Select("SELECT COUNT(*) FROM task WHERE status = 'overdue' AND deleted = 0")
    int countOverdue();
}
