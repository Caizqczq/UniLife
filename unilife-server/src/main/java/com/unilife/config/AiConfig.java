package com.unilife.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
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
     * 配置ChatClient，集成Chat Memory功能
     */
    @Bean
    public ChatClient chatClient(OpenAiChatModel model, ChatMemory chatMemory) {
        return ChatClient.builder(model)
                .defaultAdvisors(
                    new SimpleLoggerAdvisor(),
                    MessageChatMemoryAdvisor.builder(chatMemory).build()
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
