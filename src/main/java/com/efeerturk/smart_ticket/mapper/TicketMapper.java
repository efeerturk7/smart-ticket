package com.efeerturk.smart_ticket.mapper;

import com.efeerturk.smart_ticket.dto.request.TicketRequest;
import com.efeerturk.smart_ticket.dto.response.TicketResponse;
import com.efeerturk.smart_ticket.model.Ticket;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring",uses = {UserMapper.class,TicketCommentMapper.class})
public interface TicketMapper {
    Ticket toEntity(TicketRequest ticketRequest);
    TicketResponse toDto(Ticket ticket);
    List<TicketResponse> toDto(List<Ticket> ticketList);
}
