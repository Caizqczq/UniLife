package com.unilife.service.impl;

import com.unilife.common.result.Result;
import com.unilife.model.dto.AiCreateSessionDTO;
import com.unilife.model.dto.AiSendMessageDTO;
import com.unilife.model.dto.AiUpdateSessionDTO;
import com.unilife.model.vo.AiCreateSessionVO;
import com.unilife.model.vo.AiMessageHistoryVO;
import com.unilife.model.vo.AiSessionListVO;
import com.unilife.service.AiService;
import com.unilife.service.AiChatSessionHistoryService;
import com.unilife.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AiServiceImpl implements AiService {
    
    @Autowired
    private ChatClient chatClient;
    
    @Autowired
    private ChatMemory chatMemory;
    
    @Autowired
    private AiChatSessionHistoryService sessionHistoryService;

    @Override
    public Flux<String> sendMessage(AiSendMessageDTO sendMessageDTO) {
        log.info("发送消息给AI: {}, 会话ID: {}", sendMessageDTO.getMessage(), sendMessageDTO.getSessionId());
        
        String sessionId = sendMessageDTO.getSessionId();
        if (sessionId == null || sessionId.trim().isEmpty()) {
            // 如果没有提供会话ID，生成一个新的
            sessionId = "session_" + System.currentTimeMillis();
            log.info("生成新的会话ID: {}", sessionId);
        }
        
        // 确保会话元数据存在
        sessionHistoryService.createOrUpdateSession(sessionId, BaseContext.getId(), "新对话");
        
        // 使用ChatClient的流式响应，Spring AI会自动处理记忆
        return chatClient.prompt()
                .user(sendMessageDTO.getMessage())
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sendMessageDTO.getSessionId()))
                .stream()
                .content();
    }

    @Override
    public Result<AiSessionListVO> getSessionList(Integer page, Integer size) {
        log.info("获取会话列表: page={}, size={}", page, size);
        
        return sessionHistoryService.getSessionList(BaseContext.getId(), page, size);
    }

    @Override
    public Result<AiMessageHistoryVO> getSessionMessages(String sessionId, Integer page, Integer size) {
        log.info("获取会话消息历史，会话ID: {}", sessionId);
        
        try {
            // 直接从Spring AI ChatMemory获取消息历史
            List<Message> messages = chatMemory.get(sessionId);
            
            AiMessageHistoryVO messageHistory = new AiMessageHistoryVO();
            
            if (messages != null && !messages.isEmpty()) {
                // 转换Message为VO对象
                List<com.unilife.model.vo.AiMessageVO> messageVOs = messages.stream()
                        .map(message -> {
                            com.unilife.model.vo.AiMessageVO vo = new com.unilife.model.vo.AiMessageVO();
                            vo.setId(String.valueOf(System.currentTimeMillis() + Math.random()));
                            vo.setRole(message.getMessageType().getValue().toLowerCase());
                            vo.setContent(message.getText());
                            vo.setTimestamp(java.time.LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                            return vo;
                        })
                        .collect(Collectors.toList());
                
                messageHistory.setMessages(messageVOs);
                messageHistory.setTotal((long) messageVOs.size());
            } else {
                messageHistory.setMessages(new ArrayList<>());
                messageHistory.setTotal(0L);
            }
            
            return Result.success(messageHistory);
        } catch (Exception e) {
            log.error("获取会话消息历史失败: {}", e.getMessage(), e);
            return Result.error("获取消息历史失败");
        }
    }

    @Override
    public Result<AiCreateSessionVO> createSession(AiCreateSessionDTO createSessionDTO) {
        log.info("创建聊天会话: {}", createSessionDTO.getSessionId());
        
        try {
            // 使用会话历史服务创建会话元数据
            Result<?> result = sessionHistoryService.createOrUpdateSession(
                createSessionDTO.getSessionId(), 
                BaseContext.getId(), // 暂时支持匿名会话
                createSessionDTO.getTitle()
            );
            
            if (result.getCode() == 200) {
                AiCreateSessionVO response = new AiCreateSessionVO();
                response.setSessionId(createSessionDTO.getSessionId());
                response.setTitle(createSessionDTO.getTitle() != null ? createSessionDTO.getTitle() : "新对话");
                return Result.success(response);
            } else {
                return Result.error(result.getMessage());
            }
        } catch (Exception e) {
            log.error("创建会话失败: {}", e.getMessage(), e);
            return Result.error("创建会话失败");
        }
    }

    @Override
    public Result<Void> updateSessionTitle(String sessionId, AiUpdateSessionDTO updateSessionDTO) {
        log.info("更新会话标题: sessionId={}, title={}", sessionId, updateSessionDTO.getTitle());
        
        // 使用会话历史服务更新标题
        return sessionHistoryService.updateSessionTitle(sessionId, updateSessionDTO.getTitle());
    }

    @Override
    public Result<Void> clearSessionMessages(String sessionId) {
        log.info("清空会话消息，会话ID: {}", sessionId);
        
        try {
            // 清空Spring AI ChatMemory中的消息
            chatMemory.clear(sessionId);
            log.info("成功清空会话 {} 的消息", sessionId);
            return Result.success();
        } catch (Exception e) {
            log.error("清空会话消息失败: {}", e.getMessage(), e);
            return Result.error("清空会话消息失败");
        }
    }

    @Override
    public Result<Void> deleteSession(String sessionId) {
        log.info("删除会话，会话ID: {}", sessionId);
        
        try {
            // 删除Spring AI ChatMemory中的消息
            chatMemory.clear(sessionId);
            
            // 删除会话元数据
            Result<Void> result = sessionHistoryService.deleteSession(sessionId);
            
            if (result.getCode() == 200) {
                log.info("成功删除会话 {}", sessionId);
                return Result.success();
            } else {
                return result;
            }
        } catch (Exception e) {
            log.error("删除会话失败: {}", e.getMessage(), e);
            return Result.error("删除会话失败");
        }
    }
}
