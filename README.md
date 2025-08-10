# Ticketing API

A lightweight microservice for handling support tickets, designed with Spring Boot. The API supports ticket creation, status management, filtering, and comment visibility based on user roles (agent/user), secured via API keys.

---

## Features

- **Create and list tickets**
- **Filter by status, userId and assigneeId**
- **Add comments (public/internal)**
- **Role-based access (USER / AGENT)**
- **Status transitions with validation**
- **Swagger UI for API documentation**
- **Stateless API key authentication**

---

## Technologies

- Java 21
- Spring Boot
- Spring Security (API Key based)
- MapStruct
- Log4j2
- Swagger / OpenAPI
- Docker & Docker Compose
- JUnit / MockMvc for tests

---

## Authentication

Authentication is handled via a static API key system. Requests must include a header:

```http
X-API-KEY: <your-api-key>
```

Available roles and keys (from environment variables or .env):
- `API_KEY_USER` → role: `ROLE_USER`
- `API_KEY_AGENT` → role: `ROLE_AGENT`

---

## API Overview

### Create a Ticket
**POST** `/api/tickets`

```json
{
    "userId": "user-001",
    "subject": "Payment issue",
    "description": "I was charged twice for the same order."
}
```

### Get Tickets (with filters)
**GET** `/api/tickets?status=open&userId=user-001`

- Optional filters: `status`, `userId`, `assigneeId`
- Agents see all comments
- Users only see public comments

### Update Ticket Status
**PATCH** `/api/tickets/{id}/status`

*Accessible by:* `AGENT`

```json
{
  "status": "in_progress"
}
```

### Add Comment
**POST** `/api/tickets/{id}/comments`

- **USER**: can only post **public** comments
- **AGENT**: can post **internal** or **public** comments

```json
{
    "authorId": "agent-123",
    "content": "We're currently investigating your issue.",
    "visibility": "public"
}
```

---

## Tests

Covered in unit/integration tests:
- Ticket creation
- Comment visibility logic
- Valid/invalid status transitions
- Filtered ticket listing

Run tests:
```bash
mvn clean test
```

---

## 🐳 Docker

### Requirements
- Docker
- Docker Compose

### .env File
Create a `.env` file at the root:

```env
API_KEY_USER=user-secret-key
API_KEY_AGENT=agent-secret-key
```

### Start the service
```bash
docker compose up --build
```

The API will be available at: `http://localhost:8080`

---

## Logging and Trace ID Usage

The application uses Log4j2 integrated with SLF4J for structured and consistent logging across all layers of the microservice. A Trace ID is automatically generated and injected into the logs for each incoming HTTP request, allowing developers and operators to correlate log entries across different components and services, even when multiple requests are processed concurrently.

This approach improves observability and troubleshooting by:

	•	Making it easier to trace the flow of a single request through the system.
	•	Simplifying debugging in high-concurrency environments where log messages may otherwise be interleaved.
	•	Supporting future integration with distributed tracing tools (e.g., OpenTelemetry, Spring Sleuth).

Error logging is centralized in a GlobalExceptionHandler, ensuring that all unexpected conditions are recorded with the associated Trace ID for faster root-cause analysis.

---

## Swagger / OpenAPI

Swagger UI is available at:
```
http://localhost:8080/swagger-ui.html
```

**Note**: Swagger security is disabled for development purposes, allowing unauthenticated access to:
- `/swagger-ui/**`
- `/v3/api-docs/**`
- `/swagger-ui.html`

---

## Security Configuration

- **Stateless** authentication
- Swagger docs are **public**
- `/api/tickets/*/status`: only accessible by **agents**
- All other endpoints require authentication (**user** or **agent**)

### Status Transitions
Valid status transitions are enforced:
- `OPEN` → `IN_PROGRESS`
- `IN_PROGRESS` → `RESOLVED`
- `RESOLVED` → `CLOSED`

---

## Quick Start

### 1. Clone the repository
```bash
git clone https://github.com/Gatineau/ticketing-api.git
cd ticketing-api
```

### 2. Set up environment
```bash
touch .env
# Edit .env with your API keys
```

### 3. Package with maven
```bash
mvn clean install
```

### 4. Run with Docker
```bash
docker compose up --build
```

### 5. Test the API
```bash
# Create a ticket (as USER)
curl -X POST http://localhost:8080/api/tickets \
  -H "X-API-KEY: user-secret-key" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user-001",
    "subject": "Payment issue",
    "description": "I was charged twice for the same order."
  }'

# Get tickets (as AGENT)
curl -X GET http://localhost:8080/api/tickets \
  -H "X-API-KEY: agent-secret-key"
```

