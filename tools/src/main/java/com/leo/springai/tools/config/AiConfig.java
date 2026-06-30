package com.leo.springai.tools.config;

import com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeChatProperties;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.memory.redis.RedisChatMemoryRepository;
import com.leo.springai.tools.service.ToolsService;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author leoyin
 * @version JDK 8
 * @date 2026/6/30
 * @description
 */
@Configuration
public class AiConfig {

    @Resource
    ToolsService toolsService;

    @Value("${spring.ai.memory.redis.host}")
    String host;
    @Value("${spring.ai.memory.redis.port}")
    int port;
    @Value("${spring.ai.memory.redis.timeout}")
    int timeout;
    @Value("${spring.ai.memory.redis.password}")
    String password;

    @Bean
    public RedisChatMemoryRepository redisChatMemoryRepository() {
        return RedisChatMemoryRepository.builder()
                .host(host)
                .password(password)
                .port(port)
                .timeout(timeout)
                .build();
    }

    @Bean
    ChatMemory chatMemory(RedisChatMemoryRepository repository) {
        return MessageWindowChatMemory
                .builder()
                .maxMessages(50)
                .chatMemoryRepository(repository)
                .build();
    }

    /**
     * 任务规划的 ChatClient
     * @param chatModel
     * @param options
     * @param chatMemory
     * @return
     */
    @Bean
    public ChatClient chatClient(DashScopeChatModel chatModel, DashScopeChatProperties options, ChatMemory chatMemory) {
        DashScopeChatOptions dashScopeChatOptions = DashScopeChatOptions.fromOptions(options.getOptions());

        return ChatClient.builder(chatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultOptions(dashScopeChatOptions)
                .defaultTools(toolsService) // 告诉大模型可以使用哪些 tools
                .build();
    }
}
