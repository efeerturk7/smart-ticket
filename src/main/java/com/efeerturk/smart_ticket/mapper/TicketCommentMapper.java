package com.efeerturk.smart_ticket.mapper;

import com.efeerturk.smart_ticket.dto.request.TicketCommentRequest;
import com.efeerturk.smart_ticket.dto.response.TicketCommentResponse;
import com.efeerturk.smart_ticket.model.TicketComment;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring",uses = {UserMapper.class, TicketMapper.class})
public interface TicketCommentMapper {
    TicketComment toEntity(TicketCommentRequest request);
    TicketCommentResponse toResponse(TicketComment entity);
}
