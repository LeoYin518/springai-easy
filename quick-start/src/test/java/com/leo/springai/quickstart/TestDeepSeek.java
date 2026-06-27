package com.leo.springai.quickstart;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author leoyin
 * @version JDK 8
 * @date 2026/6/27
 * @description
 */
@SpringBootTest
public class TestDeepSeek {
    /**
     * 非流式调用
     */
    @Test
    public void testDeepseek(@Autowired DeepSeekChatModel chatModel) {
        String content = chatModel.call("你好，你是谁");
        System.out.println(content);
    }

    /**
     * 流式调用 - 用户体验更好
     */
    @Test
    public void testDeepseekStream(@Autowired DeepSeekChatModel chatModel) {
        Flux<String> stream = chatModel.stream("你好，你是谁");
        stream.toIterable().forEach(System.out::print);
    }

    /**
     * 通用配置
     *  temperature：值越小，回答越保守（让概率高的更高、概率小的更小，注意：即使是 0 ，也不代表完全复现）
     *         0.0 ~ 0.2 代码，数学，严谨问答
     *         0.3 ~ 0.6 聊天机器人、日常摘要、辅助写作业
     *         0.7 ~ 1.0 创作内容、广告文案、标题生成
     *         1.1 ~ 1.5 头脑风暴、灵感
     *  model: 指定其他模型
     *  maxTokens：模型最多生成 token 数
     *  stop: 截断你不想生成的内容
     */
    @Test
    public void testDeepseekOptions(@Autowired DeepSeekChatModel chatModel) {
        DeepSeekChatOptions options = DeepSeekChatOptions.builder()
                .temperature(0.8)
                .model("deepseek-v4-pro")
                .stop(List.of("政治", "最后总结一下"))
                .build();
        Prompt prompt = new Prompt("请用简短的、富有诗意的一句话来描述一个清晨", options);
        ChatResponse res = chatModel.call(prompt);
        System.out.println(res.getResult().getOutput().getText());
    }


    @Autowired
    private ChatClient.Builder chatClientBuilder;

    @Test
    public void testDeepseekOptionsStream() {
        ChatClient chatClient = chatClientBuilder.build();

        chatClient.prompt()
                .system("你是一个浪漫注意诗人")
                .user("请用简短的、富有诗意的一句话来描述一个午夜")
                .options(DeepSeekChatOptions.builder()
                        .temperature(0.8)
                        .model("deepseek-v4-pro")
                        .build())
                .stream()
                .content()
                .toIterable()
                .forEach(System.out::print);
    }


}
