package com.lvfat.service;

import com.lvfat.vo.DashboardVO;

import java.util.Map;

public interface DashboardService {
    DashboardVO getDashboardData();
    Map<String, Object> getOverview(String range);
    Map<String, Object> getTrends(String range);
    int getPendingCount();
    Map<String, Object> getNews();
}
