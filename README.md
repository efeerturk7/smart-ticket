# 🎟️ SmartTicket - Cloud-Native Event-Driven Ticketing API

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.x-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Relational_DB-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Apache Kafka](https://img.shields.io/badge/Apache_Kafka-Event_Streaming-231F20?style=for-the-badge&logo=apachekafka&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-Cache_%26_Locks-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Enterprise_Containerized-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Security](https://img.shields.io/badge/Spring_Security-JWT-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)

**SmartTicket** is a production-ready, highly scalable REST API designed for managing high-concurrency ticket creation and processing operations.

This project demonstrates a modern **DevOps-oriented Modular Monolith architecture**. It integrates **Clean Architecture** principles in Spring Boot, utilizes **Redis** for distributed locking and rate limiting, stores critical data in **PostgreSQL**, relies on **Apache Kafka** for fail-safe asynchronous processing, and is strictly containerized with **Docker**.

---

## 🏗 System Architecture & Workflow

### ⚙️ Runtime Architecture
How the application handles high-concurrency traffic in production:

1.  **🌍 Client Request:** User sends a request via Swagger or Postman.
2.  **🛡️ Security Layer:** Spring Security intercepts the request; JWT Filter validates the bearer token.
3.  **🚦 Rate Limiter (Redis):** Checks if the user/IP has exceeded their request quota (preventing DDoS).
4.  **🔒 Concurrency Control:** Redis Distributed Lock (Redisson) ensures no double-booking for the same ticket.
5.  **☕ Spring Boot Core:** Business logic processes the request, mapping immutable DTOs to Entities via MapStruct.
6.  **🐘 PostgreSQL:** Persists the ticket securely into the database.
7.  **📨 Kafka Event Streaming:** A `TicketCreatedEvent` is fired asynchronously to handle downstream tasks (notifications) without blocking the HTTP response.
8.  **📦 Standardized Response:** A Generic API Response wrapper returns a consistent payload to the client.

---

## 🚀 Key Technical Features

This project was built strictly following Enterprise Software Engineering standards, focusing on maintainability, performance, and fault tolerance.

### 1. 🏗️ Clean Architecture & Spring Boot Best Practices
* **Immutable DTOs (Java Records):** Replaced traditional boilerplate classes with Java 21 `record`s for Data Transfer Objects. This guarantees thread-safe immutability natively and keeps the codebase clean.
* **Generic API Response Wrapper:** Designed a universal `BaseResponse<T>` root entity. Every single endpoint returns this standardized envelope, ensuring a consistent contract (status codes, payload, timestamp) for frontend consumers.
* **Auditable Base Entity:** Implemented a `@MappedSuperclass` `BaseEntity`. All JPA models extend this base class to inherit common properties automatically (`id`, `createdAt`, `updatedAt`), enforcing DRY (Don't Repeat Yourself) principles.
* **Global Exception Handling:** Centralized error management using `@RestControllerAdvice`. Domain-specific exceptions are intercepted and translated into standardized, readable JSON error payloads, preventing stack traces from leaking to the client.
* **High-Performance Mapping:** Utilized **MapStruct** for type-safe, compile-time object mapping between JPA Entities and DTOs, completely isolating the database layer from the presentation layer.

### 2. 🔐 Robust Security Layer
* **Spring Security 6 & JWT:** Stateless authentication architecture. Implemented custom JWT filters to parse, validate, and authorize requests efficiently.
* **Role-Based Access Control:** Endpoints are strictly protected based on user roles and authorities.

### 3. ⚡ Advanced Redis Integration
The Redis implementation goes far beyond basic caching:
* **Distributed Locking (Redisson):** Solved the "Race Condition" problem during high-traffic ticket purchases. Redisson locks ensure that if 1000 users try to buy the last ticket simultaneously, only one succeeds.
* **Distributed Rate Limiting:** Implemented a token-bucket algorithm to throttle abusive requests at the gateway level.
* **Smart Caching:** Cache-Aside pattern implementation to significantly reduce database hits for read-heavy operations.

### 4. 📨 Enterprise Event-Driven Architecture (Apache Kafka)
Decoupled heavy downstream tasks using a robust Kafka implementation:
* **Idempotent Consumer:** Uses a unique `messageId` and a check-and-set pattern with a PostgreSQL `processed_event` table to absolutely prevent duplicate message processing.
* **Smart Retry & Dead Letter Topic (DLT):** Configured Spring Retry (`FixedBackOff`). Transient failures are retried 3 times. Fatal errors or exhausted retries are gracefully routed to a `.DLT` graveyard topic for manual inspection.
* **Poison Pill Prevention:** Uses `ErrorHandlingDeserializer` to safely catch malformed payloads mid-air, preventing infinite consumer crash-loops.
* **Reliable Producer:** Configured with `acks=all` and `enable.idempotence=true` for zero message loss.

### 5. 🐳 Enterprise-Grade Dockerization (DevOps)
* **Custom Bridge Networking:** The entire infrastructure (DB, Redis, Kafka) is isolated within a custom Docker network (`smart-ticket-net`). External ports are intentionally closed; they can only be reached internally by the Spring Boot application (Port 8080).
* **Resource Limits:** Strict CPU and Memory `limits` and `reservations` are defined in `docker-compose.yml` to prevent RAM-heavy containers from causing OS-level Out-Of-Memory (OOM) kills.

---

## 🛠️ Tech Stack

| Category | Technology |
| :--- | :--- |
| **Language** | Java 21 (LTS) |
| **Framework** | Spring Boot 3.2.x |
| **Message Broker** | Apache Kafka, Spring Kafka |
| **Database** | PostgreSQL & Spring Data JPA |
| **Caching & In-Memory** | Redis, Redisson (Distributed Locks & Rate Limiting) |
| **Architecture** | MapStruct, Lombok, Global Exception Handler |
| **DevOps** | Docker, Docker Compose (Resource Limits & Isolated Networks) |
| **Security** | Spring Security 6, JSON Web Tokens (JWT) |
| **Docs** | OpenAPI / Swagger UI |

---

## ⚙️ How to Run Locally

Since the project is strictly Dockerized with custom networks, you can run the entire secure stack with a single command.

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/efeerturk7/smart-ticket.git](https://github.com/efeerturk7/smart-ticket.git)
    cd smart-ticket-backend
    ```

2.  **Start with Docker Compose (Builds the App & Infrastructure):**
    ```bash
    docker-compose up -d --build
    ```

3.  **Access the App:**
    * **Swagger UI Documentation:** `http://localhost:8080/swagger-ui/index.html`
    * *Note: Direct external access to PostgreSQL (5432), Redis (6379), and Kafka (9092) is disabled by design for security. All traffic must route through the Spring Boot API (8080).*

---

### 👨‍💻 Author
**Bahadır Efe ERTÜRK** - Backend Developer

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/efeerturk7/)
[![GitHub](https://img.shields.io/badge/GitHub-Follow-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/efeerturk7)