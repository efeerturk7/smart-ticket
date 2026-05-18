package com.efeerturk.smart_ticket.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TicketCommentRequest(
        @NotNull
        Long userId,
        @NotBlank
        String content
) {
}
