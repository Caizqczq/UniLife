package com.unilife.mapper;

import com.unilife.model.entity.AiChatSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI聊天会话Mapper
 */
@Mapper
public interface AiChatSessionMapper {
    
    /**
     * 插入会话
     */
    int insert(AiChatSession session);
    
    /**
     * 根据ID查询会话
     */
    AiChatSession selectById(@Param("id") String id);
    
    /**
     * 根据用户ID分页查询会话列表
     */
    List<AiChatSession> selectByUserId(@Param("userId") Long userId, 
                                       @Param("offset") int offset, 
                                       @Param("limit") int limit);
    
    /**
     * 查询匿名会话列表（用户ID为空）
     */
    List<AiChatSession> selectAnonymousSessions(@Param("offset") int offset, 
                                              @Param("limit") int limit);
    
    /**
     * 统计用户会话总数
     */
    long countByUserId(@Param("userId") Long userId);
    
    /**
     * 统计匿名会话总数
     */
    long countAnonymousSessions();
    
    /**
     * 更新会话标题
     */
    int updateTitle(@Param("id") String id, @Param("title") String title);
    
    /**
     * 更新会话的最后消息时间和消息数量
     */
    int updateMessageInfo(@Param("id") String id, 
                         @Param("lastMessageTime") LocalDateTime lastMessageTime,
                         @Param("messageCount") Integer messageCount);
    
    /**
     * 删除会话
     */
    int deleteById(@Param("id") String id);
    
    /**
     * 批量删除过期会话
     */
    int deleteExpiredSessions(@Param("expireTime") LocalDateTime expireTime);
} 