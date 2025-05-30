package com.unilife.controller;

import com.unilife.common.result.Result;
import com.unilife.model.dto.AiCreateSessionDTO;
import com.unilife.model.dto.AiSendMessageDTO;
import com.unilife.model.dto.AiUpdateSessionDTO;
import com.unilife.model.vo.AiCreateSessionVO;
import com.unilife.model.vo.AiMessageHistoryVO;
import com.unilife.model.vo.AiSessionListVO;
import com.unilife.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@Slf4j
@RequestMapping("/ai")
@Tag(name = "AI辅助学习")
@RequiredArgsConstructor
public class AiController {
    
    private final AiService aiService;

    @Operation(summary = "发送消息给AI")
    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> sendMessage(@RequestBody AiSendMessageDTO sendMessageDTO) {
        log.info("发送消息给AI: {}", sendMessageDTO.getMessage());
        return aiService.sendMessage(sendMessageDTO);
    }

    @Operation(summary = "获取聊天会话列表")
    @GetMapping("/sessions")
    public Result<AiSessionListVO> getSessionList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") Integer size) {
        
        log.info("获取会话列表，页码: {}, 每页大小: {}", page, size);
        return aiService.getSessionList(page, size);
    }

    @Operation(summary = "获取会话消息历史")
    @GetMapping("/sessions/{sessionId}/messages")
    public Result<AiMessageHistoryVO> getSessionMessages(
            @Parameter(description = "会话ID") @PathVariable String sessionId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "50") Integer size) {
        
        log.info("获取会话消息历史，会话ID: {}, 页码: {}, 每页大小: {}", sessionId, page, size);
        return aiService.getSessionMessages(sessionId, page, size);
    }

    @Operation(summary = "创建聊天会话")
    @PostMapping("/sessions")
    public Result<AiCreateSessionVO> createSession(@RequestBody AiCreateSessionDTO createSessionDTO) {
        log.info("创建聊天会话: {}", createSessionDTO.getSessionId());
        return aiService.createSession(createSessionDTO);
    }

    @Operation(summary = "更新会话标题")
    @PutMapping("/sessions/{sessionId}")
    public Result<Void> updateSessionTitle(
            @Parameter(description = "会话ID") @PathVariable String sessionId,
            @RequestBody AiUpdateSessionDTO updateSessionDTO) {
        
        log.info("更新会话标题，会话ID: {}, 新标题: {}", sessionId, updateSessionDTO.getTitle());
        return aiService.updateSessionTitle(sessionId, updateSessionDTO);
    }

    @Operation(summary = "清空会话消息")
    @DeleteMapping("/sessions/{sessionId}/messages")
    public Result<Void> clearSessionMessages(@Parameter(description = "会话ID") @PathVariable String sessionId) {
        log.info("清空会话消息，会话ID: {}", sessionId);
        return aiService.clearSessionMessages(sessionId);
    }

    @Operation(summary = "删除会话")
    @DeleteMapping("/sessions/{sessionId}")
    public Result<Void> deleteSession(@Parameter(description = "会话ID") @PathVariable String sessionId) {
        log.info("删除会话，会话ID: {}", sessionId);
        return aiService.deleteSession(sessionId);
    }
}
