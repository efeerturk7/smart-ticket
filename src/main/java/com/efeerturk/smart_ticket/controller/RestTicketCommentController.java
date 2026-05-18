package com.efeerturk.smart_ticket.controller;

import com.efeerturk.smart_ticket.dto.request.TicketCommentRequest;
import com.efeerturk.smart_ticket.dto.response.TicketCommentResponse;
import com.efeerturk.smart_ticket.service.TicketCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/smartticket/ticket/comment")
public class RestTicketCommentController {
    private final TicketCommentService ticketCommentService;
    @PostMapping("/add/{ticketId}")
    public RootEntity<TicketCommentResponse>  addComment(@Valid @PathVariable(name = "ticketId") Long ticketId,@RequestBody TicketCommentRequest request){
        return RootEntity.ok(ticketCommentService.addComment(ticketId, request));
    }
    @GetMapping("/get/{ticketId}")
    public RootEntity<TicketCommentResponse> getCommentsByTicketId(@PathVariable(name = "ticketId") Long ticketId){
        return RootEntity.ok(ticketCommentService.getCommentsByTicketId(ticketId));
    }
}
