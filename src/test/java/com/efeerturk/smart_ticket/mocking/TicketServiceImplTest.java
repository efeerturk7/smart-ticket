package com.efeerturk.smart_ticket.mocking;

import com.efeerturk.smart_ticket.dto.request.TicketRequest;
import com.efeerturk.smart_ticket.dto.response.TicketResponse;
import com.efeerturk.smart_ticket.enums.MessageType;
import com.efeerturk.smart_ticket.enums.Priority;
import com.efeerturk.smart_ticket.enums.Status;
import com.efeerturk.smart_ticket.event.TicketCreatedEvent;
import com.efeerturk.smart_ticket.exception.BaseException;
import com.efeerturk.smart_ticket.mapper.TicketMapper;
import com.efeerturk.smart_ticket.model.Ticket;
import com.efeerturk.smart_ticket.repository.TicketRepository;
import com.efeerturk.smart_ticket.repository.UserRepository;
import com.efeerturk.smart_ticket.service.impl.TicketServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    // --- MOCK TANIMLAMALARI ---
    @Mock
    private TicketMapper ticketMapper;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RedissonClient redissonClient;
    @Mock
    private RedisTemplate redisTemplate;
    @Mock
    private ValueOperations valueOperations;
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Mock
    private RLock rLock;


    @InjectMocks
    private TicketServiceImpl ticketService;




    @Test
    @DisplayName("Kusursuz Akış: Bilet başarıyla oluşturulmalı ve Kafka'ya event fırlatılmalı")
    void shouldCreateTicketSuccessfully_WhenUserIsValidAndRateLimitNotExceeded() {

        Long userId = 1L;
        TicketRequest request = new TicketRequest("title","description");
        Ticket ticketEntity = new Ticket();
        ticketEntity.setStatus(Status.OPEN);

        Ticket savedTicket = new Ticket();
        savedTicket.setId(100L);
        savedTicket.setStatus(Status.OPEN);

        TicketResponse expectedResponse = new TicketResponse(1L,"title","description",Status.OPEN, Priority.MEDIUM, LocalDateTime.now());


        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get("rate_limit:ticket:create:" + userId)).willReturn(1);


        given(userRepository.existsById(userId)).willReturn(true);
        given(ticketMapper.toEntity(request)).willReturn(ticketEntity);
        given(ticketRepository.save(ticketEntity)).willReturn(savedTicket);
        given(ticketMapper.toDto(savedTicket)).willReturn(expectedResponse);


        TicketResponse actualResponse = ticketService.createTicket(userId, request);


        assertThat(actualResponse).isNotNull();

        verify(ticketRepository, times(1)).save(ticketEntity);
        verify(kafkaTemplate, times(1)).send(
                eq("ticket-events-topic"),
                anyString(),
                any(TicketCreatedEvent.class)
        );
    }

    @Test
    @DisplayName("Error Condition: A RateLimit exception should be thrown if the user submits more than 3 requests.")
    void shouldThrowException_WhenUserExceedsRateLimit() {

        Long userId = 1L;
        TicketRequest request = new TicketRequest("title", "description");



        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get("rate_limit:ticket:create:" + userId)).willReturn(3);


        Throwable thrown = catchThrowable(() -> ticketService.createTicket(userId, request));


        assertThat(thrown)
                .isInstanceOf(BaseException.class)
                .hasMessageContaining(MessageType.TOO_MANY_REQUESTS.name());


        verify(ticketRepository, times(0)).save(any());
    }
    @Test
    @DisplayName("Hata Durumu: Başka bir thread Redisson kilidini almışsa (Concurrency), exception fırlatılmalı")
    void shouldThrowException_WhenRedissonLockCannotBeAcquired() throws InterruptedException {

        Long ticketId = 100L;
        Long supportId = 2L;


        given(redissonClient.getLock("lock:ticket:assign:" + ticketId)).willReturn(rLock);
        given(rLock.tryLock(2, 10, TimeUnit.SECONDS)).willReturn(false); // Kilit BAŞKASINDA!


        Throwable thrown = catchThrowable(() -> ticketService.assignTicketToSupport(ticketId, supportId));


        assertThat(thrown)
                .isInstanceOf(BaseException.class)
                .hasMessageContaining(MessageType.TICKET_ALREADY_ASSIGNED.name());


        verify(ticketRepository, times(0)).findById(any());
    }
}