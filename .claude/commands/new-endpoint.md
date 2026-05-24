Implement a complete REST endpoint for the specified resource: $ARGUMENTS

Follow the patterns defined in CLAUDE.md exactly. Execute the steps below in order:

## 1. Understand the scope

From `$ARGUMENTS`, identify:
- Resource name (e.g. "event category", "discount")
- Desired operations (e.g. list, create, find by ID)
- If not specified, implement: GET (paginated list) + GET by ID + POST

## 2. Check what already exists

- Read files for this resource in `models/`, `repositories/`, `services/`, `controllers/`
- Identify what needs to be created vs what already exists

## 3. Create the required files (in this order)

### DTO(s) in `models/dto/`
- `<Resource>ListDTO` as a `record` for list responses
- `Create<Resource>DTO` as a `record` for creation
- Add `@NotBlank`/`@NotNull` on required fields

### Repository in `repositories/`
- Extend `JpaRepository<Entity, IdType>`
- Also extend `JpaSpecificationExecutor<Entity>` if complex filters are needed

### Service in `services/`
- Use `@AllArgsConstructor` + `private final` fields
- Methods: `list(Pageable)`, `findById(id)`, `create(dto)`
- Throw `RuntimeException("X not found with id: " + id)` when entity is not found

### Controller in `controllers/`
- `@RestController` + `@RequestMapping("/api")`
- Pagination with `page`, `size`, `sort` as optional params
- `ResponseEntity<Page<DTO>>` for list; `ResponseEntity` with status 201 for create

## 4. Create the test in `src/test/java/com/br/tickets/controllers/`

- `<Resource>ControllerTest.java`
- Cover: list without filters, list with filter (if applicable), valid creation
- Use `@ExtendWith(MockitoExtension.class)` + `MockMvcBuilders.standaloneSetup`
- Method naming: `method_scenario_expectedResult()`

## 5. Update CLAUDE.md

- Move the resource from 🔲 to ✅ in the Roadmap (or add it as ✅ if it was new)

## 6. Run tests

Execute `./mvnw test` and fix any failures before reporting completion.

When done, list the files created/modified and confirm tests passed.
