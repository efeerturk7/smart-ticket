package com.efeerturk.smart_ticket.dto.response;

import com.efeerturk.smart_ticket.enums.Priority;
import com.efeerturk.smart_ticket.enums.Status;

import java.time.LocalDateTime;

public record TicketResponse(
        Long id,
        String title,
        String description,
        Status status,
        Priority priority,
        LocalDateTime createdAt

) {
}
