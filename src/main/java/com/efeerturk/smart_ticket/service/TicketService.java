package com.efeerturk.smart_ticket.service;

import com.efeerturk.smart_ticket.dto.request.TicketRequest;
import com.efeerturk.smart_ticket.dto.response.TicketResponse;
import com.efeerturk.smart_ticket.enums.Status;


import java.util.List;

public interface TicketService {
     TicketResponse createTicket(Long customerId, TicketRequest ticketRequest);
     TicketResponse getTicketById(Long ticketId);
     List<TicketResponse> getAllTicketsByUserId(Long userId);
     List<TicketResponse> getAllOpenTickets();
     TicketResponse assignTicketToSupport(Long ticketId,Long supportId);
     TicketResponse updateTicketStatus(Long ticketId, Status status);
     String deleteTicketById(Long customerId,Long ticketId);
}
