# Tickets System API — Project Specification

This file is the **source of truth** for AI-assisted development. Every new feature, bug fix, or refactor must follow the rules defined here. If there is a conflict between this document and the existing code, resolve it by updating both.

---

## Overview

RESTful API for event management and ticket sales. Organizers create events at venues, define ticket types, and users purchase tickets through orders.

**Stack:** Java 21 · Spring Boot 3.5.x · MySQL 8 · JWT · Docker  
**Deploy:** Docker Compose (local)

---

## Architecture

```
controllers/     → Receives HTTP requests, delegates to services, never holds business logic
services/        → All business logic lives here
repositories/    → Spring Data JPA only; complex queries use Specification
models/          → JPA entities + DTOs (records)
models/base/     → Base entity hierarchy (see below)
auth/            → JWT infrastructure and Spring Security
enums/           → Domain enumerations
```

### Entity hierarchy

```
BaseClass
  └── AuditableEntity          (createdAt, updatedAt — JPA Auditing)
        └── SoftDeletableEntity (deleted=false; uses @SQLDelete + @Where)
              ├── AutoIncrementIdEntity  (Long id — reference/config entities)
              └── UUIDIdEntity           (UUID id — transactional/user-linked entities)
```

**Rule:** every new entity extends `AutoIncrementIdEntity` (for reference/configuration data) or `UUIDIdEntity` (for transactional data linked to users).

`UUIDIdEntity` uses `@UuidGenerator(style = UuidGenerator.Style.TIME)` — generates UUID v7 (time-ordered), preventing InnoDB index fragmentation without sacrificing non-enumerability.

---

## Domain & Business Rules

### Core entities
| Entity | ID | Description |
|---|---|---|
| `Event` | Long | Event with venue, organizer, tickets, orders and age rating |
| `Venue` | Long | Event location with sections and seats |
| `Ticket` | UUID | Ticket type (name, price, quantity, sale expiry) |
| `Order` | UUID | Purchase order by a user |
| `OrderTicket` | UUID | Individual ticket within an order |
| `User` | UUID | Base user; subclass `Organizer` for event creators |
| `Section` | Long | Section within a venue (numbered or general admission) |
| `Seat` | UUID | Specific seat within a section |

### User roles
- `USER` — purchases tickets
- `ORGANIZER` — creates and manages events (extends User)
- `ADMIN` — full access

### Order status (`OrderStatus`)
- `PENDING`, `CONFIRMED`, `CANCELLED`, `REFUNDED`

### Soft delete
All entities are soft-deleted. Never call `repository.deleteById()` directly — `@SQLDelete` handles it automatically when calling `repository.delete(entity)`.

---

## Code Patterns

### Controllers
- Class annotations: `@RestController` + `@RequestMapping("/api")` + `@AllArgsConstructor`
- Inject services via constructor (`@AllArgsConstructor` + `private final`) — no `@Autowired`
- Return `ResponseEntity<T>` for paginated GETs; direct return for simple POSTs
- Never put business logic in a controller

```java
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ExampleController {

    private final ExampleService exampleService;

    @GetMapping("/examples")
    public ResponseEntity<Page<ExampleListDTO>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(exampleService.list(pageable));
    }

    @PostMapping("/examples")
    public ResponseEntity<ExampleListDTO> create(@RequestBody @Valid CreateExampleDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(exampleService.create(dto));
    }
}
```

### Services
- Use `@AllArgsConstructor` (Lombok) + `private final` for constructor injection (preferred in new services)
- Filtered searches → use `Specification<T>` via `JpaSpecificationExecutor`
- Throw `RuntimeException` with a descriptive message when entity is not found (until custom exceptions are implemented)

```java
@Service
@AllArgsConstructor
public class ExampleService {

    private final ExampleRepository exampleRepository;

    public ExampleListDTO findById(Long id) {
        Example e = exampleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Example not found with id: " + id));
        return toDTO(e);
    }
}
```

### DTOs
- Always use Java `record` (immutable, no boilerplate)
- Naming: `Create<Entity>DTO`, `<Entity>ListDTO`, `<Entity>DetailDTO`
- DTOs never extend entities and never contain JPA annotations

```java
public record CreateExampleDTO(
    @NotBlank String name,
    @NotNull Long categoryId
) {}
```

### Entities
- Required Lombok annotations: `@Entity`, `@Table(name="...")`, `@AllArgsConstructor`, `@NoArgsConstructor`, `@Builder`, `@Getter`, `@Setter`
- Relationships are lazy by default; use `FetchType.EAGER` only when necessary and justified
- Do not map redundant FK columns (avoid `@Column(name="x_id", insertable=false, updatable=false)` for new fields)

