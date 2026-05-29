package com.lvfat.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.TaskDispatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 任务下发记录 Mapper
 */
@Mapper
public interface TaskDispatchMapper extends BaseMapper<TaskDispatch> {
    
    @Select("SELECT * FROM task_dispatch WHERE task_id = #{taskId} AND deleted = 0 ORDER BY dispatch_level, dispatch_time")
    List<TaskDispatch> findByTaskId(@Param("taskId") String taskId);
    
    @Select("SELECT * FROM task_dispatch WHERE parent_dispatch_id = #{parentDispatchId} AND deleted = 0 ORDER BY dispatch_time")
    List<TaskDispatch> findByParentDispatchId(@Param("parentDispatchId") String parentDispatchId);
    
    @Select("SELECT * FROM task_dispatch WHERE source_organization_id = #{organizationId} AND deleted = 0 ORDER BY dispatch_time DESC")
    List<TaskDispatch> findBySourceOrganizationId(@Param("organizationId") String organizationId);
    
    @Select("SELECT * FROM task_dispatch WHERE target_organization_id = #{organizationId} AND deleted = 0 ORDER BY dispatch_time DESC")
    List<TaskDispatch> findByTargetOrganizationId(@Param("organizationId") String organizationId);
    
    @Select("SELECT * FROM task_dispatch WHERE target_organization_id = #{organizationId} AND status = 'pending' AND deleted = 0 ORDER BY dispatch_time DESC")
    List<TaskDispatch> findPendingByTargetOrganizationId(@Param("organizationId") String organizationId);
    
    @Select("SELECT * FROM task_dispatch WHERE target_user_id = #{userId} AND deleted = 0 ORDER BY dispatch_time DESC")
    List<TaskDispatch> findByTargetUserId(@Param("userId") String userId);
    
    @Select("SELECT * FROM task_dispatch WHERE target_employee_id = #{employeeId} AND deleted = 0 ORDER BY dispatch_time DESC")
    List<TaskDispatch> findByTargetEmployeeId(@Param("employeeId") String employeeId);
    
    @Select("SELECT * FROM task_dispatch WHERE status = #{status} AND deleted = 0 ORDER BY dispatch_time DESC")
    List<TaskDispatch> findByStatus(@Param("status") String status);
    
    @Select("SELECT * FROM task_dispatch WHERE deleted = 0 ORDER BY dispatch_time DESC")
    List<TaskDispatch> findAll();
    
    @Select("SELECT COUNT(*) FROM task_dispatch WHERE source_organization_id = #{organizationId} AND deleted = 0")
    int countBySourceOrganizationId(@Param("organizationId") String organizationId);
    
    @Select("SELECT COUNT(*) FROM task_dispatch WHERE target_organization_id = #{organizationId} AND status = #{status} AND deleted = 0")
    int countByTargetOrganizationIdAndStatus(@Param("organizationId") String organizationId, @Param("status") String status);
}
