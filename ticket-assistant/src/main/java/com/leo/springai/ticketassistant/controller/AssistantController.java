package com.leo.springai.ticketassistant.controller;

import com.leo.springai.ticketassistant.config.AiTask;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.sql.SQLOutput;
import java.util.Objects;

/**
 * @author leoyin
 * @version JDK 8
 * @date 2026/6/30
 * @description
 */
@RestController
@RequestMapping("/assistant")
public class AssistantController {

    @Resource
    ChatClient planningChatClient;

    @Resource
    ChatClient chatBotClient;

    @Resource
    ChatMemory chatMemory;

    @GetMapping(value = "/serve", produces = "text/stream;charset=UTF-8")
    public Flux<String> serve(String prompt) {
        // 创建一个用于接收多条消息的 Sink
        Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
        // 推送消息
        sink.tryEmitNext("正在规划任务...");

        new Thread(() -> {
            AiTask.Task task = planningChatClient.prompt()
                    .user(prompt)
                    .advisors(config -> config.param(ChatMemory.CONVERSATION_ID, "sessionId-1"))
                    .call()
                    .entity(AiTask.Task.class);

            switch (Objects.requireNonNull(task).taskType()) {
                case CANCEL -> {
                    System.out.println(task);
                    if (task.keyInfos().isEmpty()) {
                        sink.tryEmitNext("请输入【姓名】和【订单号】：");
                    } else {
                        sink.tryEmitNext("退票成功");
                    }
                    sink.tryEmitComplete();
                }
                case QUERY -> {
                    System.out.println(task);
                    if (task.keyInfos().isEmpty()) {
                        sink.tryEmitNext("查询预定信息：xxxxx");
                    }
                    sink.tryEmitNext("查询成功");
                    sink.tryEmitComplete();
                }
                case OTHER -> {
                    Flux<String> content = chatBotClient.prompt().user(prompt).stream().content();
                    content.doOnNext(sink::tryEmitNext) // 推送每条 AI 流内容
                            .doOnComplete(sink::tryEmitComplete)
                            .subscribe();
                }
                default -> {
                    System.out.println(task);
                    sink.tryEmitNext("我没有理解你的意思....");
                    sink.tryEmitComplete();
                }
            }
        }).start();

        return sink.asFlux();
    }
}
