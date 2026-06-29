package com.leo.springai.chatclient.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
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
    @Bean
    ChatMemory chatMemory(JdbcChatMemoryRepository repository) {
        return MessageWindowChatMemory
                .builder()
                .maxMessages(3) // 只存 10 条记忆，淘汰策略：先进先出
                .chatMemoryRepository(repository)
                .build();
    }
//    @Bean
//    ChatMemory chatMemory(ChatMemoryRepository repository) {
//        return MessageWindowChatMemory
//                .builder()
//                .maxMessages(3) // 只存 10 条记忆，淘汰策略：先进先出
//                .chatMemoryRepository(repository)
//                .build();
//    }
}
