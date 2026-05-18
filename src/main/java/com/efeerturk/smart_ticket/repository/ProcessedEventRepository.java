package com.efeerturk.smart_ticket.repository;

import com.efeerturk.smart_ticket.model.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, String> {
    boolean existsByMessageId(String messageId);
}
