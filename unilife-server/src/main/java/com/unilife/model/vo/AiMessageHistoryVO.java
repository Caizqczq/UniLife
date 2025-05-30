package com.unilife.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiMessageHistoryVO {
    /**
     * 消息列表
     */
    private List<AiMessageVO> messages;
    
    /**
     * 总数量
     */
    private Long total;
    
    /**
     * 会话信息
     */
    private AiSessionVO sessionInfo;
} 