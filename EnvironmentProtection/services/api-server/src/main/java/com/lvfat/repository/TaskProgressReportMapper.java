package com.lvfat.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.TaskProgressReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 任务进度上报记录 Mapper
 */
@Mapper
public interface TaskProgressReportMapper extends BaseMapper<TaskProgressReport> {
    
    @Select("SELECT * FROM task_progress_report WHERE dispatch_id = #{dispatchId} AND deleted = 0 ORDER BY create_time DESC")
    List<TaskProgressReport> findByDispatchId(@Param("dispatchId") String dispatchId);
    
    @Select("SELECT * FROM task_progress_report WHERE reporter_id = #{reporterId} AND deleted = 0 ORDER BY create_time DESC")
    List<TaskProgressReport> findByReporterId(@Param("reporterId") String reporterId);
    
    @Select("SELECT * FROM task_progress_report WHERE deleted = 0 ORDER BY create_time DESC")
    List<TaskProgressReport> findAll();
}
