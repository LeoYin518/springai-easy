package com.leo.springai.tools.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @author leoyin
 * @version JDK 8
 * @date 2026/6/30
 * @description
 */
@RestController
@RequestMapping("/tools")
public class ToolsController {

    @Resource
    ChatClient chatClient;

    @GetMapping(value = "/test", produces = "text/stream;charset=utf-8")
    public Flux<String> testTools(String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .stream()
                .content();
    }
}
