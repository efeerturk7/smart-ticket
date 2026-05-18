package com.efeerturk.smart_ticket.dto.request;

import jakarta.validation.constraints.NotBlank;
public record RegisterRequest(
        @NotBlank
        String username,
        @NotBlank
        String email,
        @NotBlank
        String password
) {

}