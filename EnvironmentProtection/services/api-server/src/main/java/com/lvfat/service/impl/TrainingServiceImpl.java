package com.lvfat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lvfat.entity.Training;
import com.lvfat.entity.TrainingRecord;
import com.lvfat.entity.User;
import com.lvfat.repository.TrainingMapper;
import com.lvfat.repository.TrainingRecordMapper;
import com.lvfat.repository.UserMapper;
import com.lvfat.service.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 培训服务实现
 */
@Service
@RequiredArgsConstructor
public class TrainingServiceImpl extends ServiceImpl<TrainingMapper, Training> implements TrainingService {
    
    private final TrainingRecordMapper trainingRecordMapper;
    private final UserMapper userMapper;
    
    @Override
    public List<Training> listTrainings(Integer page, Integer pageSize, String type, String status,
                                       String organizationId, String keyword) {
        QueryWrapper<Training> wrapper = new QueryWrapper<>();
        
        if (StringUtils.hasText(type)) {
            wrapper.eq("type", type);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq("status", status);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like("title", keyword);
        }
        
        wrapper.orderByDesc("create_time");
        
        if (page != null && pageSize != null) {
            wrapper.last("LIMIT " + ((page - 1) * pageSize) + ", " + pageSize);
        }
        
        return list(wrapper);
    }
    
    @Override
    public Map<String, Object> getStatistics(String organizationId) {
        Map<String, Object> result = new HashMap<>();
        result.put("total", count());
        
        QueryWrapper<TrainingRecord> recordWrapper = new QueryWrapper<>();
        recordWrapper.eq("status", "completed");
        result.put("completed", trainingRecordMapper.selectCount(recordWrapper));
        
        QueryWrapper<TrainingRecord> inProgressWrapper = new QueryWrapper<>();
        inProgressWrapper.eq("status", "in_progress");
        result.put("inProgress", trainingRecordMapper.selectCount(inProgressWrapper));
        
        Double avgScore = trainingRecordMapper.selectList(recordWrapper).stream()
                .mapToInt(r -> r.getScore() != null ? r.getScore() : 0)
                .average()
                .orElse(0.0);
        result.put("avgScore", avgScore);
        
        return result;
    }
    
    @Override
    public List<TrainingRecord> listRecords(Integer page, Integer pageSize, String trainingId, String userId) {
        QueryWrapper<TrainingRecord> wrapper = new QueryWrapper<>();
        
        if (StringUtils.hasText(trainingId)) {
            wrapper.eq("training_id", trainingId);
        }
        if (StringUtils.hasText(userId)) {
            wrapper.eq("user_id", userId);
        }
        
        wrapper.orderByDesc("create_time");
        
        if (page != null && pageSize != null) {
            wrapper.last("LIMIT " + ((page - 1) * pageSize) + ", " + pageSize);
        }
        
        return trainingRecordMapper.selectList(wrapper);
    }
    
    @Override
    public void createTraining(Training training) {
        training.setStatus("published");
        save(training);
    }
    
    @Override
    public void dispatchTraining(String trainingId, List<String> organizationIds) {
        List<User> users = userMapper.selectList(
            new QueryWrapper<User>().in("organization_id", organizationIds)
        );
        
        for (User user : users) {
            TrainingRecord record = new TrainingRecord();
            record.setTrainingId(trainingId);
            record.setUserId(user.getId());
            record.setStatus("not_started");
            trainingRecordMapper.insert(record);
        }
    }
}
