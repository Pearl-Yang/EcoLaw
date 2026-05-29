package com.lvfat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lvfat.entity.TaskDispatch;
import com.lvfat.entity.Organization;

import java.util.List;
import java.util.Map;

/**
 * 任务下发服务接口
 */
public interface TaskDispatchService extends IService<TaskDispatch> {
    
    /**
     * 创建并下发任务到组织
     */
    List<TaskDispatch> createAndDispatch(String taskId, List<String> targetOrganizationIds, String notes);
    
    /**
     * 向下级组织转发任务
     */
    List<TaskDispatch> delegateTask(String dispatchId, List<String> targetOrganizationIds, String notes);
    
    /**
     * 向指定职员下发任务
     */
    List<TaskDispatch> delegateToEmployees(String dispatchId, List<String> employeeIds, String notes);
    
    /**
     * 获取任务下发链路
     */
    List<TaskDispatch> getDispatchChain(String taskId);
    
    /**
     * 获取下发记录的子记录
     */
    List<TaskDispatch> getChildDispatches(String dispatchId);
    
    /**
     * 接收任务
     */
    boolean acceptDispatch(String dispatchId);
    
    /**
     * 拒绝任务
     */
    boolean rejectDispatch(String dispatchId, String reason);
    
    /**
     * 上报进度
     */
    boolean reportProgress(String dispatchId, Integer completedCount, String notes);
    
    /**
     * 完成任务
     */
    boolean completeDispatch(String dispatchId);
    
    /**
     * 获取下发统计
     */
    Map<String, Object> getDispatchStatistics(String organizationId, String taskId);
    
    /**
     * 获取待处理任务
     */
    List<TaskDispatch> getPendingDispatches(String organizationId, String userId);
    
    /**
     * 获取可下发目标组织列表
     */
    List<Organization> getDispatchableTargets(String organizationId);
    
    /**
     * 获取下发记录详情
     */
    TaskDispatch getDispatchDetail(String dispatchId);
    
    /**
     * 更新下发记录
     */
    void updateDispatch(TaskDispatch dispatch);
    
    /**
     * 删除下发记录
     */
    void deleteDispatch(String dispatchId);
    
    /**
     * 获取我下发的任务列表
     */
    List<TaskDispatch> getMyDispatchedTasks(String organizationId);
    
    /**
     * 获取下发给我的任务列表
     */
    List<TaskDispatch> getMyReceivedTasks(String organizationId, String userId);
}
