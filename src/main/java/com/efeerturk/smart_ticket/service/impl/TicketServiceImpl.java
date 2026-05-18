package com.efeerturk.smart_ticket.service.impl;

import com.efeerturk.smart_ticket.producer.KafkaProducerConfig;
import com.efeerturk.smart_ticket.dto.request.TicketRequest;
import com.efeerturk.smart_ticket.dto.response.TicketResponse;

import com.efeerturk.smart_ticket.enums.MessageType;
import com.efeerturk.smart_ticket.enums.Status;
import com.efeerturk.smart_ticket.event.TicketCreatedEvent;
import com.efeerturk.smart_ticket.exception.BaseException;
import com.efeerturk.smart_ticket.exception.ErrorMessage;
import com.efeerturk.smart_ticket.mapper.TicketMapper;
import com.efeerturk.smart_ticket.model.Ticket;

import com.efeerturk.smart_ticket.repository.TicketRepository;
import com.efeerturk.smart_ticket.repository.UserRepository;
import com.efeerturk.smart_ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketServiceImpl implements TicketService {
    private final TicketMapper ticketMapper;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final RedissonClient redissonClient;
    private final RedisTemplate redisTemplate;
    private final KafkaTemplate<String,Object> kafkaTemplate;

    @Override
    @Transactional
    public TicketResponse createTicket(Long userId, TicketRequest ticketRequest) {
        log.info("Attempting to create ticket for user ID: {}", userId);


        String rateLimitKey = "rate_limit:ticket:create:" + userId;
        Integer requestCount = (Integer) redisTemplate.opsForValue().get(rateLimitKey);

        if (requestCount != null && requestCount >= 3) {

            log.warn("Rate limit exceeded for user ID: {}. Blocked from creating ticket.", userId);
            throw new BaseException(new ErrorMessage(MessageType.TOO_MANY_REQUESTS, userId.toString()));
        }


        if (requestCount == null) {
            redisTemplate.opsForValue().set(rateLimitKey, 1, 1, java.util.concurrent.TimeUnit.MINUTES);
        } else {
            redisTemplate.opsForValue().increment(rateLimitKey);
        }


        if (!userRepository.existsById(userId)) {
            throw new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, userId.toString()));
        }

        Ticket ticket = ticketMapper.toEntity(ticketRequest);
        Ticket savedTicket = ticketRepository.save(ticket);
        log.info("Saved ticket with ID: {} to database", savedTicket.getId());

        TicketCreatedEvent kafkaEvent = new TicketCreatedEvent(
                java.util.UUID.randomUUID().toString(),
                savedTicket.getId(),
                userId,
                savedTicket.getTitle(),
                savedTicket.getStatus().name(),
                java.time.LocalDateTime.now()
        );
        try {
            kafkaTemplate.send(KafkaProducerConfig.TICKET_CREATED_TOPIC, kafkaEvent.messageId(), kafkaEvent);
            log.info("Successfully published TicketCreatedEvent to Kafka with Message ID: {}", kafkaEvent.messageId());
        } catch (Exception e) {
            log.error("Failed to send message to Kafka for ticket ID: {}", savedTicket.getId(), e);

        }

        return ticketMapper.toDto(savedTicket);
    }

    @Override
    @Cacheable(value = "tickets", key = "#ticketId")
    public TicketResponse getTicketById(Long ticketId) {
        log.info("Fetching ticket from Database with ID: {}", ticketId);

        Ticket dbTicket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> {
                    log.error("Ticket was not found with ID: {}", ticketId);
                    return new BaseException(new ErrorMessage(MessageType.TICKET_NOT_FOUND, ticketId.toString()));
                });

        return ticketMapper.toDto(dbTicket);
    }

    @Override
    public List<TicketResponse> getAllTicketsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.error("User was not found with ID: {}", userId);
            throw new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, userId.toString()));
        }
        log.info("Fetching all tickets for user ID: {}", userId);
        List<Ticket> ticketList = ticketRepository.getAllTicketsByUserId(userId);
        return ticketMapper.toDto(ticketList);
    }

    @Override
    public List<TicketResponse> getAllOpenTickets() {
        return null;
    }

    @Override
    @Transactional
    @CachePut(value = "tickets", key = "#ticketId")
    public TicketResponse assignTicketToSupport(Long ticketId, Long supportId) {
        log.info("Attempting to assign ticket ID: {} to support ID: {}", ticketId, supportId);


        String lockKey = "lock:ticket:assign:" + ticketId;
        RLock lock = redissonClient.getLock(lockKey);

        boolean isLocked = false;
        try {

            isLocked = lock.tryLock(2, 10, java.util.concurrent.TimeUnit.SECONDS);

            if (!isLocked) {

                log.warn("Ticket ID: {} is currently being locked and processed by another thread.", ticketId);
                throw new BaseException(new ErrorMessage(MessageType.TICKET_ALREADY_ASSIGNED, ticketId.toString()));
            }


            Ticket supportTicket = ticketRepository.findById(ticketId)
                    .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.TICKET_NOT_FOUND, ticketId.toString())));


            if (supportTicket.getStatus() != Status.OPEN) {
                throw new BaseException(new ErrorMessage(MessageType.TICKET_ALREADY_ASSIGNED, ticketId.toString()));
            }

            supportTicket.setStatus(Status.IN_PROGRESS);


            ticketRepository.save(supportTicket);
            log.info("Successfully assigned ticket ID: {} to support ID: {}", ticketId, supportId);

            return ticketMapper.toDto(supportTicket);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BaseException(new ErrorMessage(MessageType.SYSTEM_ERROR, "Thread interrupted while acquiring lock"));
        } finally {

            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }


    }
    @Override
    @Transactional
    @CachePut(value = "tickets", key = "#ticketId")
    public TicketResponse updateTicketStatus (Long ticketId, Status status){
        log.info("Updating ticket ID: {} status to: {}", ticketId, status);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.TICKET_NOT_FOUND, ticketId.toString())));

        ticket.setStatus(status);
        ticketRepository.save(ticket);
        return ticketMapper.toDto(ticket);
    }

    @Override
    @Transactional
    @CacheEvict(value = "tickets", key = "#ticketId")
    public String deleteTicketById (Long userId, Long ticketId){
        log.info("User ID: {} is attempting to delete ticket ID: {}", userId, ticketId);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.TICKET_NOT_FOUND, ticketId.toString())));

        if (ticket.getUser().getId().equals(userId)) {
            ticketRepository.deleteById(ticketId);
            log.info("Ticket ID: {} successfully deleted.", ticketId);
            return "success";
        } else {
            log.warn("User ID: {} is not authorized to delete ticket ID: {}", userId, ticketId);
            throw new BaseException(new ErrorMessage(MessageType.TICKET_NOT_FOUND, ticketId.toString()));
        }
    }


}
