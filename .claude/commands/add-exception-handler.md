Implemente um handler global de exceções para padronizar as respostas de erro da API.

## O que criar

### 1. Classe de resposta de erro em `models/dto/`

```java
public record ApiErrorDTO(
    int status,
    String error,
    String message,
    LocalDateTime timestamp
) {
    public static ApiErrorDTO of(HttpStatus status, String message) {
        return new ApiErrorDTO(status.value(), status.getReasonPhrase(), message, LocalDateTime.now());
    }
}
```

### 2. Handler global em `controllers/` (ou `controllers/advice/`)

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorDTO> handleNotFound(RuntimeException ex) {
        // Mapear "X not found with id: Y" para 404
        // Outros RuntimeException → 500
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDTO> handleValidation(MethodArgumentNotValidException ex) {
        // Coletar todos os erros de field e retornar mensagem unificada
        // Status 400
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> handleGeneral(Exception ex) {
        // Status 500 genérico
    }
}
```

### 3. Regras de mapeamento

| Exceção | Status HTTP |
|---|---|
| `RuntimeException` com "not found" na mensagem | 404 |
| `MethodArgumentNotValidException` | 400 |
| `DataIntegrityViolationException` | 409 Conflict |
| Qualquer outra `Exception` | 500 |

### 4. Criar teste

`GlobalExceptionHandlerTest.java` cobrindo:
- 404 quando service lança RuntimeException com "not found"
- 400 quando payload inválido (campo obrigatório ausente)

### 5. Atualizar CLAUDE.md

Após implementar, atualizar a seção "A implementar" no Roadmap.

## Atenção

- Não logar stack trace completo para o cliente — apenas mensagem
- Em ambiente de desenvolvimento, pode retornar mais detalhes
- Verificar que o `SecurityConfig` não está interceptando os 4xx antes de chegar ao handler
