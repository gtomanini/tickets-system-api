Implemente um novo endpoint REST completo para o recurso especificado: $ARGUMENTS

Siga exatamente os padrões definidos no CLAUDE.md. Execute as etapas abaixo em ordem:

## 1. Entenda o escopo

Identifique a partir de `$ARGUMENTS`:
- Nome do recurso (ex: "categoria de evento", "desconto")
- Operações desejadas (ex: listar, criar, buscar por ID)
- Se não especificado, implemente: GET (lista paginada) + GET por ID + POST

## 2. Verifique o que já existe

- Leia os arquivos do recurso em `models/`, `repositories/`, `services/`, `controllers/`
- Identifique o que precisa ser criado vs o que já existe

## 3. Crie os arquivos necessários (nesta ordem)

### DTO(s) em `models/dto/`
- `<Recurso>ListDTO` como `record` para respostas de listagem
- `Create<Recurso>DTO` como `record` para criação
- Adicionar `@NotBlank`/`@NotNull` nos campos obrigatórios

### Repository em `repositories/`
- Estender `JpaRepository<Entidade, TipoId>`
- Se houver filtros complexos, estender também `JpaSpecificationExecutor<Entidade>`

### Service em `services/`
- Usar `@AllArgsConstructor` + campos `private final`
- Métodos: `listar(Pageable)`, `buscarPorId(id)`, `criar(dto)`
- Lançar `RuntimeException("X not found with id: " + id)` quando não encontrar

### Controller em `controllers/`
- `@RestController` + `@RequestMapping("/api")`
- Paginação com `page`, `size`, `sort` como params opcionais
- `ResponseEntity<Page<DTO>>` para listagem; `ResponseEntity` com status 201 para criação

## 4. Crie o teste em `src/test/java/com/br/tickets/controllers/`

- `<Recurso>ControllerTest.java`
- Cobrir: listagem sem filtro, listagem com filtro (se houver), criação válida
- Usar `@ExtendWith(MockitoExtension.class)` + `MockMvcBuilders.standaloneSetup`
- Nomenclatura: `metodo_cenario_resultado()`

## 5. Atualize o CLAUDE.md

- Mova o recurso de 🔲 para ✅ no Roadmap (ou adicione-o como ✅ se era novo)

## 6. Rode os testes

Execute `./mvnw test` e corrija qualquer falha antes de reportar conclusão.

Ao terminar, liste os arquivos criados/modificados e confirme que os testes passaram.
