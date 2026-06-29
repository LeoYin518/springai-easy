package com.leo.springai.chatclient.advisor;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

/**
 * @author leoyin
 * @version JDK 8
 * @date 2026/6/29
 * @description
 */
public class ReReadingAdvisor implements BaseAdvisor {
    public static final String DEFAULT_USER_TEXT_ADVISE = """
            Question: {re2_input_query}
            Read the question again: {re2_input_query} (Let's think step by step)
            """;

    /**
     * 到达模型前执行
     * @param chatClientRequest
     * @param advisorChain
     * @return
     */
    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        // 获取提示词
        String contents = chatClientRequest.prompt().getContents();
        // 重读
        String re2InputQuery = PromptTemplate.builder()
                .template(DEFAULT_USER_TEXT_ADVISE)
                .build()
                .render(Map.of("re2_input_query", contents));
        ChatClientRequest clientRequest = chatClientRequest
                .mutate()
                .prompt(Prompt.builder()
                        .content(re2InputQuery)
                        .build())
                .build();
        return clientRequest;
    }

    /**
     * 模型回答做出响应后执行
     * @param chatClientResponse
     * @param advisorChain
     * @return
     */
    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        return chatClientResponse;
    }

    @Override
    public int getOrder() {
        return 0; // advisor 顺序，数值越大，优先级越低
    }
}
