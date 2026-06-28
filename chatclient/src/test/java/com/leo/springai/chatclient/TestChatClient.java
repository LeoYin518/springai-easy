package com.leo.springai.chatclient;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author leoyin
 * @version JDK 8
 * @date 2026/6/28
 * @description
 */
@SpringBootTest
public class TestChatClient {

    /**
     * 非流式
     */
    @Test
    public void testChatClient(@Autowired ChatClient.Builder chatClientBuilder) {
        ChatClient chatClient = chatClientBuilder.build();
        String content = chatClient.prompt()
                .user("你好,你是谁")
                .call()
                .content();
        System.out.println(content);
    }

    @Test
    public void testChatClientStream(@Autowired DashScopeChatModel chatModel) {
        // 指定对应模型
        ChatClient chatClient = ChatClient.builder(chatModel).build();

        chatClient.prompt()
                .system("你是一个浪漫注意诗人")
                .user("请用简短的、富有诗意的一句话来描述一个午夜")
                .options(DashScopeChatOptions.builder()
                        .withTemperature(0.8)
                        .withModel("qwen-plus")
                        .withStop(List.of("政治", "最后总结一下"))
                        .build())
                .stream()
                .content()
                .toIterable()
                .forEach(System.out::print);
    }
}