### Repositories
- Extend `JpaRepository<T, ID>`; also extend `JpaSpecificationExecutor<T>` when filtered searches are needed
- Simple queries via Spring Data method names; complex queries via `Specification` or `@Query` with JPQL

### Tests
- Framework: JUnit 5 + Mockito + MockMvc
- Test class: `<Entity>ControllerTest` in `src/test/java/com/br/tickets/controllers/`
- Method naming: `method_scenario_expectedResult()` (e.g. `create_validEvent_returnsCreatedEvent`)
- Mock the service in controller tests; never mock the repository in controller tests
- Use `@ExtendWith(MockitoExtension.class)` + `MockMvcBuilders.standaloneSetup(controller).build()`

```java
@ExtendWith(MockitoExtension.class)
class ExampleControllerTest {

    private MockMvc mockMvc;

    @Mock private ExampleService exampleService;
    @InjectMocks private ExampleController exampleController;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(exampleController).build();
    }
}
```

---

## API Conventions

| Operation | Method | Path | Status |
|---|---|---|---|
| List (paginated) | GET | `/api/{resource}s` | 200 |
| Find by ID | GET | `/api/{resource}s/{id}` | 200 / 404 |
| Create | POST | `/api/{resource}s` | 201 |
| Update | PUT | `/api/{resource}s/{id}` | 200 |
| Delete | DELETE | `/api/{resource}s/{id}` | 204 |

**Pagination:** `page` (default 0), `size` (default 10), `sort` (default `"name,desc"`)  
**Filters:** optional query params mapped to `<Entity>SearchCriteria` + `Specification`

---

## Current State & Roadmap

### Implemented ✅
- `GET /api/events` — paginated listing with name and status filters
- `POST /api/events` — event creation
- `GET /api/tickets?eventId=` — tickets with variants by event
- `GET /api/tickets/{ticketId}/variants` — list variants for a ticket
- `POST /api/tickets/{ticketId}/variants` — create variant (validates quantity against parent capacity)
- `GET /api/venues` — venue listing
- `GET /api/venues/{id}` — venue by ID
- JWT infrastructure (filter, service, util) — working but endpoints commented out
- Docker Compose with MySQL + PhpMyAdmin
- Unit tests for Events, Tickets, Venues and TicketVariants
- UUID v7 via `@UuidGenerator(Style.TIME)` on `UUIDIdEntity`
- `Ticket` holds `totalCapacity` + `@Version` (optimistic lock for checkout); `TicketVariant` holds per-tier pricing and stock

### To implement 🔲
- `POST /auth/register` — user registration (infrastructure ready, uncomment and complete)
- `POST /auth/login` — JWT login (uncomment `AuthController`)
- `GET /admin/events` — admin listing with ADMIN role (uncomment and implement)
- Checkout flow (`CheckoutController` — stub exists, implement order logic)
- `PUT` and `DELETE` for events, tickets and venues
- Input validation with `@Valid` in controllers (jakarta.validation already on classpath)
- Custom exceptions with `@ControllerAdvice` to standardize 4xx/5xx responses

---

## Security

- Authentication: JWT Bearer token via `Authorization: Bearer <token>`
- Currently public: `/api/**`, `/auth/login`, `/actuator/**`
- Protected routes (commented, to be activated): `/admin/**` requires ADMIN role
- Passwords: always `BCryptPasswordEncoder` — never store in plaintext

---

## Local Environment

```bash
# Start database and PhpMyAdmin
docker compose up -d

# Run API
./mvnw spring-boot:run

# Run tests
./mvnw test
```

Required environment variables:
```
DB_URL=jdbc:mysql://localhost:3306/tickets
DB_USERNAME=admin
DB_PASSWORD=1234
JWT_SECRET=<secret>
```

---

## Rules for AI

1. **Follow the patterns above** — do not introduce new patterns without updating this file
2. **Update the Roadmap** when implementing features (move from 🔲 to ✅)
3. **Write tests** for every new endpoint before marking a feature as complete
4. **DTOs as records** — never create a mutable class when a record works
5. **Do not remove** commented-out code without understanding why it was commented out
6. **Soft delete** — never use `deleteById`; always `repository.delete(entity)` so `@SQLDelete` fires
7. **Constructor injection everywhere** — always use `@AllArgsConstructor` + `private final`; never use `@Autowired` field injection
