package com.lvfat.controller;

import com.lvfat.common.result.Result;
import com.lvfat.entity.AIConversation;
import com.lvfat.entity.AIMessage;
import com.lvfat.service.AIConversationService;
import com.lvfat.service.AIMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI对话管理控制器
 */
@Tag(name = "AI对话管理")
@RestController
@RequestMapping("/ai/conversation")
@RequiredArgsConstructor
public class AIConversationController {

    private final AIConversationService aiConversationService;
    private final AIMessageService aiMessageService;

    @Operation(summary = "获取用户对话列表")
    @GetMapping("/list")
    public Result<List<AIConversation>> listConversations(
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "会话类型") @RequestParam(required = false) String sessionType,
            @Parameter(description = "页码") @RequestParam(required = false) Integer page,
            @Parameter(description = "每页数量") @RequestParam(required = false) Integer pageSize
    ) {
        List<AIConversation> conversations = aiConversationService.listByUser(userId, sessionType, page, pageSize);
        return Result.success(conversations);
    }

    @Operation(summary = "获取对话详情")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getConversationDetail(@PathVariable String id) {
        AIConversation conversation = aiConversationService.getConversationDetail(id);
        List<AIMessage> messages = aiMessageService.listByConversation(id);

        Map<String, Object> result = new HashMap<>();
        result.put("conversation", conversation);
        result.put("messages", messages);
        return Result.success(result);
    }

    @Operation(summary = "创建新对话")
    @PostMapping
    public Result<AIConversation> createConversation(
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "会话类型") @RequestParam(required = false, defaultValue = "law") String sessionType,
            @Parameter(description = "首条消息") @RequestParam(required = false) String firstMessage
    ) {
        AIConversation conversation = aiConversationService.createConversation(userId, sessionType, firstMessage);
        return Result.success(conversation);
    }

    @Operation(summary = "更新对话标题")
    @PutMapping("/{id}/title")
    public Result<Void> updateTitle(
            @PathVariable String id,
            @RequestParam String title
    ) {
        aiConversationService.updateConversationSummary(id, title, null);
        return Result.success("更新成功");
    }

    @Operation(summary = "归档对话")
    @PutMapping("/{id}/archive")
    public Result<Void> archiveConversation(@PathVariable String id) {
        aiConversationService.archiveConversation(id);
        return Result.success("归档成功");
    }

    @Operation(summary = "删除对话")
    @DeleteMapping("/{id}")
    public Result<Void> deleteConversation(@PathVariable String id) {
        aiConversationService.deleteConversation(id);
        return Result.success("删除成功");
    }

    @Operation(summary = "获取对话消息列表")
    @GetMapping("/{id}/messages")
    public Result<List<AIMessage>> listMessages(@PathVariable String id) {
        List<AIMessage> messages = aiMessageService.listByConversation(id);
        return Result.success(messages);
    }

    @Operation(summary = "消息反馈")
    @PostMapping("/messages/{messageId}/feedback")
    public Result<Void> feedback(
            @PathVariable String messageId,
            @RequestParam String feedback,
            @RequestParam(required = false) String feedbackContent
    ) {
        aiMessageService.feedback(messageId, feedback, feedbackContent);
        return Result.success("反馈成功");
    }

    @Operation(summary = "获取用户AI使用统计")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "开始日期") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) String endDate
    ) {
        Map<String, Object> stats = aiMessageService.getStatistics(userId, startDate, endDate);
        return Result.success(stats);
    }
}
