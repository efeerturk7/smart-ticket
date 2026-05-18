package com.efeerturk.smart_ticket.jwt;

import com.efeerturk.smart_ticket.dto.request.LoginRequest;
import com.efeerturk.smart_ticket.dto.request.RegisterRequest;
import com.efeerturk.smart_ticket.dto.response.AuthResponse;
import com.efeerturk.smart_ticket.enums.MessageType;
import com.efeerturk.smart_ticket.enums.Role;
import com.efeerturk.smart_ticket.exception.BaseException;
import com.efeerturk.smart_ticket.exception.ErrorMessage;
import com.efeerturk.smart_ticket.model.User;
import com.efeerturk.smart_ticket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    public AuthResponse register(RegisterRequest request){
        User user=new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setUsername(request.username());
        user.setRole(Role.CUSTOMER);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
        String jwtToken = jwtService.generateToken(new CustomUserDetails(user));
        return new AuthResponse(jwtToken);
    }
    public AuthResponse authenticate(LoginRequest request){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        User user=userRepository.findByEmail(request.email());
        if (user==null){
            throw new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, request.email()));
        }
        String jwtToken = jwtService.generateToken(new CustomUserDetails(user));
        return new AuthResponse(jwtToken);
    }
}
