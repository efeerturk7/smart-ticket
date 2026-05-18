package com.efeerturk.smart_ticket.repository;


import com.efeerturk.smart_ticket.model.Ticket;

import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;


public interface TicketRepository extends JpaRepository<Ticket,Long> {
    List<Ticket> getAllTicketsByUserId(Long userId);

}
