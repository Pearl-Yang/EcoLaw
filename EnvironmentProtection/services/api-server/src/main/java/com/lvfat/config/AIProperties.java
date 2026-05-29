package com.lvfat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * AI服务配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ai")
public class AIProperties {

    private ServiceConfig service = new ServiceConfig();
    private RagConfig rag = new RagConfig();

    @Data
    public static class ServiceConfig {
        /**
         * Python AI 服务地址
         */
        private String url = "http://localhost:8000";

        /**
         * 请求超时时间（毫秒）
         */
        private int timeout = 120000;

        /**
         * 是否启用流式输出
         */
        private boolean enableStream = true;
    }

    @Data
    public static class RagConfig {
        /**
         * 是否启用RAG检索
         */
        private boolean enable = true;

        /**
         * 检索返回结果数量
         */
        private int topK = 5;

        /**
         * 最小相关度阈值
         */
        private double minSimilarity = 0.3;
    }
}
