package com.unilife.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiSessionListVO {
    /**
     * 会话列表
     */
    private List<AiSessionVO> sessions;
    
    /**
     * 总数量
     */
    private Long total;
} 