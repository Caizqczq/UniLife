package com.unilife.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@Slf4j
@RequestMapping("/ai")
@Tag(name = "AI对话")
@AllArgsConstructor
public class AiController {
    private final ChatClient chatClient;

    @RequestMapping(value = "/chat",produces = "text/html;charset=UTF-8")
    public Flux<String>chat(String prompt,String chatId){
        return chatClient.prompt(prompt)
                .stream()
                .content();
    }


}
