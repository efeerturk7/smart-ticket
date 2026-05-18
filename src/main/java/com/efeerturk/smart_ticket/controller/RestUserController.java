package com.efeerturk.smart_ticket.controller;

import com.efeerturk.smart_ticket.dto.request.UserRequest;
import com.efeerturk.smart_ticket.dto.response.UserResponse;
import com.efeerturk.smart_ticket.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/smartticket/user")
public class RestUserController {
    private final UserService userService;
    @PostMapping("/create")
    public RootEntity<UserResponse>createUser(@Valid @RequestBody UserRequest newUser){
        return RootEntity.ok(userService.createUser(newUser));
    }
    @GetMapping("/getUser")
    public RootEntity<UserResponse> getUserByEmail(@RequestBody String email){
        return RootEntity.ok(userService.getUserByEmail(email));
    }
}
