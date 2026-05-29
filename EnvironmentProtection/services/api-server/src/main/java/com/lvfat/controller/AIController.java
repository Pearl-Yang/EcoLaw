package com.lvfat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lvfat.client.AIServiceClient;
import com.lvfat.common.result.Result;
import com.lvfat.entity.AIConversation;
import com.lvfat.entity.AIMessage;
import com.lvfat.entity.AIMaterial;
import com.lvfat.entity.Law;
import com.lvfat.repository.LawMapper;
import com.lvfat.service.AIConversationService;
import com.lvfat.service.AIMessageService;
import com.lvfat.service.AIMaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * AI素材生成控制器（整合对话历史和Python AI服务）
 */
@Tag(name = "AI功能")
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIMaterialService aiMaterialService;
    private final LawMapper lawMapper;
    private final AIServiceClient aiServiceClient;
    private final AIConversationService aiConversationService;
    private final AIMessageService aiMessageService;
    private final ObjectMapper objectMapper;

    @Operation(summary = "生成普法素材")
    @PostMapping("/material/generate")
    public Result<Map<String, Object>> generateMaterial(@RequestBody Map<String, Object> data) {
        String type = (String) data.get("type");
        String topic = (String) data.get("topic");
        String targetAudience = (String) data.getOrDefault("targetAudience", "普通群众");
        String lawId = (String) data.getOrDefault("lawId", "");

        // 调用Python AI服务
        Map<String, Object> aiResult = aiServiceClient.generateMaterial(type, topic, targetAudience);

        // 保存到数据库
        AIMaterial material = aiMaterialService.generateMaterial(type, topic, targetAudience, lawId);

        Map<String, Object> result = new HashMap<>();
        result.put("id", material.getId());
        result.put("title", topic);
        result.put("content", aiResult.getOrDefault("content", ""));
        result.put("type", type);
        result.put("isFallback", aiResult.getOrDefault("_fallback", false));

        return Result.success(result);
    }

    @Operation(summary = "生成法条解读")
    @PostMapping("/law/explain")
    public Result<Map<String, Object>> explainLaw(@RequestBody Map<String, Object> data) {
        String content = (String) data.get("content");
        String lawId = (String) data.getOrDefault("lawId", "");
        String level = (String) data.getOrDefault("level", "normal");

        // 调用Python AI服务
        Map<String, Object> aiResult = aiServiceClient.interpretLaw(content, level);

        Map<String, Object> result = new HashMap<>();
        result.put("explanation", aiResult.getOrDefault("interpretation", "解读生成中..."));
        result.put("isFallback", aiResult.getOrDefault("_fallback", false));

        return Result.success(result);
    }

    @Operation(summary = "生成案例分析")
    @PostMapping("/case/analyze")
    public Result<Map<String, Object>> analyzeCase(@RequestBody Map<String, Object> data) {
        String content = (String) data.get("content");
        String type = (String) data.getOrDefault("type", "administrative");

        // 调用Python AI服务
        Map<String, Object> aiResult = aiServiceClient.analyzeCase(content, type);

        Map<String, Object> result = new HashMap<>();
        result.put("analysis", aiResult.getOrDefault("analysis", ""));
        result.put("isFallback", aiResult.getOrDefault("_fallback", false));

        // 添加相关法规
        List<Map<String, String>> relatedLaws = new ArrayList<>();
        List<Law> laws = lawMapper.selectList(null);
        if (laws != null && !laws.isEmpty()) {
            for (Law law : laws.subList(0, Math.min(3, laws.size()))) {
                Map<String, String> lawMap = new HashMap<>();
                lawMap.put("id", law.getId());
                lawMap.put("title", law.getTitle());
                relatedLaws.add(lawMap);
            }
        }
        result.put("relatedLaws", relatedLaws);

        return Result.success(result);
    }

    @Operation(summary = "生成考试题目")
    @PostMapping("/exam/generate")
    public Result<Map<String, Object>> generateExam(@RequestBody Map<String, Object> data) {
        String topic = (String) data.get("topic");
        Integer count = (Integer) data.getOrDefault("count", 5);
        String difficulty = (String) data.getOrDefault("difficulty", "medium");
        String type = (String) data.getOrDefault("type", "single");

        // 调用Python AI服务
        Map<String, Object> aiResult = aiServiceClient.generateExam(topic, count, difficulty, type);

        Map<String, Object> result = new HashMap<>();
        result.put("questions", aiResult.getOrDefault("questions", new ArrayList<>()));
        result.put("isFallback", aiResult.getOrDefault("_fallback", false));

        return Result.success(result);
    }

    @Operation(summary = "生成工作报告")
    @PostMapping("/report/generate")
    public Result<Map<String, Object>> generateReport(@RequestBody Map<String, Object> data) {
        String taskId = (String) data.get("taskId");
        String content = (String) data.get("content");
        String type = (String) data.getOrDefault("type", "daily");

        // 生成工作报告（暂不调用AI，简化实现）
        Map<String, Object> result = new HashMap<>();
        result.put("report", "【" + ("daily".equals(type) ? "日报" : "周报".equals(type) ? "周报" : "月报") + "】\n\n" +
                "一、基本情况\n" +
                content + "\n\n" +
                "二、工作进展\n" +
                "1. 已完成主要工作内容\n" +
                "2. 正在推进相关任务\n\n" +
                "三、存在问题\n" +
                "暂无重大问题\n\n" +
                "四、下一步计划\n" +
                "1. 继续推进工作计划\n" +
                "2. 加强协调配合");

        return Result.success(result);
    }

    @Operation(summary = "法律智能问答（带对话历史）")
    @PostMapping("/chat")
    public Result<Map<String, Object>> lawChat(@RequestBody Map<String, Object> data) {
        String question = (String) data.get("question");
        String userId = (String) data.getOrDefault("userId", "anonymous");
        String role = (String) data.getOrDefault("role", "common");
        String conversationId = (String) data.getOrDefault("conversationId", "");

        long startTime = System.currentTimeMillis();

        // 获取或创建会话
        AIConversation conversation;
        if (conversationId == null || conversationId.isEmpty()) {
            conversation = aiConversationService.createConversation(userId, "law", question);
            conversationId = conversation.getId();
        } else {
            conversation = aiConversationService.getById(conversationId);
        }

        // 保存用户消息
        AIMessage userMessage = aiMessageService.saveUserMessage(conversationId, question);

        // 调用Python AI服务
        Map<String, Object> aiResult = aiServiceClient.lawChat(question, role, conversationId);

        long latency = System.currentTimeMillis() - startTime;

        // 保存AI回复
        String lawReferences = null;
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> relatedLaws = (List<Map<String, Object>>) aiResult.get("related_laws");
            if (relatedLaws != null) {
                lawReferences = objectMapper.writeValueAsString(relatedLaws);
            }
        } catch (JsonProcessingException e) {
            // 忽略序列化错误
        }

        AIMessage assistantMessage = aiMessageService.saveAssistantMessage(
                conversationId,
                (String) aiResult.getOrDefault("answer", ""),
                "gpt-4",
                null,
                (int) latency,
                lawReferences
        );

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("answer", aiResult.getOrDefault("answer", ""));
        result.put("conversationId", conversationId);
        result.put("messageId", assistantMessage.getId());
        result.put("latencyMs", latency);
        result.put("isFallback", aiResult.getOrDefault("_fallback", false));

        // 添加综合总结
        String summary = (String) aiResult.get("summary");
        if (summary != null) {
            result.put("summary", summary);
        } else {
            // 自动生成总结
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> laws = (List<Map<String, Object>>) aiResult.get("related_laws");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> news = (List<Map<String, Object>>) aiResult.get("related_news");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> comments = (List<Map<String, Object>>) aiResult.get("related_comments");
            
            int lawCount = laws != null ? laws.size() : 0;
            int newsCount = news != null ? news.size() : 0;
            int commentCount = comments != null ? comments.size() : 0;
            
            result.put("summary", String.format("搜索到 %d 部相关法规、%d 篇资讯、%d 条网友讨论", lawCount, newsCount, commentCount));
        }

        // 添加相关法规引用
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> relatedLaws = (List<Map<String, Object>>) aiResult.get("related_laws");
        if (relatedLaws == null || relatedLaws.isEmpty()) {
            relatedLaws = new ArrayList<>();
            List<Law> laws = lawMapper.selectList(null);
            if (laws != null && !laws.isEmpty()) {
                for (Law law : laws.subList(0, Math.min(3, laws.size()))) {
                    Map<String, Object> lawMap = new HashMap<>();
                    lawMap.put("id", law.getId());
                    lawMap.put("title", law.getTitle());
                    lawMap.put("level", law.getLevel());
                    lawMap.put("relevance", 0.85);
                    relatedLaws.add(lawMap);
                }
            }
        }
        result.put("relatedLaws", relatedLaws);

        // 添加相关新闻
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> relatedNews = (List<Map<String, Object>>) aiResult.get("related_news");
        if (relatedNews == null) {
            relatedNews = new ArrayList<>();
        }
        result.put("relatedNews", relatedNews);

        // 添加相关评论
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> relatedComments = (List<Map<String, Object>>) aiResult.get("related_comments");
        if (relatedComments == null) {
            relatedComments = new ArrayList<>();
        }
        result.put("relatedComments", relatedComments);

        // 添加追问建议
        @SuppressWarnings("unchecked")
        List<String> suggestions = (List<String>) aiResult.get("suggestions");
        if (suggestions == null) {
            suggestions = Arrays.asList("相关法规具体有哪些？", "违规会有什么处罚？", "如何进行合规整改？");
        }
        result.put("suggestions", suggestions);

        return Result.success(result);
    }

    @Operation(summary = "获取素材列表")
    @GetMapping("/materials")
    public Result<List<AIMaterial>> listMaterials(
            @Parameter(description = "页码") @RequestParam(required = false) Integer page,
            @Parameter(description = "每页数量") @RequestParam(required = false) Integer pageSize,
            @Parameter(description = "类型") @RequestParam(required = false) String type,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword
    ) {
        return Result.success(aiMaterialService.listMaterials(page, pageSize, type, keyword));
    }

    @Operation(summary = "获取素材详情")
    @GetMapping("/materials/{id}")
    public Result<AIMessage> getMaterial(@PathVariable String id) {
        return Result.success(aiMessageService.getById(id));
    }

    @Operation(summary = "删除素材")
    @DeleteMapping("/materials/{id}")
    public Result<Void> deleteMaterial(@PathVariable String id) {
        aiMaterialService.removeById(id);
        return Result.success("删除成功");
    }
}
