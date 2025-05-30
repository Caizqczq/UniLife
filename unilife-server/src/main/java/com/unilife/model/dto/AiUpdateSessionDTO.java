package com.unilife.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiUpdateSessionDTO {
    /**
     * 更新后的会话标题
     */
    private String title;
} 