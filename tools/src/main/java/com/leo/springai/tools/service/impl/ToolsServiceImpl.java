package com.leo.springai.tools.service.impl;

import com.leo.springai.tools.service.TicketService;
import com.leo.springai.tools.service.ToolsService;
import jakarta.annotation.Resource;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

/**
 * @author leoyin
 * @version JDK 8
 * @date 2026/6/30
 * @description
 */
@Service
public class ToolsServiceImpl implements ToolsService {
    @Resource
    TicketService ticketService;

    @Tool(description = "退票")
    @Override
    public String testTools(@ToolParam(description = "预定号") String ticketCode, @ToolParam(description = "姓名") String name) {
        System.out.println("Tools 方法被调用，接收到参数：" + ticketCode + " --- " + name);
        String result = ticketService.cancel(ticketCode, name);
        System.out.println(result);
        return result;
    }
}
