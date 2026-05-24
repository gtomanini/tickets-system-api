# Tickets System API — Especificação do Projeto

Este arquivo é a **fonte da verdade** para desenvolvimento com IA. Toda nova feature, correção ou refatoração deve seguir as regras aqui definidas. Se houver conflito entre este documento e o código existente, resolva atualizando ambos.

---

## Visão Geral

API RESTful para gestão de eventos e venda de ingressos. Permite que organizadores cadastrem eventos em venues, definam tipos de ingresso e que usuários comprem ingressos com geração de pedidos.

**Stack:** Java 21 · Spring Boot 3.5.x · MySQL 8 · JWT · Docker  
**Deploy:** Render.com (produção) · Docker Compose (local)

---

## Arquitetura

```
controllers/     → Recebe HTTP, delega para services, nunca contém regra de negócio
services/        → Toda a lógica de negócio vive aqui
repositories/    → Apenas Spring Data JPA; queries complexas usam Specification
models/          → Entidades JPA + DTOs (records)
models/base/     → Hierarquia de base para entidades (ver abaixo)
auth/            → Infraestrutura JWT e Spring Security
enums/           → Enumerações de domínio
```

### Hierarquia de entidades

```
BaseClass
  └── AuditableEntity          (createdAt, updatedAt — JPA Auditing)
        └── SoftDeletableEntity (deleted=false; usa @SQLDelete + @Where)
              ├── AutoIncrementIdEntity  (Long id — entidades de domínio principal)
              └── UUIDIdEntity           (UUID id — usuários, pedidos, tickets)
```

**Regra:** toda nova entidade estende `AutoIncrementIdEntity` (para dados de configuração/referência) ou `UUIDIdEntity` (para dados transacionais ligados a usuários).

---

## Domínio e Regras de Negócio

### Entidades principais
| Entidade | ID | Descrição |
|---|---|---|
| `Event` | Long | Evento com venue, organizer, tickets, orders e age rating |
| `Venue` | Long | Local do evento com seções e assentos |
| `Ticket` | UUID | Tipo de ingresso (nome, valor, quantidade, validade de venda) |
| `Order` | UUID | Pedido de compra de um usuário |
| `OrderTicket` | UUID | Ingresso individual dentro de um pedido |
| `User` | UUID | Usuário base; subclasse `Organizer` para criadores de eventos |
| `Section` | Long | Seção de um venue (pode ser numerada ou geral) |
| `Seat` | UUID | Assento específico dentro de uma seção |

### Roles de usuário
- `USER` — compra ingressos
- `ORGANIZER` — cria e gerencia eventos (extends User)
- `ADMIN` — acesso total

### Status de Order (`OrderStatus`)
- `PENDING`, `CONFIRMED`, `CANCELLED`, `REFUNDED`

### Soft delete
Todas as entidades são soft-deletadas. Nunca use `repository.deleteById()` diretamente — o `@SQLDelete` cuida disso automaticamente quando chamado via `delete(entity)`.

---

## Padrões de Código

### Controllers
- Anotação de classe: `@RestController` + `@RequestMapping("/api")`
- Injetar services via `@Autowired` (padrão atual do projeto)
- Retornar `ResponseEntity<T>` nos métodos GET com paginação; retorno direto para POST simples
- Nunca colocar lógica de negócio no controller

```java
@RestController
@RequestMapping("/api")
public class ExemploController {

    @Autowired
    private ExemploService exemploService;

    @GetMapping("/exemplos")
    public ResponseEntity<Page<ExemploListDTO>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(exemploService.listar(pageable));
    }

    @PostMapping("/exemplos")
    public ResponseEntity<ExemploListDTO> create(@RequestBody @Valid CreateExemploDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(exemploService.criar(dto));
    }
}
```

### Services
- Usar `@AllArgsConstructor` (Lombok) + `private final` para injeção via construtor (padrão preferido em serviços novos)
- Busca com filtros → usar `Specification<T>` via `JpaSpecificationExecutor`
- Lançar `RuntimeException` com mensagem descritiva quando entidade não for encontrada (até implementarmos exceptions customizadas)

```java
@Service
@AllArgsConstructor
public class ExemploService {

    private final ExemploRepository exemploRepository;

    public ExemploListDTO buscarPorId(Long id) {
        Exemplo e = exemploRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Exemplo not found with id: " + id));
        return toDTO(e);
    }
}
```

### DTOs
- Sempre usar Java `record` (imutável, sem boilerplate)
- Nomenclatura: `Create<Entidade>DTO`, `<Entidade>ListDTO`, `<Entidade>DetailDTO`
- DTOs nunca estendem entidades nem contêm anotações JPA

```java
public record CreateExemploDTO(
    @NotBlank String nome,
    @NotNull Long categoriaId
) {}
```

