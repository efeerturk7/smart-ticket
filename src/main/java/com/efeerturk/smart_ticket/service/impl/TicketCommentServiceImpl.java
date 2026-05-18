package com.efeerturk.smart_ticket.service.impl;

import com.efeerturk.smart_ticket.dto.request.TicketCommentRequest;
import com.efeerturk.smart_ticket.dto.response.TicketCommentResponse;
import com.efeerturk.smart_ticket.enums.MessageType;
import com.efeerturk.smart_ticket.exception.BaseException;
import com.efeerturk.smart_ticket.exception.ErrorMessage;
import com.efeerturk.smart_ticket.mapper.TicketCommentMapper;
import com.efeerturk.smart_ticket.model.Ticket;
import com.efeerturk.smart_ticket.model.TicketComment;
import com.efeerturk.smart_ticket.repository.TicketCommentRepository;
import com.efeerturk.smart_ticket.repository.TicketRepository;
import com.efeerturk.smart_ticket.repository.UserRepository;
import com.efeerturk.smart_ticket.service.TicketCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketCommentServiceImpl implements TicketCommentService {
    private final TicketCommentRepository ticketCommentRepository;
    private final TicketCommentMapper ticketCommentMapper;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    @Override
    @Transactional
    @CacheEvict(value = "ticket_comments", key = "#ticketId")
    public TicketCommentResponse addComment(Long ticketId, TicketCommentRequest request){
        log.info("Attempting to add comment for ticket ID: {}", ticketId);

        Ticket dbTicket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> {
                    log.error("Ticket not found with ID: {}", ticketId);
                    return new BaseException(new ErrorMessage(MessageType.TICKET_NOT_FOUND, ticketId.toString()));
                });

        if (!userRepository.existsById(request.userId())) {
            log.error("User not found with ID: {}", request.userId());

            throw new BaseException(new ErrorMessage(MessageType.COMMENT_NOT_ADD, ticketId.toString()));
        }

        if (!request.userId().equals(dbTicket.getId())) {
            log.warn("User ID: {} is not the owner of the ticket ID: {}", request.userId(), ticketId);
            throw new BaseException(new ErrorMessage(MessageType.COMMENT_NOT_ADD, ticketId.toString()));
        }

        TicketComment comment = ticketCommentMapper.toEntity(request);
        TicketComment addedComment = ticketCommentRepository.save(comment);

        log.info("Successfully added comment with ID: {} to ticket ID: {}", addedComment.getId(), ticketId);
        return ticketCommentMapper.toResponse(addedComment);
    }

    @Override
    @Cacheable(value = "ticket_comments", key = "#ticketId")
    public TicketCommentResponse getCommentsByTicketId(Long ticketId){
        log.info("Fetching comments from Database for ticket ID: {}", ticketId);

        if (!ticketRepository.existsById(ticketId)){
            log.error("Ticket not found with ID: {}", ticketId);
            throw new BaseException(new ErrorMessage(MessageType.TICKET_NOT_FOUND, ticketId.toString()));
        }

        TicketComment comment = ticketCommentRepository.findById(ticketId).orElseThrow();
        return ticketCommentMapper.toResponse(comment);
    }
}
