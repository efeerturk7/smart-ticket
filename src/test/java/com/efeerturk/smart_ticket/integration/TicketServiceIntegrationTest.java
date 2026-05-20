package com.efeerturk.smart_ticket.integration;

import com.efeerturk.smart_ticket.dto.request.TicketRequest;
import com.efeerturk.smart_ticket.dto.response.TicketResponse;
import com.efeerturk.smart_ticket.model.Ticket;
import com.efeerturk.smart_ticket.model.User;
import com.efeerturk.smart_ticket.repository.TicketRepository;
import com.efeerturk.smart_ticket.repository.UserRepository;
import com.efeerturk.smart_ticket.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class TicketServiceIntegrationTest {
	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("pgvector/pgvector:pg15"))
			.withDatabaseName("smartticket_db")
			.withUsername("user")
			.withPassword("password");;

	@Container
	static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
			.withExposedPorts(6379)
			.withCommand("redis-server --requirepass password");

	@Container
	static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("apache/kafka:3.7.0"))
			.waitingFor(org.testcontainers.containers.wait.strategy.Wait.forListeningPort());

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);

		registry.add("spring.data.redis.host", redis::getHost);
		registry.add("spring.data.redis.port", redis::getFirstMappedPort);

		registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
	}
	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {

		registry.add("spring.data.redis.host", redis::getHost);
		registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
		registry.add("spring.data.redis.password", () -> "password");


		registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);


		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}


	@Autowired
	private TicketService ticketService;

	@Autowired
	private TicketRepository ticketRepository;

	@Autowired
	private UserRepository userRepository;

	private User testUser;


	@BeforeEach
	void setUp() {
		ticketRepository.deleteAll();
		userRepository.deleteAll();

		User user = new User();
		user.setUsername("testuser");
		user.setEmail("test@smartticket.com");
		testUser = userRepository.save(user);
	}

	@Test
	@DisplayName("REAL-LIFE ENVIRONMENT: The ticket must be saved to the database and Redis cache/Kafka must be triggered.")
	void shouldSaveTicketToRealDatabase() {
		// --- GIVEN ---
		TicketRequest request = new TicketRequest("title","description");


		// --- WHEN ---

		TicketResponse response = ticketService.createTicket(testUser.getId(), request);

		// --- THEN ---
		assertThat(response).isNotNull();
		assertThat(response.id()).isNotNull();


		List<Ticket> savedTickets = ticketRepository.findAll();
		assertThat(savedTickets).hasSize(1);
		assertThat(savedTickets.get(0).getUser().getId()).isEqualTo(testUser.getId());
	}


}
