package com.lvfat.controller;

import com.lvfat.common.result.Result;
import com.lvfat.dto.*;
import com.lvfat.service.ExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 考试管理控制器
 */
@Tag(name = "考试管理")
@RestController
@RequestMapping("/api/exam")
@RequiredArgsConstructor
public class ExamController {
    
    private final ExamService examService;
    
    // ==================== 题目管理 ====================
    
    @Operation(summary = "获取题目列表")
    @GetMapping("/questions")
    public Result<List<Map<String, Object>>> listQuestions(
            @Parameter(description = "页码") @RequestParam(required = false) Integer page,
            @Parameter(description = "每页数量") @RequestParam(required = false) Integer pageSize,
            @Parameter(description = "题型") @RequestParam(required = false) String type,
            @Parameter(description = "难度") @RequestParam(required = false) String difficulty,
            @Parameter(description = "分类ID") @RequestParam(required = false) String categoryId,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword
    ) {
        return Result.success(examService.listQuestions(page, pageSize, type, difficulty, categoryId, keyword));
    }
    
    @Operation(summary = "获取题目详情")
    @GetMapping("/questions/{id}")
    public Result<Map<String, Object>> getQuestion(@PathVariable String id) {
        return Result.success(examService.getQuestionById(id));
    }
    
    @Operation(summary = "创建题目")
    @PostMapping("/questions")
    public Result<Void> createQuestion(@RequestBody Map<String, Object> question) {
        examService.saveQuestion(question);
        return Result.success("题目创建成功");
    }
    
    @Operation(summary = "更新题目")
    @PutMapping("/questions/{id}")
    public Result<Void> updateQuestion(@PathVariable String id, @RequestBody Map<String, Object> question) {
        question.put("id", id);
        examService.updateQuestion(question);
        return Result.success("题目更新成功");
    }
    
    @Operation(summary = "删除题目")
    @DeleteMapping("/questions/{id}")
    public Result<Void> deleteQuestion(@PathVariable String id) {
        examService.removeQuestion(id);
        return Result.success("题目删除成功");
    }
    
    // ==================== 试卷管理 ====================
    
    @Operation(summary = "获取试卷列表")
    @GetMapping("/papers")
    public Result<List<Map<String, Object>>> listPapers(
            @Parameter(description = "页码") @RequestParam(required = false) Integer page,
            @Parameter(description = "每页数量") @RequestParam(required = false) Integer pageSize,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "组卷方式") @RequestParam(required = false) String sourceType,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword
    ) {
        return Result.success(examService.listPapers(page, pageSize, status, sourceType, keyword));
    }
    
    @Operation(summary = "获取试卷详情")
    @GetMapping("/papers/{id}")
    public Result<ExamPaperDetailDTO> getPaper(
            @PathVariable String id,
            @Parameter(description = "是否包含答案") @RequestParam(required = false, defaultValue = "false") Boolean includeAnswer
    ) {
        return Result.success(examService.getPaperDetail(id, includeAnswer));
    }
    
    @Operation(summary = "创建试卷")
    @PostMapping("/papers")
    public Result<Map<String, Object>> createPaper(@RequestBody Map<String, Object> paper) {
        return Result.success(examService.createPaper(paper));
    }
    
    @Operation(summary = "更新试卷")
    @PutMapping("/papers/{id}")
    public Result<Void> updatePaper(@PathVariable String id, @RequestBody Map<String, Object> paper) {
        paper.put("id", id);
        examService.updatePaper(paper);
        return Result.success("试卷更新成功");
    }
    
    @Operation(summary = "删除试卷")
    @DeleteMapping("/papers/{id}")
    public Result<Void> deletePaper(@PathVariable String id) {
        examService.removePaper(id);
        return Result.success("试卷删除成功");
    }
    
    @Operation(summary = "发布试卷")
    @PostMapping("/papers/{id}/publish")
    public Result<Void> publishPaper(@PathVariable String id) {
        examService.publishPaper(id);
        return Result.success("试卷发布成功");
    }
    
    // ==================== 智能组卷 ====================
    
    @Operation(summary = "智能组卷")
    @PostMapping("/papers/auto-generate")
    public Result<Map<String, Object>> autoGeneratePaper(@RequestBody AutoGeneratePaperDTO config) {
        return Result.success(examService.autoGeneratePaper(config));
    }
    
    // ==================== 考试下发 ====================
    
    @Operation(summary = "下发考试")
    @PostMapping("/dispatch")
    public Result<Map<String, Object>> dispatchExam(@RequestBody ExamDispatchDTO dispatchDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return Result.success(examService.dispatchExam(dispatchDTO));
    }
    
    // ==================== 考试记录 ====================
    
    @Operation(summary = "获取考试记录列表")
    @GetMapping("/records")
    public Result<List<Map<String, Object>>> listExamRecords(
            @Parameter(description = "页码") @RequestParam(required = false) Integer page,
            @Parameter(description = "每页数量") @RequestParam(required = false) Integer pageSize,
            @Parameter(description = "试卷ID") @RequestParam(required = false) String paperId,
            @Parameter(description = "用户ID") @RequestParam(required = false) String userId,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "组织ID") @RequestParam(required = false) String organizationId
    ) {
        return Result.success(examService.listExamRecords(page, pageSize, paperId, userId, status, organizationId));
    }
    
    @Operation(summary = "获取考试记录详情")
    @GetMapping("/records/{id}")
    public Result<Map<String, Object>> getExamRecord(@PathVariable String id) {
        return Result.success(examService.getExamRecordById(id));
    }
    
    @Operation(summary = "开始考试")
    @PostMapping("/start/{paperId}")
    public Result<Map<String, Object>> startExam(@PathVariable String paperId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return Result.success(examService.startExam(paperId, username));
    }
    
    @Operation(summary = "提交考试")
    @PostMapping("/submit")
    public Result<Map<String, Object>> submitExam(@RequestBody ExamSubmitDTO submitDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return Result.success(examService.submitExam(submitDTO, username));
    }
    
    @Operation(summary = "获取答题详情")
    @GetMapping("/records/{id}/answers")
    public Result<List<AnswerDetailDTO>> getAnswerDetails(@PathVariable String id) {
        return Result.success(examService.getAnswerDetails(id));
    }
    
    // ==================== 统计分析 ====================
    
    @Operation(summary = "获取考试统计")
    @GetMapping("/statistics/{paperId}")
    public Result<ExamStatisticsDTO> getExamStatistics(@PathVariable String paperId) {
        return Result.success(examService.getExamStatistics(paperId));
    }
    
    @Operation(summary = "获取我的考试记录")
    @GetMapping("/my-records")
    public Result<List<Map<String, Object>>> getMyExamRecords() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return Result.success(examService.getExamRecordsByUser(username));
    }
    
    @Operation(summary = "获取我的排名")
    @GetMapping("/rank/{paperId}")
    public Result<Map<String, Object>> getMyRank(
            @PathVariable String paperId,
            @Parameter(description = "用户ID") @RequestParam(required = false) String userId
    ) {
        if (userId == null || userId.isEmpty()) {
            userId = SecurityContextHolder.getContext().getAuthentication().getName();
        }
        return Result.success(examService.getExamRank(paperId, userId));
    }
}