---

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/assignment/ticketing/
│   │       ├── config/              # Security & Swagger config
│   │       ├── controller/          # REST endpoints
│   │       ├── exception/           # Exceptions handler
|   │       ├── logging/             # Logging configuration
│   │       ├── model/               # Application data model
│   │       │  ├── domain/           # Business objects
│   │       │  ├── dto/              # DTO for API requests/responses
│   │       │  ├── enums/            # Enumerations
│   │       │  └── mapper/           # MapStruct mappers
│   │       ├── repository/          # Data access layer
│   │       ├── security/            # API key authentication
│   │       ├── service/             # Business logic and application services
│   │       └── storage/             # In-memory storage implementation 
│   └── resources/
│       ├── application.properties   # Configuration
│       ├── log4j2.xml               # Logs
└── test/                            # Unit & integration tests
```

---

## Development

### Running locally (without Docker)
```bash
# Start the application
mvn spring-boot:run

# Or run the JAR
mvn clean package
java -jar target/ticketing-api-1.0.0.jar
```

### Environment Variables
```env
API_KEY_USER=your-user-key
API_KEY_AGENT=your-agent-key
```

---

## 📝 API Examples

### Create Ticket
```bash
curl -X POST http://localhost:8080/api/tickets \
  -H "X-API-KEY: user-secret-key" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user-001",
    "subject": "Payment issue",
    "description": "I was charged twice for the same order."
  }'
```

### Add Comment
```bash
curl -X POST http://localhost:8080/api/tickets/1/comments \
  -H "X-API-KEY: agent-secret-key" \
  -H "Content-Type: application/json" \
  -d '{
    "authorId": "agent-123",
    "content": "We are currently investigating your issue.",
    "visibility": "public"
  }'
```

### Update Status
```bash
curl -X PATCH http://localhost:8080/api/tickets/1/status \
  -H "X-API-KEY: agent-secret-key" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "IN_PROGRESS"
  }'
```

---

## Design decisions

Several design choices were made to keep the system simple, secure, and maintainable:
### 1.	Role-based access via API Keys
	•	Instead of a full OAuth2/JWT authentication setup, the service uses API keys mapped to specific roles (ROLE_USER and ROLE_AGENT).
	•	This allows for lightweight authentication suitable for internal services or controlled environments.
	•	API keys are externalized via environment variables for security, ensuring they are not hardcoded in the codebase.
	• The authentication mechanism can be easily replaced with JWT or OAuth2 if future requirements demand stronger security or token-based authorization.

### 2.	Separation of User and Agent Capabilities
	•	Users can create tickets and add only public comments.
	•	Agents can update ticket statuses and add both public and internal comments.
	•	This is enforced at both the HTTP security configuration level (URL-based access control) and in business logic (service layer checks).

### 3.	Comment Visibility Filtering
	•	Public comments are visible to everyone, but internal comments are only visible to agents.
	•	This filtering happens in the service layer, ensuring that repository results are always processed according to the caller’s role.

### 4.	Status Transition Rules
	•	Status changes follow a strict linear progression: OPEN → IN_PROGRESS → RESOLVED → CLOSED
	• Invalid transitions are rejected to maintain workflow integrity.
	• Transitions are enforced using a Java 17 switch statement, ensuring clear and maintainable validation logic.

### 5.	Immutable DTOs via MapStruct and records
	•		TicketResponse and related DTOs are generated using MapStruct, providing clean, type-safe mapping and preventing direct exposure of internal entity structures to API clients.
	•	DTOs are implemented as Java Records, ensuring immutability and making them ideal for data transfer between layers.
	•	The createdAt and updatedAt fields are initialized and updated at the storage layer, guaranteeing consistent behavior regardless of the underlying persistence mechanism. In a database-backed implementation, this could be enforced via triggers.

### 6.	Swagger / OpenAPI for Documentation
	•	API documentation is exposed via Swagger UI (/swagger-ui.html), with security configuration allowing public access to docs while keeping business endpoints protected.

## AI Tool Usage and Validation Steps

During the development of this project, AI assistance was leveraged in several ways:

	•	Generating unit and integration test cases.
	•	Analyzing and troubleshooting runtime errors.
	•	Producing portions of the README file and in-code comments, and improving documentation clarity.
	•	Accelerating repetitive coding tasks by suggesting syntax and method signatures.

Additionally, GitHub Copilot was used to enhance productivity through in-editor code suggestions and autocompletion.

### Validation Steps

	1.	All AI-generated code was manually reviewed to ensure compliance with project requirements and coding standards.
	2.	Generated tests were executed to verify correct behavior.
	3.	Error-handling strategies were validated against realistic application scenarios.
	4.	Documentation content was adapted and validated for accuracy, clarity, and project relevance.

## License

This project is licensed under the MIT License.
