package com.leo.springai.chatclient.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.leo.springai.chatclient.model.PlatAndModelOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.HashMap;

/**
 * @author leoyin
 * @version JDK 8
 * @date 2026/6/28
 * @description
 */
@RestController
@RequestMapping("/ai")
public class MorePlatformController {

    HashMap<String, ChatModel> platforms = new HashMap<>();

    public MorePlatformController(DashScopeChatModel dashScopeChatModel, DeepSeekChatModel deepSeekChatModel) {
        platforms.put("dashscope", dashScopeChatModel);
        platforms.put("deepseek", deepSeekChatModel);
    }

    @GetMapping(value = "/chat", produces = "text/stream;charset=UTF-8")
    public Flux<String> chat(String prompt, PlatAndModelOptions options) {
        // 获取请求参数
        String platform = options.getPlatform();
        String model = options.getModel();
        Double temperature = options.getTemperature();

        // 获取对应的模型
        ChatModel chatModel = platforms.get(platform);

        return ChatClient
                .builder(chatModel)
                .build()
                .prompt()
                .user(prompt)
                .options(ChatOptions
                        .builder()
                        .model(model)
                        .temperature(temperature)
                        .build())
                .stream()
                .content();
    }
}
