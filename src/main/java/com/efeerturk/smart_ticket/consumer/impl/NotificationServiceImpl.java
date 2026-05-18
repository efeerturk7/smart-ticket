package com.efeerturk.smart_ticket.consumer.impl;

import com.efeerturk.smart_ticket.consumer.NotificationService;
import com.efeerturk.smart_ticket.event.TicketCreatedEvent;
import com.efeerturk.smart_ticket.model.ProcessedEvent;
import com.efeerturk.smart_ticket.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final ProcessedEventRepository processedEventRepository;

    @Transactional
    @KafkaListener(topics = "ticket-events-topic", groupId = "smart-ticket-notification-group")
    @Override
    public void consumeTicketEvent(TicketCreatedEvent event) {

        log.info("Received message from Kafka. Message ID: {}", event.messageId());

        // 1. IDEMPOTENT CONSUMER (Check-and-Set Pattern)
        if (processedEventRepository.existsByMessageId(event.messageId())) {
            log.warn("Message ID {} has already been processed. Skipping to prevent duplicate.", event.messageId());
            return;
        }

        // 2. EXCEPTION TRANSLATION & RETRY SIMULATION

        if (event.title().contains("fatal")) {
            log.error("Fatal error detected! Moving directly to DLT without retry.");
            throw new IllegalArgumentException("Invalid data format! Do not retry.");
        }

        if (event.title().contains("timeout")) {
            log.warn("Network timeout simulation! This will trigger the FixedBackOff retry mechanism.");
            throw new RuntimeException("Temporary network failure! Retrying...");
        }


        log.info("Processing ticket creation notification...");
        log.info("Sending email to Customer ID: {} for Ticket ID: {}", event.userId(), event.ticketId());
        log.info("Ticket Status: {}", event.status());


        ProcessedEvent processedEvent = new ProcessedEvent(event.messageId(), LocalDateTime.now());
        processedEventRepository.save(processedEvent);

        log.info("Message processed successfully and ID saved to database.");
    }


    // 5. DEAD LETTER TOPIC (DLT) LISTENER

    @KafkaListener(topics = "ticket-events-topic.DLT", groupId = "smart-ticket-dlt-group")
    @Override
    public void consumeDeadLetterTopic(TicketCreatedEvent event) {
        log.error("DEAD LETTER TOPIC (DLT) ALERT!");
        log.error("Message ID {} failed completely and requires manual inspection.", event.messageId());
        log.error("Ticket details - ID: {}, Title: {}", event.ticketId(), event.title());
    }
}
