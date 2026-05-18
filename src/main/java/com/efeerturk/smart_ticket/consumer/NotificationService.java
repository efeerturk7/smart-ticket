package com.efeerturk.smart_ticket.consumer;

import com.efeerturk.smart_ticket.event.TicketCreatedEvent;

public interface NotificationService {
    void consumeTicketEvent(TicketCreatedEvent event);
    void consumeDeadLetterTopic(TicketCreatedEvent event);
}
