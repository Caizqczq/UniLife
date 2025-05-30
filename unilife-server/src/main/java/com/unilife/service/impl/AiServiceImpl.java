package com.unilife.service.impl;

import com.unilife.common.result.Result;
import com.unilife.model.dto.AiCreateSessionDTO;
import com.unilife.model.dto.AiSendMessageDTO;
import com.unilife.model.dto.AiUpdateSessionDTO;
import com.unilife.model.vo.AiCreateSessionVO;
import com.unilife.model.vo.AiMessageHistoryVO;
import com.unilife.model.vo.AiSessionListVO;
import com.unilife.service.AiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;

@Service
@Slf4j
public class AiServiceImpl implements AiService {
    
    @Autowired
    private ChatClient chatClient;

    @Override
    public Flux<String> sendMessage(AiSendMessageDTO sendMessageDTO) {
        log.info("发送消息给AI: {}", sendMessageDTO.getMessage());
        
        // 使用ChatClient的流式响应
        return chatClient.prompt(sendMessageDTO.getMessage())
                .stream()
                .content();
    }

    @Override
    public Result<AiSessionListVO> getSessionList(Integer page, Integer size) {
        log.info("获取会话列表，页码: {}, 每页大小: {}", page, size);
        
        // TODO: 实现从数据库获取会话列表的逻辑
        AiSessionListVO sessionList = new AiSessionListVO();
        sessionList.setSessions(new ArrayList<>());
        sessionList.setTotal(0L);
        
        return Result.success(sessionList);
    }

    @Override
    public Result<AiMessageHistoryVO> getSessionMessages(String sessionId, Integer page, Integer size) {
        log.info("获取会话消息历史，会话ID: {}, 页码: {}, 每页大小: {}", sessionId, page, size);
        
        // TODO: 实现从数据库获取消息历史的逻辑
        AiMessageHistoryVO messageHistory = new AiMessageHistoryVO();
        messageHistory.setMessages(new ArrayList<>());
        messageHistory.setTotal(0L);
        
        return Result.success(messageHistory);
    }

    @Override
    public Result<AiCreateSessionVO> createSession(AiCreateSessionDTO createSessionDTO) {
        log.info("创建聊天会话: {}", createSessionDTO.getSessionId());
        
        // TODO: 实现在数据库中创建会话的逻辑
        AiCreateSessionVO response = new AiCreateSessionVO();
        response.setSessionId(createSessionDTO.getSessionId());
        response.setTitle(createSessionDTO.getTitle() != null ? createSessionDTO.getTitle() : "新对话");
        
        return Result.success(response);
    }

    @Override
    public Result<Void> updateSessionTitle(String sessionId, AiUpdateSessionDTO updateSessionDTO) {
        log.info("更新会话标题，会话ID: {}, 新标题: {}", sessionId, updateSessionDTO.getTitle());
        
        // TODO: 实现在数据库中更新会话标题的逻辑
        
        return Result.success();
    }

    @Override
    public Result<Void> clearSessionMessages(String sessionId) {
        log.info("清空会话消息，会话ID: {}", sessionId);
        
        // TODO: 实现在数据库中清空会话消息的逻辑
        
        return Result.success();
    }

    @Override
    public Result<Void> deleteSession(String sessionId) {
        log.info("删除会话，会话ID: {}", sessionId);
        
        // TODO: 实现在数据库中删除会话的逻辑
        
        return Result.success();
    }
}
