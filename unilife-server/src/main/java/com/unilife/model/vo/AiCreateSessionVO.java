package com.unilife.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiCreateSessionVO {
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 会话标题
     */
    private String title;
} 