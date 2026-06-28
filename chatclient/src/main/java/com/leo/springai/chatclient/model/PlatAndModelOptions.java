package com.leo.springai.chatclient.model;

import lombok.Data;

/**
 * @author leoyin
 * @version JDK 8
 * @date 2026/6/28
 * @description
 */
@Data
public class PlatAndModelOptions {
    private String platform;
    private String model;
    private Double temperature;
}
