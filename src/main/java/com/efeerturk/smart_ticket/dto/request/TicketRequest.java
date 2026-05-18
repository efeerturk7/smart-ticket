package com.efeerturk.smart_ticket.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TicketRequest(
        @NotBlank
        String title,
        @NotBlank
        String description
) {
}
