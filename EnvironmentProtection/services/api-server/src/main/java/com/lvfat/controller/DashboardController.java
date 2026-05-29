package com.lvfat.controller;

import com.lvfat.common.result.Result;
import com.lvfat.service.DashboardService;
import com.lvfat.vo.DashboardVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 数据看板
 */
@Tag(name = "数据看板")
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "获取仪表盘数据")
    @GetMapping
    public Result<DashboardVO> getDashboardData() {
        return Result.success(dashboardService.getDashboardData());
    }

    @Operation(summary = "获取统计概览")
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview(
            @RequestParam(required = false, defaultValue = "7d") String range
    ) {
        return Result.success(dashboardService.getOverview(range));
    }

    @Operation(summary = "获取趋势数据")
    @GetMapping("/trends")
    public Result<Map<String, Object>> getTrends(
            @RequestParam(required = false, defaultValue = "7d") String range
    ) {
        return Result.success(dashboardService.getTrends(range));
    }

    @Operation(summary = "获取待办任务数")
    @GetMapping("/pending-count")
    public Result<Integer> getPendingCount() {
        return Result.success(dashboardService.getPendingCount());
    }

    @Operation(summary = "获取最新动态")
    @GetMapping("/news")
    public Result<Map<String, Object>> getNews() {
        return Result.success(dashboardService.getNews());
    }
}
