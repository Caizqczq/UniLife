package com.unilife.config;

import com.unilife.common.constant.Prompt;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AiConfig {
    
    /**
     * 是否启用自动生成会话标题功能
     */
    @Value("${app.ai.auto-title.enabled:true}")
    private boolean autoTitleEnabled;
    
    /**
     * 标题生成策略：simple(简单算法) 或 ai(AI生成)
     */
    @Value("${app.ai.auto-title.strategy:simple}")
    private String titleGenerationStrategy;
    
    /**
     * 配置ChatMemory，使用JDBC存储库实现持久化
     */
    @Bean
    public ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(20) // 保留最近20条消息作为上下文
                .build();
    }

    /**
     * 配置异步任务执行器，专门用于PDF向量化处理
     */
    @Bean("pdfVectorTaskExecutor")
    public Executor pdfVectorTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);        // 核心线程数
        executor.setMaxPoolSize(5);         // 最大线程数
        executor.setQueueCapacity(100);     // 队列容量
        executor.setThreadNamePrefix("pdf-vector-");
        executor.setKeepAliveSeconds(60);   // 线程空闲时间
        executor.initialize();
        return executor;
    }

    
    /**
     * 配置ChatClient，集成Chat Memory功能
     */
    @Bean
    public ChatClient chatClient(OpenAiChatModel model, ChatMemory chatMemory, VectorStore vectorStore) {
        return ChatClient.builder(model)
                .defaultSystem(Prompt.Prompt) // 设置默认系统提示
                .defaultAdvisors(
                    new SimpleLoggerAdvisor(),
                    MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(SearchRequest.builder()
                                        .similarityThreshold(0.5d)
                                        .topK(5)
                                        .build())
                                        .build()
                )
                .build();
    }
    
    /**
     * 获取自动标题生成配置
     */
    public boolean isAutoTitleEnabled() {
        return autoTitleEnabled;
    }
    
    /**
     * 获取标题生成策略
     */
    public String getTitleGenerationStrategy() {
        return titleGenerationStrategy;
    }
}
