package com.unilife.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiCreateSessionDTO {
    /**
     * 会话ID（前端生成）
     */
    private String sessionId;
    
    /**
     * 会话标题（可选）
     */
    private String title;
} 