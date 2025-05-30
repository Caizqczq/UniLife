package com.unilife.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiMessageVO {
    /**
     * 消息ID
     */
    private String id;
    
    /**
     * 角色
     */
    private String role;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 时间戳
     */
    private String timestamp;
} 