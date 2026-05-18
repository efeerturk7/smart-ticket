package com.efeerturk.smart_ticket.controller;

import com.efeerturk.smart_ticket.dto.request.TicketRequest;
import com.efeerturk.smart_ticket.dto.response.TicketResponse;
import com.efeerturk.smart_ticket.enums.Status;
import com.efeerturk.smart_ticket.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/smartticket/ticket")
public class RestTicketController {
    private final TicketService ticketService;
    @PostMapping("/create/{userId}")
    public RootEntity<TicketResponse>createTicket(@Valid @PathVariable(name = "userId") Long userId, @RequestBody TicketRequest ticketRequest){
        return RootEntity.ok(ticketService.createTicket(userId, ticketRequest));
    }
    @GetMapping("/get/{ticketId}")
    public RootEntity<TicketResponse> getTicketByTicketId(@PathVariable(name = "ticketId") Long ticketId) {
        return RootEntity.ok(ticketService.getTicketById(ticketId));
    }
    @DeleteMapping("/delete/{userId}/{ticketId}")
    public RootEntity<String> deleteTicketById(@PathVariable(name = "userId") Long userId,@PathVariable(name = "ticketId") Long ticketId) {
        return RootEntity.ok(ticketService.deleteTicketById(userId, ticketId));
    }
    @PatchMapping("/update/{ticketId}/status/{status}")
    public RootEntity<TicketResponse>updateTicketStatus(@PathVariable(name = "ticketId") Long ticketId,@PathVariable(name = "status") Status status){
        return RootEntity.ok(ticketService.updateTicketStatus(ticketId, status));
    }
    @PostMapping("/assign-ticket/{ticketId}/support/{supportId}")
    public RootEntity<TicketResponse>assignTicketToSupport(@PathVariable(name = "ticketId") Long ticketId,@PathVariable(name = "supportId") Long supportId){
        return RootEntity.ok(ticketService.assignTicketToSupport(ticketId, supportId));
    }
    @GetMapping("/getAll/{userId}")
    public RootEntity<List<TicketResponse>>getAllTicketsByUserId(@PathVariable(name = "userId") Long userId){
        return RootEntity.ok(ticketService.getAllTicketsByUserId(userId));
    }
}
