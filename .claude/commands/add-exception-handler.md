Implement a global exception handler to standardize API error responses.

## What to create

### 1. Error response record in `models/dto/`

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

### 2. Global handler in `controllers/` (or `controllers/advice/`)

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorDTO> handleNotFound(RuntimeException ex) {
        // Map "X not found with id: Y" to 404
        // Other RuntimeException → 500
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDTO> handleValidation(MethodArgumentNotValidException ex) {
        // Collect all field errors and return a unified message
        // Status 400
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> handleGeneral(Exception ex) {
        // Generic 500
    }
}
```

### 3. Mapping rules

| Exception | HTTP Status |
|---|---|
| `RuntimeException` with "not found" in the message | 404 |
| `MethodArgumentNotValidException` | 400 |
| `DataIntegrityViolationException` | 409 Conflict |
| Any other `Exception` | 500 |

### 4. Write tests

`GlobalExceptionHandlerTest.java` covering:
- 404 when service throws RuntimeException with "not found"
- 400 when payload is invalid (missing required field)

### 5. Update CLAUDE.md

After implementing, update the "To implement" section in the Roadmap.

## Important

- Never expose the full stack trace to the client — message only
- In development, more detail may be returned
- Verify that `SecurityConfig` is not intercepting 4xx responses before they reach the handler
