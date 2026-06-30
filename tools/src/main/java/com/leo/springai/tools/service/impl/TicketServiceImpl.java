package com.leo.springai.tools.service.impl;

import com.leo.springai.tools.service.TicketService;
import org.springframework.stereotype.Service;

/**
 * @author leoyin
 * @version JDK 8
 * @date 2026/6/30
 * @description
 */
@Service
public class TicketServiceImpl implements TicketService {
    @Override
    public String cancel(String ticketCode, String name) {
        return String.format("%s 取消订票，预定号 %s", name, ticketCode);
    }
}
