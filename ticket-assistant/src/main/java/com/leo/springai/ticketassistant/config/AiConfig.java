package com.leo.springai.ticketassistant.config;

import com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeChatProperties;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.memory.redis.RedisChatMemoryRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
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
    public ChatClient planningChatClient(DashScopeChatModel chatModel, DashScopeChatProperties options, ChatMemory chatMemory) {
        DashScopeChatOptions dashScopeChatOptions = DashScopeChatOptions.fromOptions(options.getOptions());
        dashScopeChatOptions.setTemperature(0.7);

        return ChatClient.builder(chatModel)
                .defaultSystem("""
                        # 票务助手任务拆分规则
                        ## 1. 要求
                        ### 1.1 根据用户内容识别任务
                        
                        ## 2. 任务
                        ### 2.1 JobType:退票（CANCEL） 要求用户提供姓名和预定号，或者从对话中提取
                        ### 2.2 JobType:查票（QUERY） 要求用户提供预定号，或者从对话中提取
                        ### 2.3 JobType:其他（OTHER）
                        """)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultOptions(dashScopeChatOptions)
                .build();
    }

    @Bean
    public ChatClient chatBotClient(DashScopeChatModel chatModel, DashScopeChatProperties options, ChatMemory chatMemory) {
        DashScopeChatOptions dashScopeChatOptions = DashScopeChatOptions.fromOptions(options.getOptions());
        dashScopeChatOptions.setTemperature(1.2);

        return ChatClient.builder(chatModel)
                .defaultSystem("""
                        你是[大飞航空]智能客服代理，请以友好的态度服务用户
                        """)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultOptions(dashScopeChatOptions)
                .build();
    }
}
