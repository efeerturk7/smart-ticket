# 🎟️ SmartTicket - Cloud-Native Event-Driven & AI-Powered Ticketing API

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.x-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring AI](https://img.shields.io/badge/Spring_AI-Cognitive_Agent-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Ollama](https://img.shields.io/badge/Ollama-Local_LLM-FFFFFF?style=for-the-badge&logo=ollama&logoColor=black)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-pgvector-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Apache Kafka](https://img.shields.io/badge/Apache_Kafka-Event_Streaming-231F20?style=for-the-badge&logo=apachekafka&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-Cache_%26_Locks-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Enterprise_Containerized-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![CI/CD](https://img.shields.io/badge/CI%2FCD-GitHub_Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white)
![Security](https://img.shields.io/badge/Spring_Security-JWT-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)

**SmartTicket** is a production-ready, highly scalable REST API designed for managing high-concurrency ticket creation, processing operations, and automated AI-driven customer support.

This project demonstrates a modern **DevSecOps-oriented Modular Monolith architecture**. It integrates **Clean Architecture** principles in Spring Boot, utilizes **Redis** for distributed locking, relies on **Apache Kafka** for fail-safe asynchronous processing, features an **Enterprise Cognitive AI Agent** for L1 support, and is strictly containerized with **Docker** and automated via **CI/CD pipelines**.

---

## 🏗 System Architecture & Workflow

### ⚙️ Runtime Architecture
How the application handles high-concurrency traffic and AI operations in production:

1. **🌍 Client Request:** User sends a request via Swagger or Postman.
2. **🛡️ Security Layer:** Spring Security intercepts the request; JWT Filter validates the bearer token.
3. **🚦 Rate Limiter (Redis):** Checks if the user/IP has exceeded their request quota (preventing DDoS).
4. **🔒 Concurrency Control:** Redis Distributed Lock (Redisson) ensures no double-booking for the same ticket.
5. **🐘 PostgreSQL:** Persists the ticket securely into the database.
6. **📨 Kafka Event Streaming:** A `TicketCreatedEvent` is fired asynchronously to handle downstream tasks.
7. **🤖 AI Orchestrator (L1 Agent):** New tickets are intercepted by the **Spring AI Orchestrator**.
    * **Advanced RAG:** Searches `pgvector` for past solutions.
    * **Structured Output:** Categorizes urgency and sentiment (e.g., ANGRY).
    * **HITL (Human-in-the-Loop):** Escalates severe issues directly to human agents.
8. **📦 Standardized Response:** A Generic API Response wrapper returns a consistent payload to the client.

---

## 🚀 Key Technical Features

This project was built strictly following Enterprise Software Engineering standards, focusing on maintainability, AI integration, and fault tolerance.

### 1. 🧠 Enterprise Cognitive AI Agent (Spring AI)
* **Local LLM Integration:** Powered by **Ollama (Llama 3)**, ensuring 100% data privacy without external API limits.
* **Advanced RAG Pipeline:** Utilizes PostgreSQL with the `pgvector` extension. Implements Semantic Search (Cosine Distance) to provide the LLM with accurate, hallucination-free context from past resolved tickets.
* **Agentic Workflow & Tool Calling:** The AI functions as an Autonomous Agent, securely invoking internal Java methods (like checking ticket status in the DB) to resolve user queries dynamically.
* **Structured Output & Sentiment Analysis:** Evaluates unstructured user complaints, extracting sentiment and urgency into immutable Java Records.

### 2. 🏗️ Clean Architecture & Spring Boot Best Practices
* **Immutable DTOs (Java Records):** Replaced traditional boilerplate classes with Java 21 `record`s for Data Transfer Objects.
* **Generic API Response Wrapper:** Designed a universal `BaseResponse<T>` root entity for consistent frontend contracts.
* **Global Exception Handling:** Centralized error management using `@RestControllerAdvice`.

### 3. 🧪 Enterprise-Grade Testing & CI/CD
* **Continuous Integration (GitHub Actions):** Fully automated CI/CD pipeline. Every push triggers the workflow to compile code, run tests, and verify builds.
* **Zero-Mock Integration Testing (Testcontainers):** Integration tests spin up ephemeral, real Docker containers for **PostgreSQL, Redis, and Kafka** inside the CI pipeline, guaranteeing production-parity.
* **Behavior-Driven Unit Testing (Mockito & AssertJ):** Strict, isolated service layer testing using chained mocking techniques and AssertJ fluent assertions.

### 4. ⚡ Advanced Redis Integration
* **Distributed Locking (Redisson):** Solved the "Race Condition" problem during high-traffic ticket assignments.
* **Distributed Rate Limiting:** Implemented a token-bucket algorithm to throttle abusive requests at the gateway level.

### 5. 📨 Enterprise Event-Driven Architecture (Apache Kafka)
* **Idempotent Consumer:** Uses a unique `messageId` and a check-and-set pattern to absolutely prevent duplicate message processing.
* **Smart Retry & Dead Letter Topic (DLT):** Configured Spring Retry (`FixedBackOff`). Exhausted retries are gracefully routed to a `.DLT` graveyard topic.

### 6. 🐳 Enterprise-Grade Dockerization (DevSecOps)
* **Zero-Hardcoding Security:** Passwords and JWT secrets are injected dynamically via `.env` environment variables.
* **Custom Bridge Networking:** The entire infrastructure (DB, Redis, Kafka) is isolated within a custom Docker network (`smart-ticket-net`).

---

## 🛠️ Tech Stack

| Category | Technology |
| :--- | :--- |
| **Language** | Java 21 (LTS) |
| **Framework** | Spring Boot 3.2.x |
| **Artificial Intelligence** | Spring AI, Ollama, Advanced RAG, Tool Calling |
| **Testing & CI/CD** | JUnit 5, Testcontainers, Mockito, GitHub Actions |
| **Message Broker** | Apache Kafka, Spring Kafka |
| **Database & Vector Store** | PostgreSQL, `pgvector` & Spring Data JPA |
| **Caching & In-Memory** | Redis, Redisson (Distributed Locks & Rate Limiting) |
| **DevSecOps** | Docker, Docker Compose, `.env` Secret Management |
| **Security** | Spring Security 6, JSON Web Tokens (JWT) |

---

## ⚙️ How to Run Locally

Since the project is strictly Dockerized and secured with environment variables, follow these steps to spin up the production-parity environment:

### Prerequisites
* Docker & Docker Compose installed.
* Ollama installed locally.

### 1. Start Local LLM Models (Ollama)
Open your terminal and pull the required models for the AI Agent:

```bash
ollama run llama3
ollama run nomic-embed-text
```

### 2. Setup Environment Variables
Create a .env file in the root directory and configure your secrets:
```bash
DB_USER=postgres
DB_PASSWORD=your_secure_db_password
DB_NAME=smartticket
REDIS_PASSWORD=your_secure_redis_password
JWT_SECRET=your_base64_encoded_jwt_secret_key
```

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/efeerturk7/smart-ticket.git](https://github.com/efeerturk7/smart-ticket.git)
    cd smart-ticket
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


