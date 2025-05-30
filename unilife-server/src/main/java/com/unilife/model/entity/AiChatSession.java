package com.unilife.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI聊天会话实体
 * 只管理会话元数据，消息存储由Spring AI ChatMemory处理
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiChatSession {
    /**
     * 会话ID（前端生成）
     */
    private String id;
    
    /**
     * 用户ID（可选，支持匿名会话）
     */
    private Long userId;
    
    /**
     * 会话标题
     */
    private String title;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 