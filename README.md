# Tickets System API

A RESTful API for event management and ticket sales. Organizers create events at venues, define ticket types, and users purchase tickets through orders.

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
| `GET` | `/api/tickets?eventId={id}` | List ticket types for an event |

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

## Running Locally

### Prerequisites

- Java 21+
- Docker and Docker Compose

### 1. Clone the repository

```bash
git clone https://github.com/gtomanini/tickets-system-api.git
cd tickets-system-api
```

### 2. Start the database

```bash
docker compose up -d
```

This starts MySQL 8 on port `3306` and PhpMyAdmin on port `8090`.

### 3. Set environment variables

```bash
export DB_URL=jdbc:mysql://localhost:3306/tickets
export DB_USERNAME=admin
export DB_PASSWORD=1234
export JWT_SECRET=your-secret-key
```

### 4. Run the application

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

---

## Running Tests

```bash
./mvnw test
```

Tests use an H2 in-memory database and do not require a running MySQL instance.

---

## Project Structure

```
src/main/java/com/br/tickets/
├── auth/               # JWT filter, Spring Security config, token service
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
- [ ] Checkout flow — order creation and ticket issuance
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
