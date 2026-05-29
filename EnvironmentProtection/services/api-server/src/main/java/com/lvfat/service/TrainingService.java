package com.lvfat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lvfat.entity.Training;
import com.lvfat.entity.TrainingRecord;

import java.util.List;
import java.util.Map;

/**
 * 培训服务接口
 */
public interface TrainingService extends IService<Training> {
    
    /**
     * 分页查询培训
     */
    List<Training> listTrainings(Integer page, Integer pageSize, String type, String status,
                                 String organizationId, String keyword);
    
    /**
     * 获取培训统计
     */
    Map<String, Object> getStatistics(String organizationId);
    
    /**
     * 获取培训记录
     */
    List<TrainingRecord> listRecords(Integer page, Integer pageSize, String trainingId, String userId);
    
    /**
     * 创建培训
     */
    void createTraining(Training training);
    
    /**
     * 派发培训
     */
    void dispatchTraining(String trainingId, List<String> organizationIds);
}