### Entidades
- Anotações Lombok obrigatórias: `@Entity`, `@Table(name="...")`, `@AllArgsConstructor`, `@NoArgsConstructor`, `@Builder`, `@Getter`, `@Setter`
- Relacionamentos lazy por padrão; usar `FetchType.EAGER` apenas quando necessário e documentado
- Não mapear colunas de FK redundantes (evitar `@Column(name="x_id", insertable=false, updatable=false)` em novos campos)

### Repositories
- Estendem `JpaRepository<T, ID>`; quando precisar de filtros complexos, também estendem `JpaSpecificationExecutor<T>`
- Queries simples via nome de método Spring Data; queries complexas via `Specification` ou `@Query` com JPQL

### Testes
- Framework: JUnit 5 + Mockito + MockMvc
- Classe de teste: `<Entidade>ControllerTest` em `src/test/java/com/br/tickets/controllers/`
- Cada método de teste: `metodo_cenario_resultado()` (ex: `create_validEvent_returnsCreatedEvent`)
- Mockar o service no teste do controller; nunca mockar o repository nos testes do controller
- Usar `@ExtendWith(MockitoExtension.class)` + `MockMvcBuilders.standaloneSetup(controller).build()`

```java
@ExtendWith(MockitoExtension.class)
class ExemploControllerTest {

    private MockMvc mockMvc;

    @Mock private ExemploService exemploService;
    @InjectMocks private ExemploController exemploController;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(exemploController).build();
    }
}
```

---

## Convenções de API

| Operação | Método | Path | Status |
|---|---|---|---|
| Listar (paginado) | GET | `/api/{recurso}s` | 200 |
| Buscar por ID | GET | `/api/{recurso}s/{id}` | 200 / 404 |
| Criar | POST | `/api/{recurso}s` | 201 |
| Atualizar | PUT | `/api/{recurso}s/{id}` | 200 |
| Deletar | DELETE | `/api/{recurso}s/{id}` | 204 |

**Paginação:** parâmetros `page` (default 0), `size` (default 10), `sort` (default `"name,desc"`)  
**Filtros:** parâmetros de query opcionais mapeados para `<Entidade>SearchCriteria` + `Specification`

---

## Estado Atual & Roadmap

### Implementado ✅
- `GET /api/events` — listagem paginada com filtro por nome e status
- `POST /api/events` — criação de evento
- `GET /api/tickets?eventId=` — tickets por evento
- `GET /api/venues` — listagem de venues
- `GET /api/venues/{id}` — venue por ID
- Infraestrutura JWT (filter, service, util) — funcionando mas endpoints comentados
- Docker Compose com MySQL + PhpMyAdmin
- Testes unitários para Events, Tickets e Venues

### A implementar 🔲
- `POST /auth/register` — cadastro de usuário (infrastructure pronta, descomentar e completar)
- `POST /auth/login` — login com JWT (descomentar `AuthController`)
- `GET /admin/events` — listagem admin com role ADMIN (descomentar e implementar)
- Checkout flow (`CheckoutController` — stub existente, implementar lógica de pedido)
- `PUT` e `DELETE` para events, tickets e venues
- Validação de input com `@Valid` nos controllers (jakarta.validation já no classpath)
- Exceptions customizadas com `@ControllerAdvice` para padronizar erros 4xx/5xx

---

## Segurança

- Autenticação: JWT Bearer token via `Authorization: Bearer <token>`
- Endpoints públicos ativos: `/api/**`, `/auth/login`, `/actuator/**`
- Rotas protegidas (comentadas, a ativar): `/admin/**` exige role ADMIN
- Senhas: sempre `BCryptPasswordEncoder` — nunca armazenar em plaintext

---

## Ambiente Local

```bash
# Subir banco e PhpMyAdmin
docker compose up -d

# Rodar API
./mvnw spring-boot:run

# Rodar testes
./mvnw test
```

Variáveis de ambiente necessárias:
```
DB_URL=jdbc:mysql://localhost:3306/tickets
DB_USERNAME=admin
DB_PASSWORD=1234
JWT_SECRET=<segredo>
```

---

## Regras para a IA

1. **Seguir os padrões acima** — não inventar novos padrões sem atualizar este arquivo
2. **Atualizar o Roadmap** ao implementar features (mover de 🔲 para ✅)
3. **Criar testes** para todo endpoint novo antes de marcar a feature como completa
4. **DTOs como records** — nunca criar classe mutável quando um record serve
5. **Não remover** código comentado existente sem entender o porquê está comentado
6. **Soft delete** — nunca usar `deleteById`; sempre `repository.delete(entity)` para que o `@SQLDelete` funcione
7. **Injeção via construtor** (`@AllArgsConstructor` + `final`) em serviços novos; `@Autowired` campo só em controllers existentes por consistência
