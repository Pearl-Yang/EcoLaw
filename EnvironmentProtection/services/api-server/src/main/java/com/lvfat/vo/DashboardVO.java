package com.lvfat.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardVO {
    private Integer pendingTasks;
    private Integer completedTasks;
    private Integer pendingReports;
    private Integer totalTrainings;
    private Integer totalUsers;
    private List<Map<String, Object>> recentActivities;
}
