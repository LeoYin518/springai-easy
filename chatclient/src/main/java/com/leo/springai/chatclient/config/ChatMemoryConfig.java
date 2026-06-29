package com.leo.springai.chatclient.config;

import com.alibaba.cloud.ai.memory.redis.RedisChatMemoryRepository;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author leoyin
 * @version JDK 8
 * @date 2026/6/29
 * @description
 */
@Configuration
public class ChatMemoryConfig {

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
                .maxMessages(10) // 只存 10 条记忆，淘汰策略：先进先出
                .chatMemoryRepository(repository)
                .build();
    }

//    @Bean
//    ChatMemory chatMemory(JdbcChatMemoryRepository repository) {
//        return MessageWindowChatMemory
//                .builder()
//                .maxMessages(10) // 只存 10 条记忆，淘汰策略：先进先出
//                .chatMemoryRepository(repository)
//                .build();
//    }

//    @Bean
//    ChatMemory chatMemory(ChatMemoryRepository repository) {
//        return MessageWindowChatMemory
//                .builder()
//                .maxMessages(10) // 只存 10 条记忆，淘汰策略：先进先出
//                .chatMemoryRepository(repository)
//                .build();
//    }
}
