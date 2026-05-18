package com.efeerturk.smart_ticket.dto.response;

public record TicketCommentResponse(
        Long ticketId,
        Long userId,
        String content
) {
}
