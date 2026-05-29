package com.lvfat.controller;

import com.lvfat.common.result.Result;
import com.lvfat.entity.Report;
import com.lvfat.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 举报管理控制器
 */
@Tag(name = "举报管理")
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {
    
    private final ReportService reportService;
    
    @Operation(summary = "获取举报列表")
    @GetMapping
    public Result<List<Report>> listReports(
            @Parameter(description = "页码") @RequestParam(required = false) Integer page,
            @Parameter(description = "每页数量") @RequestParam(required = false) Integer pageSize,
            @Parameter(description = "类型") @RequestParam(required = false) String type,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "开始日期") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) String endDate
    ) {
        return Result.success(reportService.listReports(page, pageSize, type, status, keyword, startDate, endDate));
    }
    
    @Operation(summary = "获取举报详情")
    @GetMapping("/{id}")
    public Result<Report> getReport(@PathVariable String id) {
        return Result.success(reportService.getById(id));
    }
    
    @Operation(summary = "提交举报")
    @PostMapping
    public Result<Void> submitReport(@RequestBody Report report) {
        reportService.submitReport(report);
        return Result.success("举报提交成功");
    }
    
    @Operation(summary = "处理举报")
    @PostMapping("/{id}/handle")
    public Result<Void> handleReport(
            @PathVariable String id,
            @RequestBody Map<String, String> data
    ) {
        String status = data.get("status");
        String result = data.get("result");
        reportService.handleReport(id, status, result);
        return Result.success("处理完成");
    }
    
    @Operation(summary = "获取举报统计")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(
            @Parameter(description = "开始日期") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) String endDate
    ) {
        return Result.success(reportService.getStatistics(startDate, endDate));
    }
}
