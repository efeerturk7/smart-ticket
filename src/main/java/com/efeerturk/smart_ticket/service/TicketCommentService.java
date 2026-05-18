package com.efeerturk.smart_ticket.service;

import com.efeerturk.smart_ticket.dto.request.TicketCommentRequest;
import com.efeerturk.smart_ticket.dto.response.TicketCommentResponse;

public interface TicketCommentService {
    TicketCommentResponse addComment(Long ticketId, TicketCommentRequest request);
    TicketCommentResponse getCommentsByTicketId(Long ticketId);
}
