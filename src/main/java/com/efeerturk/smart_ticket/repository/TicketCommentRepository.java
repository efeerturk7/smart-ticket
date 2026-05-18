package com.efeerturk.smart_ticket.repository;

import com.efeerturk.smart_ticket.model.TicketComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketCommentRepository extends JpaRepository<TicketComment, Long> {
}
