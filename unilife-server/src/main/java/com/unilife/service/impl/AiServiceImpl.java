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
import com.unilife.config.AiConfig;
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

    @Autowired
    private AiConfig aiConfig;

    @Override
    public Flux<String> sendMessage(AiSendMessageDTO sendMessageDTO) {
        log.info("发送消息给AI: {}, 会话ID: {}", sendMessageDTO.getMessage(), sendMessageDTO.getSessionId());
        
        String sessionId = sendMessageDTO.getSessionId();
        
        // 确保会话元数据存在
        sessionHistoryService.createOrUpdateSession(sessionId, BaseContext.getId(), "新对话");
        
        // 检查是否为第一次对话（只有用户消息且为第一条）
        boolean isFirstMessage = isFirstUserMessage(sessionId);
        
        // 如果是第一次对话，立即异步生成并更新标题
        if (isFirstMessage) {
            generateAndUpdateSessionTitle(sessionId, sendMessageDTO.getMessage());
        }
        
        // 使用ChatClient的流式响应，Spring AI会自动处理记忆
        Flux<String> responseFlux = chatClient.prompt()
                .user(sendMessageDTO.getMessage())
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sendMessageDTO.getSessionId()))
                .stream()
                .content();
        
        // 在消息发送完成后只更新会话的活跃时间
        final String finalSessionId = sessionId;
        return responseFlux.doOnComplete(() -> {
            try {
                // 更新会话的最后活动时间
                sessionHistoryService.updateSessionLastActivity(finalSessionId);
                log.info("已更新会话 {} 的最后活动时间", finalSessionId);
            } catch (Exception e) {
                log.warn("更新会话活动时间失败: {}", e.getMessage());
            }
        });
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
    
    /**
     * 检查是否为第一条用户消息
     * @param sessionId 会话ID
     * @return 是否为第一条用户消息
     */
    private boolean isFirstUserMessage(String sessionId) {
        try {
            List<Message> messages = chatMemory.get(sessionId);
            if (messages == null || messages.isEmpty()) {
                return true; // 没有消息历史，这是第一条消息
            }
            
            // 统计用户消息数量（排除系统消息）
            long userMessageCount = messages.stream()
                .filter(message -> "user".equalsIgnoreCase(message.getMessageType().getValue()))
                .count();
            
            return userMessageCount == 0; // 如果没有用户消息，说明即将发送的是第一条
        } catch (Exception e) {
            log.warn("检查第一条消息失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 异步生成并更新会话标题
     * @param sessionId 会话ID
     * @param userMessage 用户消息内容
     */
    private void generateAndUpdateSessionTitle(String sessionId, String userMessage) {
        // 检查是否启用自动标题生成
        if (!aiConfig.isAutoTitleEnabled()) {
            log.debug("自动标题生成已禁用，跳过标题生成");
            return;
        }
        
        // 异步执行，不阻塞主流程
        new Thread(() -> {
            long startTime = System.currentTimeMillis();
            try {
                log.debug("开始为会话 {} 生成标题，策略: {}", sessionId, aiConfig.getTitleGenerationStrategy());
                
                String generatedTitle = generateTitleFromMessage(userMessage);
                
                // 更新会话标题
                Result<Void> updateResult = sessionHistoryService.updateSessionTitle(sessionId, generatedTitle);
                
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                
                if (updateResult.getCode() == 200) {
                    log.info("成功为会话 {} 生成标题: {} (耗时: {}ms)", sessionId, generatedTitle, duration);
                } else {
                    log.warn("更新会话标题失败: {} (耗时: {}ms)", updateResult.getMessage(), duration);
                }
            } catch (Exception e) {
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                log.error("生成会话标题失败: {} (耗时: {}ms)", e.getMessage(), duration, e);
            }
        }).start();
    }
    
    /**
     * 从用户消息生成会话标题
     * 根据配置选择生成策略：simple(简单算法) 或 ai(AI生成)
     * @param userMessage 用户消息
     * @return 生成的标题
     */
    private String generateTitleFromMessage(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return "新对话";
        }
        
        String message = userMessage.trim();
        String strategy = aiConfig.getTitleGenerationStrategy();
        
        log.debug("使用标题生成策略: {}", strategy);
        
        if ("ai".equalsIgnoreCase(strategy)) {
            // 方案2：使用AI生成智能标题
            return generateAITitle(message);
        } else {
            // 方案1：使用简单文本处理（默认）
            return generateSimpleTitle(message);
        }
    }
    
    /**
     * 使用简单算法生成标题
     * @param message 用户消息
     * @return 生成的标题
     */
    private String generateSimpleTitle(String message) {
        // 去除多余的空格和换行
        String cleanMessage = message.replaceAll("\\s+", " ").trim();
        
        // 如果消息太短，直接返回
        if (cleanMessage.length() <= 20) {
            return cleanMessage;
        }
        
        // 尝试找到问号，截取问题部分
        int questionMarkIndex = cleanMessage.indexOf('？');
        if (questionMarkIndex == -1) {
            questionMarkIndex = cleanMessage.indexOf('?');
        }
        
        if (questionMarkIndex > 0 && questionMarkIndex <= 50) {
            return cleanMessage.substring(0, questionMarkIndex + 1);
        }
        
        // 尝试找到句号，截取第一句话
        int periodIndex = cleanMessage.indexOf('。');
        if (periodIndex == -1) {
            periodIndex = cleanMessage.indexOf('.');
        }
        
        if (periodIndex > 0 && periodIndex <= 50) {
            return cleanMessage.substring(0, periodIndex + 1);
        }
        
        // 如果没有标点符号，截取前50个字符
        if (cleanMessage.length() > 50) {
            return cleanMessage.substring(0, 47) + "...";
        }
        
        return cleanMessage;
    }
    
    /**
     * 使用AI生成标题
     * @param message 用户消息
     * @return 生成的标题
     */
    private String generateAITitle(String message) {
        try {
            String prompt = String.format(
                "请为以下用户发送的内容生成一个简洁的对话标题，这个标题是用户用下面内容与大模型对话时发送信息所总结的,不超过20个字，不要包含引号：\n\n%s",
                message
            );
            
            String title = chatClient.prompt()
                .user(prompt)
                .call()
                .content();
                
            // 清理生成的标题
            title = title.trim()
                .replaceAll("^[\"']+|[\"']+$", "") // 去除首尾引号
                .replaceAll("\\s+", " "); // 合并多个空格
                
            if (title.length() > 30) {
                title = title.substring(0, 27) + "...";
            }
            
            return title.isEmpty() ? generateSimpleTitle(message) : title;
        } catch (Exception e) {
            log.warn("AI生成标题失败，使用简单算法: {}", e.getMessage());
            return generateSimpleTitle(message);
        }
    }
}
