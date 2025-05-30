package com.unilife.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiSessionVO {
    /**
     * 会话ID
     */
    private String id;
    
    /**
     * 会话标题
     */
    private String title;
    
    /**
     * 创建时间
     */
    private String createdAt;
    
    /**
     * 更新时间
     */
    private String updatedAt;

} 