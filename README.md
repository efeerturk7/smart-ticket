# 🎟️ SmartTicket - Cloud-Native Ticketing API

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Relational_DB-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Apache Kafka](https://img.shields.io/badge/Apache_Kafka-Event_Streaming-231F20?style=for-the-badge&logo=apachekafka&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-Rate_Limit-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Enterprise_Containerized-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Security](https://img.shields.io/badge/Spring_Security-JWT-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)

**SmartTicket** is a production-ready, highly scalable REST API designed for managing high-concurrency ticket creation and processing operations.

This project demonstrates a modern **DevOps-oriented Modular Monolith architecture**. It is strictly containerized with **Docker** (utilizing custom networks and resource limits), uses **Redis** for distributed Rate Limiting, stores critical data in **PostgreSQL**, and relies on **Apache Kafka** for advanced, fail-safe asynchronous background processing.

---

## 🏗 System Architecture & Workflow

### 🔄 CI/CD Pipeline (Deployment Flow)
Everything is streamlined and automated.

> **💻 Developer Push** 👉  **⚙️ GitHub Actions (Build & Test)** 👉  **🐳 Docker Hub (Image Registry)** ### ⚙️ Runtime Architecture
How the application works in a highly concurrent production environment:

1.  **🌍 Client Request:** User sends a ticket creation request via Swagger or Postman.
2.  **🛡️ Security Layer:** Spring Security with JWT Filter validates the user's token.
3.  **🚦 Rate Limiter (Redis):** Before hitting the database, Redis checks if the user has exceeded their request quota (preventing DDoS and abuse).
4.  **☕ Spring Boot Core:** Business logic processes the request.
5.  **🐘 PostgreSQL:** If valid, the ticket is persisted securely in the database.
6.  **📨 Kafka Event Streaming (Async):** On successful database write, a background `TicketCreatedEvent` is instantly fired to Kafka to handle heavy downstream tasks (e.g., email notifications) without blocking the user's HTTP response.

---

## 🚀 Key Technical Features

This project utilizes a **Modular Monolith** approach, keeping deployment simple while strictly separating domain boundaries and utilizing asynchronous communication.

### 1. 📨 Advanced Event-Driven Architecture (Apache Kafka)
Integrated Apache Kafka to handle background tasks asynchronously. It implements real-world enterprise resilience and fault-tolerance patterns:
* **Idempotent Consumer (Duplicate Prevention):** Prevents processing duplicate messages caused by network partitions or consumer retries. The system attaches a unique `messageId` (UUID) to events and utilizes a Check-and-Set pattern via a dedicated PostgreSQL table (`processed_event`) to enforce exact-once processing semantics.
* **Poison Pill Prevention:** Utilizes `ErrorHandlingDeserializer` to catch and safely discard malformed JSON payloads mid-air, preventing infinite crash-loops in the notification consumer.
* **Smart Retry & Dead Letter Topic (DLT):** Built with a robust Exception Translation mechanism. Network/transient failures trigger a `FixedBackOff` strategy (retries 3 times with a 2-second interval). If all attempts fail, or if a fatal non-retryable exception occurs, the message is automatically routed to a `.DLT` graveyard topic for manual inspection, ensuring the main topic partition is never blocked.
* **Reliable Producer:** Configured with `acks=all` and `enable.idempotence=true` to guarantee zero message loss and strict ordering during transmission.

### 2. 🐳 Enterprise-Grade Dockerization (DevOps Security)
* **Custom Bridge Networking:** The entire stack (DB, Redis, Kafka, Zookeeper) is isolated within a custom Docker network (`smart-ticket-net`). External ports for databases and brokers are intentionally closed, preventing direct internet access and ensuring they can only be reached internally by the Spring Boot application.
* **Resource Limits & Reservations:** Strict CPU and Memory limits are defined for each container in the `docker-compose.yml` to prevent RAM-heavy services (like Kafka and Spring Boot) from causing OS-level Out-Of-Memory (OOM) kernel kills.

### 3. ⚡ Distributed Rate Limiting (Redis)
* Implemented a distributed token-bucket/sliding-window Rate Limiter using Redis.
* Ensures fair usage policies per user/IP, demonstrating capabilities in handling high-concurrency scenarios securely before traffic hits the relational database.

---

## 🛠️ Tech Stack

| Category | Technology |
| :--- | :--- |
| **Language** | Java 21 (LTS) |
| **Framework** | Spring Boot 3.2.x |
| **Message Broker** | Apache Kafka, Spring Kafka |
| **Database** | PostgreSQL & Spring Data JPA |
| **Caching & Ops** | Redis (Rate Limiting) |
| **Serialization** | Jackson (JSON for Redis & Kafka), MapStruct |
| **DevOps** | Docker, Docker Compose (Resource Limits & Isolated Networks) |
| **Security** | Spring Security 6, JWT |
| **Docs** | OpenAPI / Swagger UI |

---

## ⚙️ How to Run Locally

Since the project is strictly Dockerized with custom networks, you can run the entire secure stack (App + DB + Redis + Kafka + Zookeeper) with a single command.

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/efeerturk7/smart-ticket-backend.git](https://github.com/efeerturk7/smart-ticket-backend.git)
    cd smart-ticket-backend
    ```

2.  **Start with Docker Compose (Builds the App & Infrastructure):**
    ```bash
    docker-compose up -d --build
    ```

3.  **Access the App:**
    * Swagger UI: `http://localhost:8080/swagger-ui/index.html`
    * *Note: Direct external access to PostgreSQL (5432), Redis (6379), and Kafka (9092) is disabled by design for security. All traffic must route through the Spring Boot API (8080).*

---

### 👨‍💻 Author
**Bahadır Efe ERTÜRK** - Backend Developer

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/efeerturk7/)
[![GitHub](https://img.shields.io/badge/GitHub-Follow-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/efeerturk7)