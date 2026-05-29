package com.lvfat.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lvfat.config.AIProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * AI服务HTTP客户端
 * 负责与Python AI服务通信
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AIServiceClient {

    private final AIProperties aiProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 法律智能问答
     */
    public Map<String, Object> lawChat(String question, String role, String conversationId) {
        String url = aiProperties.getService().getUrl() + "/api/v1/law-chat/chat";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("question", question);
        requestBody.put("role", role != null ? role : "common");
        if (conversationId != null) {
            requestBody.put("conversation_id", conversationId);
        }

        return callAIApi(url, requestBody);
    }

    /**
     * 法条通俗化解读
     */
    public Map<String, Object> interpretLaw(String content, String level) {
        String url = aiProperties.getService().getUrl() + "/api/v1/law-chat/interpret";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("content", content);
        requestBody.put("level", level != null ? level : "normal");

        return callAIApi(url, requestBody);
    }

    /**
     * 生成普法素材
     */
    public Map<String, Object> generateMaterial(String type, String topic, String targetAudience) {
        String url = aiProperties.getService().getUrl() + "/api/v1/material/generate";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("type", type);
        requestBody.put("topic", topic);
        requestBody.put("target_audience", targetAudience);

        return callAIApi(url, requestBody);
    }

    /**
     * 案例拆解分析
     */
    public Map<String, Object> analyzeCase(String content, String type) {
        String url = aiProperties.getService().getUrl() + "/api/v1/case/analyze";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("content", content);
        requestBody.put("type", type);

        return callAIApi(url, requestBody);
    }

    /**
     * 智能组卷
     */
    public Map<String, Object> generateExam(String topic, int count, String difficulty, String type) {
        String url = aiProperties.getService().getUrl() + "/api/v1/exam/generate";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("topic", topic);
        requestBody.put("count", count);
        requestBody.put("difficulty", difficulty);
        requestBody.put("type", type);

        return callAIApi(url, requestBody);
    }

    /**
     * 通用AI API调用
     */
    private Map<String, Object> callAIApi(String url, Map<String, Object> requestBody) {
        try {
            long startTime = System.currentTimeMillis();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            long latency = System.currentTimeMillis() - startTime;

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                JsonNode dataNode = jsonNode.get("data");
                if (dataNode != null) {
                    Map<String, Object> result = objectMapper.convertValue(dataNode, Map.class);
                    result.put("_latency_ms", latency);
                    return result;
                }
            }

            log.warn("AI服务返回异常: {}, status: {}", url, response.getStatusCode());
            return createFallbackResponse(requestBody);

        } catch (Exception e) {
            log.error("调用AI服务失败: {}, error: {}", url, e.getMessage());
            return createFallbackResponse(requestBody);
        }
    }

    /**
     * 创建降级响应（当AI服务不可用时）
     */
    private Map<String, Object> createFallbackResponse(Map<String, Object> requestBody) {
        Map<String, Object> fallback = new HashMap<>();
        String question = (String) requestBody.get("question");
        String topic = (String) requestBody.get("topic");

        if (question != null) {
            fallback.put("answer", "您好！您询问的「" + question + "」涉及白色污染治理相关法律法规。\n\n" +
                    "建议您：\n1. 查阅相关法规文件\n2. 咨询当地环保部门\n3. 如需专业法律意见，建议联系律师。");
            fallback.put("suggestions", java.util.List.of(
                    "相关法规有哪些？",
                    "违规会有什么处罚？",
                    "如何进行合规整改？"
            ));
        } else if (topic != null) {
            fallback.put("content", "关于「" + topic + "」的宣传素材：\n\n" +
                    "【标题】" + topic + "\n\n" +
                    "【正文】\n保护环境，人人有责。让我们共同行动，减少白色污染，共建美好家园。\n\n" +
                    "【呼吁】从我做起，从现在做起！");
        } else {
            fallback.put("result", "AI服务暂时不可用，请稍后再试或联系管理员。");
        }

        fallback.put("_fallback", true);
        return fallback;
    }
}
