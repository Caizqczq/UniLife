package com.unilife.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiSendMessageDTO {
    /**
     * 用户发送的消息内容
     */
    private String message;
    
    /**
     * 会话ID（可选）
     */
    private String sessionId;
    
    /**
     * 会话历史记录（可选）
     */
    private List<ConversationMessage> conversationHistory;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ConversationMessage {
        private String id;
        private String role;
        private String content;
        private String timestamp;
    }
} 