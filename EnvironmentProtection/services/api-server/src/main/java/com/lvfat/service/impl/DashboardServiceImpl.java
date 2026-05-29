package com.lvfat.service.impl;

import com.lvfat.entity.Report;
import com.lvfat.entity.Task;
import com.lvfat.entity.Training;
import com.lvfat.entity.User;
import com.lvfat.repository.ReportMapper;
import com.lvfat.repository.TaskMapper;
import com.lvfat.repository.TrainingMapper;
import com.lvfat.repository.UserMapper;
import com.lvfat.service.DashboardService;
import com.lvfat.vo.DashboardVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final TaskMapper taskMapper;
    private final ReportMapper reportMapper;
    private final TrainingMapper trainingMapper;
    private final UserMapper userMapper;

    @Override
    public DashboardVO getDashboardData() {
        List<Task> tasks = taskMapper.selectList(null);
        List<Report> reports = reportMapper.selectList(null);
        List<Training> trainings = trainingMapper.selectList(null);
        List<User> users = userMapper.selectList(null);

        int pendingTasks = (int) tasks.stream().filter(t -> "pending".equals(t.getStatus()) || "in_progress".equals(t.getStatus())).count();
        int completedTasks = (int) tasks.stream().filter(t -> "completed".equals(t.getStatus())).count();
        int pendingReports = (int) reports.stream().filter(r -> "pending".equals(r.getStatus())).count();
        int totalTrainings = trainings.size();
        int totalUsers = users.size();

        return DashboardVO.builder()
                .pendingTasks(pendingTasks)
                .completedTasks(completedTasks)
                .pendingReports(pendingReports)
                .totalTrainings(totalTrainings)
                .totalUsers(totalUsers)
                .recentActivities(generateRecentActivities())
                .build();
    }

    private List<Map<String, Object>> generateRecentActivities() {
        List<Map<String, Object>> activities = new ArrayList<>();
        activities.add(createActivity("系统", "企业「环保科技」完成了员工合规培训", "5 分钟前"));
        activities.add(createActivity("管理员", "新增法规《塑料污染治理条例》已入库", "30 分钟前"));
        activities.add(createActivity("系统", "律所「正信律所」提交了入驻申请", "1 小时前"));
        activities.add(createActivity("系统", "法律明白人「张三」完成了本月普法任务", "2 小时前"));
        return activities;
    }

    private Map<String, Object> createActivity(String user, String content, String time) {
        Map<String, Object> activity = new HashMap<>();
        activity.put("id", UUID.randomUUID().toString());
        activity.put("user", user);
        activity.put("content", content);
        activity.put("time", time);
        return activity;
    }

    @Override
    public Map<String, Object> getOverview(String range) {
        int days = "30d".equals(range) ? 30 : 7;
        
        List<Task> tasks = taskMapper.selectList(null);
        List<Report> reports = reportMapper.selectList(null);
        
        Map<String, Object> overview = new HashMap<>();
        overview.put("totalTasks", tasks.size());
        overview.put("pendingTasks", tasks.stream().filter(t -> "pending".equals(t.getStatus())).count());
        overview.put("inProgressTasks", tasks.stream().filter(t -> "in_progress".equals(t.getStatus())).count());
        overview.put("completedTasks", tasks.stream().filter(t -> "completed".equals(t.getStatus())).count());
        overview.put("totalReports", reports.size());
        overview.put("pendingReports", reports.stream().filter(r -> "pending".equals(r.getStatus())).count());
        overview.put("resolvedReports", reports.stream().filter(r -> "resolved".equals(r.getStatus())).count());
        
        return overview;
    }

    @Override
    public Map<String, Object> getTrends(String range) {
        int days = "30d".equals(range) ? 30 : 7;
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        List<String> dates = new ArrayList<>();
        List<Integer> reachData = new ArrayList<>();
        List<Integer> completeData = new ArrayList<>();
        
        Random random = new Random();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            dates.add(date.format(formatter));
            reachData.add(50 + random.nextInt(150));
            completeData.add(30 + random.nextInt(120));
        }
        
        Map<String, Object> trends = new HashMap<>();
        trends.put("dates", dates);
        trends.put("reachData", reachData);
        trends.put("completeData", completeData);
        
        return trends;
    }

    @Override
    public int getPendingCount() {
        List<Task> tasks = taskMapper.selectList(null);
        List<Report> reports = reportMapper.selectList(null);
        
        int pendingTasks = (int) tasks.stream().filter(t -> "pending".equals(t.getStatus())).count();
        int pendingReports = (int) reports.stream().filter(r -> "pending".equals(r.getStatus())).count();
        
        return pendingTasks + pendingReports;
    }

    @Override
    public Map<String, Object> getNews() {
        Map<String, Object> news = new HashMap<>();
        news.put("list", generateRecentActivities());
        return news;
    }
}
