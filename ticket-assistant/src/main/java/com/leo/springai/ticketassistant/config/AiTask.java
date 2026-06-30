package com.leo.springai.ticketassistant.config;

import java.util.Map;

/**
 * @author leoyin
 * @version JDK 8
 * @date 2026/6/30
 * @description
 */
public class AiTask {
    public record Task(TaskType taskType, Map<String, String> keyInfos) {

    }
    public enum TaskType {
        CANCEL,
        QUERY,
        OTHER
    }
}
