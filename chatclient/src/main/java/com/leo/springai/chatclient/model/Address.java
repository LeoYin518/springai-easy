package com.leo.springai.chatclient.model;

import lombok.Data;

/**
 * @author leoyin
 * @version JDK 8
 * @date 2026/6/29
 * @description
 */
@Data
public class Address {
    private String name;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String detail;
}
