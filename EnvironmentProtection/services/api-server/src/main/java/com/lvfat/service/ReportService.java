package com.lvfat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lvfat.entity.Report;

import java.util.List;
import java.util.Map;

/**
 * 举报服务接口
 */
public interface ReportService extends IService<Report> {
    
    /**
     * 分页查询举报
     */
    List<Report> listReports(Integer page, Integer pageSize, String type, String status, 
                            String keyword, String startDate, String endDate);
    
    /**
     * 处理举报
     */
    void handleReport(String id, String status, String result);
    
    /**
     * 获取举报统计
     */
    Map<String, Object> getStatistics(String startDate, String endDate);
    
    /**
     * 提交举报
     */
    void submitReport(Report report);
}
