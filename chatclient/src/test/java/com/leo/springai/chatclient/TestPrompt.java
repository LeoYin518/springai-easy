package com.leo.springai.chatclient;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

/**
 * @author leoyin
 * @version JDK 8
 * @date 2026/6/28
 * @description
 */
@SpringBootTest
public class TestPrompt {
    /**
     * system prompt 使用方式 1
     */
    @Test
    public void testChatClientPrompt1(@Autowired DashScopeChatModel chatModel) {
        ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultSystem("你是一个浪漫主义诗人")
                .build();
        chatClient.prompt()
                .user("请用简短的、富有诗意的一句话来描述一个午夜")
                .options(DashScopeChatOptions.builder().build())
                .stream()
                .content()
                .toIterable()
                .forEach(System.out::print);
    }

    /**
     * system prompt 使用方式 2
     */
    @Test
    public void testChatClientPrompt2(@Autowired DashScopeChatModel chatModel) {
        ChatClient chatClient = ChatClient.builder(chatModel).build();
        chatClient.prompt()
                .system("你是一个浪漫主义诗人")
                .user("请用简短的、富有诗意的一句话来描述一个午夜")
                .options(DashScopeChatOptions.builder().build())
                .stream()
                .content()
                .toIterable()
                .forEach(System.out::print);
    }

    /**
     * system prompt 使用方式 3：伪系统提示词，使用 user prompt
     */
    @Test
    public void testChatClientPrompt3(@Autowired DashScopeChatModel chatModel) {
        ChatClient chatClient = ChatClient.builder(chatModel).build();
        chatClient.prompt()
                .user("""
                        你是一个浪漫主义诗人
                        请用简短的、富有诗意的一句话来描述一个午夜
                        """)
                .options(DashScopeChatOptions.builder().build())
                .stream()
                .content()
                .toIterable()
                .forEach(System.out::print);
    }

    /**
     * system prompt 使用动态数据
     */
    @Test
    public void testChatClientPrompt4(@Autowired DashScopeChatModel chatModel) {
        ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultSystem("你是一个浪漫主义诗人：{name}")
                .build();
        chatClient.prompt()
                .system(p -> p.param("name", "李白"))
                .user("你是谁")
                .options(DashScopeChatOptions.builder().build())
                .stream()
                .content()
                .toIterable()
                .forEach(System.out::print);
    }

    /**
     * system prompt 使用动态数据
     */
    @Test
    public void testChatClientPrompt5(@Autowired DashScopeChatModel chatModel) {
        ChatClient chatClient = ChatClient.builder(chatModel)
                .build();
        chatClient.prompt()
                .system(p -> p.text("""
                             你是一个浪漫主义诗人：{name}
                        """).param("name", "李白"))
                .user("你是谁")
                .options(DashScopeChatOptions.builder().build())
                .stream()
                .content()
                .toIterable()
                .forEach(System.out::print);
    }

    /**
     * user prompt 使用动态数据
     */
    @Test
    public void testChatClientPrompt6(@Autowired DashScopeChatModel chatModel) {
        ChatClient chatClient = ChatClient.builder(chatModel).build();
        chatClient.prompt()
                .user(u -> u.text("""
                        你是一个现实主义诗人：{name}
                        请问你是谁
                        """
                ).param("name", "杜甫"))
                .options(DashScopeChatOptions.builder().build())
                .stream()
                .content()
                .toIterable()
                .forEach(System.out::print);
    }

    /**
     * 加载 prompt
     */
    @Test
    public void testChatClientPrompt7(@Autowired DashScopeChatModel chatModel, @Value("classpath:/files/prompt.st") Resource resource) {
        ChatClient chatClient = ChatClient.builder(chatModel)
                .build();
        chatClient.prompt()
                .system(p -> p.text(resource).param("name", "李白"))
                .user("你是谁")
                .options(DashScopeChatOptions.builder().build())
                .stream()
                .content()
                .toIterable()
                .forEach(System.out::print);
    }
}
