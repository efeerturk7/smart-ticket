package com.efeerturk.smart_ticket.mapper;

import com.efeerturk.smart_ticket.dto.request.UserRequest;
import com.efeerturk.smart_ticket.dto.response.UserResponse;
import com.efeerturk.smart_ticket.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",uses = {TicketMapper.class,TicketCommentMapper.class})
public interface UserMapper {
    User toEntity(UserRequest userRequest);
    UserResponse toResponse(User user);

}
