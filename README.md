# Tickets System API

A RESTful API for event management and ticket sales. Organizers create events at venues, define ticket types and pricing variants, and users purchase tickets through orders.

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5 |
| Persistence | Spring Data JPA + Hibernate 6 |
| Database | MySQL 8 |
| Auth | Spring Security + JWT (JJWT 0.12) |
| Validation | Hibernate Validator (Bean Validation 3) |
| API Docs | springdoc-openapi 2 (OpenAPI 3 + Swagger UI) |
| Metrics | Micrometer + Prometheus + Grafana |
| Messaging | Apache Kafka (KRaft) |
| Build | Maven 3.9 |
| Container | Docker + Docker Compose |

---

## API Documentation

Swagger UI is available when the app is running:

| Resource | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI spec (JSON) | http://localhost:8080/v3/api-docs |

### Authenticating in Swagger UI

1. Call `POST /auth/login` and copy the `token` from the response
2. Click **Authorize** (top right)
3. Paste the token and click **Authorize**

All subsequent requests will include `Authorization: Bearer <token>` automatically.

---

## API Endpoints

### Auth
| Method | Path | Auth | Description |
|---|---|---|---|
| `POST` | `/auth/register` | — | Register a new user, returns JWT |
| `POST` | `/auth/login` | — | Authenticate, returns JWT |

### Events
| Method | Path | Auth | Description |
|---|---|---|---|
| `GET` | `/api/events` | ✓ | Paginated event listing with optional filters |
| `POST` | `/api/events` | ✓ | Create a new event |

**Query params for `GET /api/events`:**
- `name` — partial name filter (case-insensitive)
- `status` — exact status match
- `page` — page number (default: `0`)
- `size` — page size (default: `10`)
- `sort` — sort field and direction (default: `name,desc`)

### Tickets
| Method | Path | Auth | Description |
|---|---|---|---|
| `GET` | `/api/tickets?eventId={id}` | ✓ | List ticket types with variants for an event |
| `GET` | `/api/tickets/{ticketId}/variants` | ✓ | List variants for a ticket |
| `POST` | `/api/tickets/{ticketId}/variants` | ✓ | Create a variant for a ticket |

### Venues
| Method | Path | Auth | Description |
|---|---|---|---|
| `GET` | `/api/venues` | ✓ | List all venues |
| `GET` | `/api/venues/{id}` | ✓ | Get venue by ID |

### Observability
| Method | Path | Description |
|---|---|---|
| `GET` | `/actuator/health` | Application health check |
| `GET` | `/actuator/prometheus` | Prometheus metrics scrape endpoint |

---

## Local Development

### Prerequisites

- Java 21+
- Docker and Docker Compose
- `make` (optional but recommended)

### 1. Clone the repository

```bash
git clone https://github.com/gtomanini/tickets-system-api.git
cd tickets-system-api
```

### 2. Configure environment

```bash
cp .env.example .env
# .env already has working defaults for local Docker — no changes needed
```

### 3. Start the database and run the API

```bash
make dev
```

This starts all Docker services and boots the app with the `dev` profile.

| Service | URL | Credentials |
|---|---|---|
| API | http://localhost:8080 | JWT |
| Swagger UI | http://localhost:8080/swagger-ui.html | — |
| PhpMyAdmin | http://localhost:8090 | root / 1234 |
| Kafka UI | http://localhost:8082 | — |
| Prometheus | http://localhost:9090 | — |
| Grafana | http://localhost:3000 | admin / admin |

> On first startup, `DevDataSeeder` automatically populates the database with venues, events, tickets and variants — ready for manual testing.

### Without `make`

```bash
docker compose up -d
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## Makefile Commands

| Command | Description |
|---|---|
| `make dev` | Start DB containers and run the API (dev profile) |
| `make db-up` | Start MySQL + PhpMyAdmin only |
| `make db-down` | Stop containers (data is preserved) |
| `make db-reset` | Destroy data volume and restart with a clean database |
| `make test` | Run the test suite |

---

## Seed Data

When running with the `dev` profile, the following data is seeded automatically on first startup:

- **States:** SP, RJ, MG, RS
- **Venues:** Allianz Parque, Maracanã, Mineirão
- **Events:** Rock in Rio 2025, Lollapalooza Brasil 2025, Clássico Mineiro, Festival de Inverno 2024 (past)
- **Ticket variants per ticket:** Full Price, Half Price — Student, Half Price — Senior (60+)

To reset and reseed: `make db-reset` followed by `make dev`.

---

## Running Tests

```bash
make test
# or
./mvnw test
```

Tests use an H2 in-memory database and do not require Docker.

---

## Project Structure

```
src/main/java/com/br/tickets/
├── auth/               # JWT filter, Spring Security config, token service
├── config/             # OpenApiConfig, DevDataSeeder (dev profile only)
├── controllers/        # REST controllers + GlobalExceptionHandler
├── enums/              # Domain enumerations (UserRole, OrderStatus)
├── models/
│   ├── base/           # Entity hierarchy (Auditable, SoftDeletable, UUID/Long ID)
│   ├── dto/            # Request/response records
│   └── *.java          # JPA entities
├── repositories/       # Spring Data JPA repositories
└── services/           # Business logic
```

### Entity ID strategy

- `AutoIncrementIdEntity` (Long) — reference data: Event, Venue, Section, City, etc.
- `UUIDIdEntity` (UUID v7, time-ordered) — transactional data: User, Order, Ticket, Seat, etc.

All entities support **soft delete** via `@SQLDelete` and are audited with `createdAt`/`updatedAt` timestamps.

---

## Roadmap

- [x] `POST /auth/register` — user registration
- [x] `POST /auth/login` — JWT authentication
- [x] Global exception handler with standardized JSON error responses
- [x] Swagger UI / OpenAPI 3 documentation
- [x] Prometheus + Grafana monitoring (import dashboard ID 4701)
- [ ] `PUT` / `DELETE` for events, tickets and venues
- [ ] Checkout flow with PIX payment (MercadoPago / PagSeguro)
- [ ] 20-minute ticket reservation with expiry scheduler
- [ ] Role-based access control (`ADMIN`, `ORGANIZER`, `USER`)

---

## Docker

A `Dockerfile` is included for production builds (multi-stage: Maven build → JRE runtime).

```bash
docker build -t tickets-system-api .
docker run -p 8080:8080 \
  -e DB_URL=... \
  -e DB_USERNAME=... \
  -e DB_PASSWORD=... \
  -e JWT_SECRET=... \
  tickets-system-api
```
