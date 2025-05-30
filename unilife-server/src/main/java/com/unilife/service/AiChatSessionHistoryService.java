package com.unilife.service;

import com.unilife.common.result.Result;
import com.unilife.model.entity.AiChatSession;
import com.unilife.model.vo.AiSessionListVO;

/**
 * AI聊天会话元数据管理服务
 * 只处理会话元数据，消息存储和查询由Spring AI ChatMemory处理
 */
public interface AiChatSessionHistoryService {
    
    /**
     * 创建或更新会话
     * @param sessionId 会话ID
     * @param userId 用户ID（可选，支持匿名会话）
     * @param title 会话标题
     * @return 创建结果
     */
    Result<AiChatSession> createOrUpdateSession(String sessionId, Long userId, String title);
    
    /**
     * 获取会话列表（支持用户会话和匿名会话）
     * @param userId 用户ID（为null时查询匿名会话）
     * @param page 页码
     * @param size 每页大小
     * @return 会话列表
     */
    Result<AiSessionListVO> getSessionList(Long userId, Integer page, Integer size);
    
    /**
     * 获取会话详细信息
     * @param sessionId 会话ID
     * @return 会话信息
     */
    Result<AiChatSession> getSessionDetail(String sessionId);
    
    /**
     * 更新会话标题
     * @param sessionId 会话ID
     * @param title 新标题
     * @return 更新结果
     */
    Result<Void> updateSessionTitle(String sessionId, String title);
    
    /**
     * 更新会话的最后活动时间
     * @param sessionId 会话ID
     * @return 更新结果
     */
    Result<Void> updateSessionLastActivity(String sessionId);
    
    /**
     * 删除会话（会话元数据和Spring AI ChatMemory中的消息）
     * @param sessionId 会话ID
     * @return 删除结果
     */
    Result<Void> deleteSession(String sessionId);
} 