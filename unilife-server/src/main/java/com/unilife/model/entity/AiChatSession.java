package com.unilife.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiChatSession {
    /**
     * 会话ID（前端生成）
     */
    private String id;
    
    /**
     * 用户ID
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