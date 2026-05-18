package com.efeerturk.smart_ticket.dto.response;

import com.efeerturk.smart_ticket.enums.Role;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String username,
        String email,
        Role role,
        LocalDateTime createdAt
) {
}
