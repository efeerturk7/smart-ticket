package com.efeerturk.smart_ticket.event;

import java.time.LocalDateTime;

public record TicketCreatedEvent(
        String messageId,
        Long ticketId,
        Long userId,
        String title,
        String status,
        LocalDateTime createdAt
) {
}
