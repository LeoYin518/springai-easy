package com.leo.springai.chatclient;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.leo.springai.chatclient.advisor.ReReadingAdvisor;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * @author leoyin
 * @version JDK 8
 * @date 2026/6/28
 * @description
 */
@SpringBootTest
public class TestAdvisor {
    /**
     * 加载 prompt
     */
    @Test
    public void testChatClientAdvisor(@Autowired DashScopeChatModel chatModel, @Value("classpath:/files/prompt.st") Resource resource) {
        ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
        chatClient.prompt()
                .system(p -> p.text(resource).param("name", "李白"))
                .user("杜甫怎么样")
                .options(DashScopeChatOptions.builder().build())
                .stream()
                .content()
                .toIterable()
                .forEach(System.out::print);
    }

    @Test
    public void testChatClientAdvisor2(@Autowired DashScopeChatModel chatModel, @Value("classpath:/files/prompt.st") Resource resource) {
        ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor(), new SafeGuardAdvisor(List.of("杜甫")))
                .build();
        chatClient.prompt()
                .system(p -> p.text(resource).param("name", "李白"))
                .user("杜甫怎么样")
                .options(DashScopeChatOptions.builder().build())
                .stream()
                .content()
                .toIterable()
                .forEach(System.out::print);
    }

    @Test
    public void testChatClientAdvisor3(@Autowired DashScopeChatModel chatModel, @Value("classpath:/files/prompt.st") Resource resource) {
        ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor(), new ReReadingAdvisor())
                .build();
        chatClient.prompt()
                .system(p -> p.text(resource).param("name", "李白"))
                .user("杜甫怎么样")
                .options(DashScopeChatOptions.builder().build())
                .stream()
                .content()
                .toIterable()
                .forEach(System.out::print);
    }
}
