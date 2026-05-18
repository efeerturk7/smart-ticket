package com.efeerturk.smart_ticket.service;

import com.efeerturk.smart_ticket.dto.request.UserRequest;
import com.efeerturk.smart_ticket.dto.response.UserResponse;

public interface UserService {
    UserResponse getUserByEmail(String email);
    UserResponse createUser(UserRequest newUser);
}
