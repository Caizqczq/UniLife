package com.unilife.service.impl;

import com.unilife.common.result.Result;
import com.unilife.mapper.AiChatSessionMapper;
import com.unilife.model.entity.AiChatSession;
import com.unilife.model.vo.AiSessionListVO;
import com.unilife.model.vo.AiSessionVO;
import com.unilife.service.AiChatSessionHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI聊天会话元数据管理服务实现
 * 只处理会话元数据，消息存储和查询由Spring AI ChatMemory处理
 */
@Service
@Slf4j
public class AiChatSessionHistoryServiceImpl implements AiChatSessionHistoryService {
    
    @Autowired
    private AiChatSessionMapper sessionMapper;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional
    public Result<AiChatSession> createOrUpdateSession(String sessionId, Long userId, String title) {
        log.info("创建或更新会话: sessionId={}, userId={}, title={}", sessionId, userId, title);
        
        try {
            // 检查会话是否已存在
            AiChatSession existingSession = sessionMapper.selectById(sessionId);
            
            if (existingSession != null) {
                // 更新现有会话
                if (title != null && !title.equals(existingSession.getTitle())) {
                    sessionMapper.updateTitle(sessionId, title);
                    existingSession.setTitle(title);
                    existingSession.setUpdatedAt(LocalDateTime.now());
                }
                return Result.success(existingSession);
            } else {
                // 创建新会话
                AiChatSession newSession = new AiChatSession();
                newSession.setId(sessionId);
                newSession.setUserId(userId);
                newSession.setTitle(title != null ? title : "新对话");
                newSession.setCreatedAt(LocalDateTime.now());
                newSession.setUpdatedAt(LocalDateTime.now());
                
                sessionMapper.insert(newSession);
                log.info("成功创建新会话: {}", sessionId);
                return Result.success(newSession);
            }
        } catch (Exception e) {
            log.error("创建或更新会话失败: {}", e.getMessage(), e);
            return Result.error("会话操作失败");
        }
    }

    @Override
    public Result<AiSessionListVO> getSessionList(Long userId, Integer page, Integer size) {
        log.info("获取会话列表: userId={}, page={}, size={}", userId, page, size);
        
        try {
            int offset = (page - 1) * size;
            
            List<AiChatSession> sessions;
            long total;
            
            if (userId != null) {
                // 查询用户会话
                sessions = sessionMapper.selectByUserId(userId, offset, size);
                total = sessionMapper.countByUserId(userId);
            } else {
                // 查询匿名会话
                sessions = sessionMapper.selectAnonymousSessions(offset, size);
                total = sessionMapper.countAnonymousSessions();
            }
            
            // 转换为VO
            List<AiSessionVO> sessionVOs = sessions.stream()
                    .map(this::convertToSessionVO)
                    .collect(Collectors.toList());
            
            AiSessionListVO result = new AiSessionListVO();
            result.setSessions(sessionVOs);
            result.setTotal(total);
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取会话列表失败: {}", e.getMessage(), e);
            return Result.error("获取会话列表失败");
        }
    }

    @Override
    public Result<AiChatSession> getSessionDetail(String sessionId) {
        log.info("获取会话详细信息: {}", sessionId);
        
        try {
            AiChatSession session = sessionMapper.selectById(sessionId);
            if (session == null) {
                return Result.error("会话不存在");
            }
            return Result.success(session);
        } catch (Exception e) {
            log.error("获取会话详细信息失败: {}", e.getMessage(), e);
            return Result.error("获取会话信息失败");
        }
    }

    @Override
    @Transactional
    public Result<Void> updateSessionTitle(String sessionId, String title) {
        log.info("更新会话标题: sessionId={}, title={}", sessionId, title);
        
        try {
            int updated = sessionMapper.updateTitle(sessionId, title);
            if (updated > 0) {
                return Result.success();
            } else {
                return Result.error("会话不存在或更新失败");
            }
        } catch (Exception e) {
            log.error("更新会话标题失败: {}", e.getMessage(), e);
            return Result.error("更新会话标题失败");
        }
    }

    @Override
    @Transactional
    public Result<Void> deleteSession(String sessionId) {
        log.info("删除会话: {}", sessionId);
        
        try {
            // 只删除会话元数据，Spring AI ChatMemory中的消息由AiServiceImpl处理
            int deleted = sessionMapper.deleteById(sessionId);
            if (deleted > 0) {
                log.info("成功删除会话: {}", sessionId);
                return Result.success();
            } else {
                return Result.error("会话不存在");
            }
        } catch (Exception e) {
            log.error("删除会话失败: {}", e.getMessage(), e);
            return Result.error("删除会话失败");
        }
    }
    
    /**
     * 转换为SessionVO
     */
    private AiSessionVO convertToSessionVO(AiChatSession session) {
        AiSessionVO vo = new AiSessionVO();
        vo.setId(session.getId());
        vo.setTitle(session.getTitle());
        vo.setCreatedAt(session.getCreatedAt() != null ? session.getCreatedAt().format(FORMATTER) : null);
        vo.setUpdatedAt(session.getUpdatedAt() != null ? session.getUpdatedAt().format(FORMATTER) : null);
        return vo;
    }
} 