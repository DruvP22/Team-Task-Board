# Team Task Board
 
A full-stack task management API with role-based workflows, JWT authentication,
and Slack integration — built with Spring Boot and PostgreSQL.
 
The Next.js frontend calls the Spring Boot REST API over HTTP, which persists
data to PostgreSQL and pushes real-time notifications to Slack on task creation
and status changes.
 
## Tech Stack
- **Backend:** Java 21, Spring Boot 4.1.1, Spring Data JPA, Spring Security
- **Database:** PostgreSQL 18 (via Docker)
- **Auth:** JWT (jjwt), BCrypt password hashing, role-based access control
- **Testing:** JUnit 5, Mockito, Testcontainers
- **Docs:** springdoc-openapi (Swagger UI)
- **Integration:** Slack Incoming Webhooks
## Features
- Full CRUD for Projects and Tasks, with pagination and filtering
- Role-based authorization (ADMIN / MEMBER) — e.g., only ADMINs can delete tasks
- Enforced task workflow: `BACKLOG → IN_PROGRESS → REVIEW → DONE`, illegal
  transitions rejected with a clear error message
- Fixed an N+1 query problem on the task list endpoint using `@EntityGraph`,
  reducing query count from ~100+ to 1 per page for a 50-item page
- Slack notifications on task creation and status changes
- Global exception handling with clean JSON error responses
## Getting Started
 
### Prerequisites
- Java 21 ([Adoptium](https://adoptium.net/))
- Docker Desktop
- Maven (or use the included `mvnw` wrapper — no separate install needed)
### Setup
1. Clone the repo and navigate into it.
2. Copy the example config and fill in your own values:
```
   cp src/main/resources/application.properties.example 
   src/main/resources/application.properties
```
3. Set the required environment variables (`JWT_SECRET` and, optionally,
   `SLACK_WEBHOOK_URL` for the Slack integration feature):
```
   $env:JWT_SECRET="your-long-random-secret-string"
   $env:SLACK_WEBHOOK_URL="https://hooks.slack.com/services/..."
```
4. Start Postgres:
```
   docker run --name taskboard-db -e POSTGRES_PASSWORD=yourpassword -e POSTGRES_DB=taskboard -p 5432:5432 -d postgres
```
5. Run the app:
```
   ./mvnw spring-boot:run
```
6. Open Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
### Running Tests
```
./mvnw test
```
Note: the repository integration test (`Task_RepositoryIT`) uses Testcontainers
and requires Docker Desktop to be running.
 
## API Overview
| Endpoint | Description |
|---|---|
| `POST /api/auth/register` | Register a new user, returns a JWT |
| `POST /api/auth/login` | Log in, returns a JWT |
| `GET/POST /api/projects` | List / create projects |
| `GET/POST/PUT/DELETE /api/tasks` | Full task CRUD (DELETE requires ADMIN) |
| `PATCH /api/tasks/{id}/status` | Move a task through its workflow |
 
Full interactive documentation is available via Swagger UI once the app is running.
 
## Architecture Decisions
See [docs/adr-001-dto-projections.md](docs/adr-001-dto-projections.md) for the reasoning behind the N+1 fix approach.
 
