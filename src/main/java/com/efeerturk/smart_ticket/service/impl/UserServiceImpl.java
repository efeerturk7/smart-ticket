package com.efeerturk.smart_ticket.service.impl;

import com.efeerturk.smart_ticket.dto.request.UserRequest;
import com.efeerturk.smart_ticket.dto.response.UserResponse;
import com.efeerturk.smart_ticket.enums.MessageType;
import com.efeerturk.smart_ticket.exception.BaseException;
import com.efeerturk.smart_ticket.exception.ErrorMessage;
import com.efeerturk.smart_ticket.mapper.UserMapper;
import com.efeerturk.smart_ticket.model.User;
import com.efeerturk.smart_ticket.repository.UserRepository;
import com.efeerturk.smart_ticket.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @Override
    @Transactional
    public UserResponse createUser(UserRequest newUser){
        log.info("Attempting to create user with username: {}", newUser.username());

        if (userRepository.findByUsername(newUser.username()).isPresent()){
            log.warn("User already exists with username: {}", newUser.username());
            throw new BaseException(new ErrorMessage(MessageType.USER_ALREADY_EXISTS, newUser.username()));
        }

        User user = userMapper.toEntity(newUser);
        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getId());

        return userMapper.toResponse(savedUser);
    }

    @Override
    @Cacheable(value = "users", key = "#email")
    public UserResponse getUserByEmail(String email){
        log.info("Fetching user from Database for email: {}", email);

        User dbUser = userRepository.findByEmail(email);
        if (dbUser == null){
            log.warn("User not found with email: {}", email);
            throw new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, email));
        }else {
            return userMapper.toResponse(dbUser);
        }



    }



}
