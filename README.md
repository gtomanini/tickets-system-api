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
| Build | Maven 3.9 |
| Container | Docker + Docker Compose |

---

## API Endpoints

### Events
| Method | Path | Description |
|---|---|---|
| `GET` | `/api/events` | Paginated event listing with optional filters |
| `POST` | `/api/events` | Create a new event |

**Query params for `GET /api/events`:**
- `name` — partial name filter (case-insensitive)
- `status` — exact status match
- `page` — page number (default: `0`)
- `size` — page size (default: `10`)
- `sort` — sort field and direction (default: `name,desc`)

### Tickets
| Method | Path | Description |
|---|---|---|
| `GET` | `/api/tickets?eventId={id}` | List ticket types with variants for an event |
| `GET` | `/api/tickets/{ticketId}/variants` | List variants for a ticket |
| `POST` | `/api/tickets/{ticketId}/variants` | Create a variant for a ticket |

### Venues
| Method | Path | Description |
|---|---|---|
| `GET` | `/api/venues` | List all venues |
| `GET` | `/api/venues/{id}` | Get venue by ID |

### Health
| Method | Path | Description |
|---|---|---|
| `GET` | `/actuator/health` | Application health check |

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

This starts MySQL 8 + PhpMyAdmin via Docker Compose, then boots the app with the `dev` profile.

| Service | URL |
|---|---|
| API | http://localhost:8080 |
| PhpMyAdmin | http://localhost:8090 |

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
├── config/             # DevDataSeeder (dev profile only)
├── controllers/        # REST controllers
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

- [ ] `POST /auth/register` — user registration
- [ ] `POST /auth/login` — JWT authentication
- [ ] `PUT` / `DELETE` for events, tickets and venues
- [ ] Checkout flow with PIX payment (MercadoPago / PagSeguro)
- [ ] 20-minute ticket reservation with expiry scheduler
- [ ] Role-based access control (`ADMIN`, `ORGANIZER`, `USER`)
- [ ] Global exception handler with standardized error responses

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
