package com.efeerturk.smart_ticket.ai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

public class AiToolsConfig {
    public record TicketStatusRequest(Long ticketId) {}
    public record TicketStatusResponse(String status, String assignedTo) {}

    @Bean
    @Description("Verilen bilet ID'sine (ticketId) göre biletin güncel durumunu veritabanından çeker.")
    public Function<TicketStatusRequest, TicketStatusResponse> ticketStatusTool() {
        return request -> {
            System.out.println("🤖 YAPAY ZEKA TOOL KULLANDI: Bilet sorgulanıyor ID: " + request.ticketId());
            return new TicketStatusResponse("IN_PROGRESS", "Bahadir Efe (AI Agent)");
        };
    }
}
