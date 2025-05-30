package com.unilife.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiChatMessage {
    /**
     * 消息ID（前端生成）
     */
    private String id;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 角色 (user, assistant, system)
     */
    private String role;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
} 