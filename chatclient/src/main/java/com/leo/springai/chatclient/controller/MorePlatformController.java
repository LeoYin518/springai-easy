package com.leo.springai.chatclient.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.leo.springai.chatclient.model.Address;
import com.leo.springai.chatclient.model.PlatAndModelOptions;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
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

    @Resource
    ChatMemory chatMemory;

    @GetMapping(value = "/memory", produces = "text/stream;charset=UTF-8")
    public Flux<String> memory(String prompt) {
        // 获取对应的模型
        ChatModel chatModel = platforms.get("dashscope");

        return ChatClient
                .builder(chatModel)
                .defaultAdvisors(PromptChatMemoryAdvisor.builder(chatMemory).build())
                .build()
                .prompt()
                .user(prompt)
                .advisors(config -> config.param(ChatMemory.CONVERSATION_ID, "sessionID-1"))
                .options(ChatOptions
                        .builder()
                        .model("qwen-plus")
                        .temperature(0.3)
                        .build())
                .stream()
                .content();
    }

    @GetMapping(value = "/memory2", produces = "text/stream;charset=UTF-8")
    public Flux<String> memory2(String prompt) {
        // 获取对应的模型
        ChatModel chatModel = platforms.get("dashscope");

        return ChatClient
                .builder(chatModel)
                .defaultAdvisors(PromptChatMemoryAdvisor.builder(chatMemory).build())
                .build()
                .prompt()
                .user(prompt)
                .advisors(config -> config.param(ChatMemory.CONVERSATION_ID, "sessionID-2"))
                .options(ChatOptions
                        .builder()
                        .model("qwen-plus")
                        .temperature(0.3)
                        .build())
                .stream()
                .content();
    }

    @GetMapping(value = "/structureOut")
    public Address structureOut() {
        // 获取对应的模型
        ChatModel chatModel = platforms.get("dashscope");
        return ChatClient
                .builder(chatModel)
                .build()
                .prompt()
                .system("请从以下信息中提取收货信息")
                .user("""
                        收货人：李白，电话：13322441111，地址：辽宁省沈阳市浑南区世纪路新秀街25号
                        """)
                .advisors(config -> config.param(ChatMemory.CONVERSATION_ID, "sessionID-1"))
                .options(ChatOptions
                        .builder()
                        .model("qwen-plus")
                        .temperature(0.3)
                        .build())
                .call()
                .entity(Address.class); // 大模型会去根据属性名理解、赋值
    }
}
