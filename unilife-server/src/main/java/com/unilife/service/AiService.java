package com.unilife.service;

import com.unilife.common.result.Result;
import com.unilife.model.dto.AiCreateSessionDTO;
import com.unilife.model.dto.AiSendMessageDTO;
import com.unilife.model.dto.AiUpdateSessionDTO;
import com.unilife.model.vo.AiCreateSessionVO;
import com.unilife.model.vo.AiMessageHistoryVO;
import com.unilife.model.vo.AiSessionListVO;
import reactor.core.publisher.Flux;

/**
 * AI服务接口
 */
public interface AiService {
    
    /**
     * 发送消息给AI（流式响应）
     * @param sendMessageDTO 发送消息DTO
     * @return 流式字符串响应
     */
    Flux<String> sendMessage(AiSendMessageDTO sendMessageDTO);
    
    /**
     * 获取会话列表
     * @param page 页码
     * @param size 每页大小
     * @return 会话列表
     */
    Result<AiSessionListVO> getSessionList(Integer page, Integer size);
    
    /**
     * 获取会话消息历史
     * @param sessionId 会话ID
     * @param page 页码
     * @param size 每页大小
     * @return 消息历史
     */
    Result<AiMessageHistoryVO> getSessionMessages(String sessionId, Integer page, Integer size);
    
    /**
     * 创建会话
     * @param createSessionDTO 创建会话DTO
     * @return 创建结果
     */
    Result<AiCreateSessionVO> createSession(AiCreateSessionDTO createSessionDTO);
    
    /**
     * 更新会话标题
     * @param sessionId 会话ID
     * @param updateSessionDTO 更新会话DTO
     * @return 更新结果
     */
    Result<Void> updateSessionTitle(String sessionId, AiUpdateSessionDTO updateSessionDTO);
    
    /**
     * 清空会话消息
     * @param sessionId 会话ID
     * @return 清空结果
     */
    Result<Void> clearSessionMessages(String sessionId);
    
    /**
     * 删除会话
     * @param sessionId 会话ID
     * @return 删除结果
     */
    Result<Void> deleteSession(String sessionId);
}
