package com.lvfat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lvfat.entity.Report;
import com.lvfat.entity.User;
import com.lvfat.repository.ReportMapper;
import com.lvfat.repository.UserMapper;
import com.lvfat.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 举报服务实现
 */
@Service
@RequiredArgsConstructor
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report> implements ReportService {
    
    private final UserMapper userMapper;
    
    @Override
    public List<Report> listReports(Integer page, Integer pageSize, String type, String status,
                                   String keyword, String startDate, String endDate) {
        QueryWrapper<Report> wrapper = new QueryWrapper<>();
        
        if (StringUtils.hasText(type)) {
            wrapper.eq("type", type);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq("status", status);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like("title", keyword).or().like("content", keyword));
        }
        if (StringUtils.hasText(startDate)) {
            wrapper.ge("create_time", startDate);
        }
        if (StringUtils.hasText(endDate)) {
            wrapper.le("create_time", endDate);
        }
        
        wrapper.orderByDesc("create_time");
        
        if (page != null && pageSize != null) {
            wrapper.last("LIMIT " + ((page - 1) * pageSize) + ", " + pageSize);
        }
        
        return list(wrapper);
    }
    
    @Override
    public void handleReport(String id, String status, String result) {
        Report report = getById(id);
        if (report == null) {
            throw new RuntimeException("举报不存在");
        }
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User handler = userMapper.findByUsername(username);
        
        report.setStatus(status);
        report.setHandleResult(result);
        report.setHandlerId(handler != null ? handler.getId() : null);
        report.setHandlerName(handler != null ? handler.getNickname() : null);
        report.setHandleTime(LocalDateTime.now());
        
        updateById(report);
    }
    
    @Override
    public Map<String, Object> getStatistics(String startDate, String endDate) {
        QueryWrapper<Report> wrapper = new QueryWrapper<>();
        
        if (StringUtils.hasText(startDate)) {
            wrapper.ge("create_time", startDate);
        }
        if (StringUtils.hasText(endDate)) {
            wrapper.le("create_time", endDate);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", (int) count(wrapper));
        
        QueryWrapper<Report> pendingWrapper = wrapper.clone();
        pendingWrapper.eq("status", "pending");
        result.put("pending", (int) count(pendingWrapper));
        
        QueryWrapper<Report> processingWrapper = wrapper.clone();
        processingWrapper.eq("status", "processing");
        result.put("processing", (int) count(processingWrapper));
        
        QueryWrapper<Report> resolvedWrapper = wrapper.clone();
        resolvedWrapper.eq("status", "resolved");
        result.put("resolved", (int) count(resolvedWrapper));
        
        QueryWrapper<Report> rejectedWrapper = wrapper.clone();
        rejectedWrapper.eq("status", "rejected");
        result.put("rejected", (int) count(rejectedWrapper));
        
        result.put("byType", reportMapper().countByType());
        
        return result;
    }
    
    @Override
    public void submitReport(Report report) {
        report.setStatus("pending");
        save(report);
    }
    
    private ReportMapper reportMapper() {
        return (ReportMapper) getBaseMapper();
    }
}
