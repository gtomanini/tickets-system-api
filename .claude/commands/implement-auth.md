Activate and complete the JWT authentication system that is partially implemented in this project.

The JWT infrastructure already exists in `src/main/java/com/br/tickets/auth/`. What needs to be done:

## Current state (verify before starting)

- `auth/JwtUtil.java` — JWT utility
- `auth/services/JwtService.java` — token generation and validation
- `auth/services/AuthService.java` — authentication logic
- `auth/filter/JwtAuthenticationFilter.java` — Spring Security filter
- `auth/impl/UserDetailsServiceImpl.java` — Spring Security integration
- `auth/config/SecurityConfig.java` — configuration (admin routes commented out)
- `controllers/AuthController.java` — **commented out**, needs to be uncommented
- `models/dto/AuthRequest.java` — existing login DTO

## What to implement

### 1. Uncomment and complete AuthController

Activate `AuthController.java` and ensure it has:
- `POST /auth/login` — receives `AuthRequest` (email + password), returns `JwtResponse`
- `POST /auth/register` — receives `RegisterRequest`, creates user, returns `JwtResponse`

Check `auth/requests/RegisterRequest.java` for the registration fields.

### 2. Complete AuthService

Ensure `AuthService` has:
- `login(AuthRequest)` → validates credentials via `AuthenticationManager`, generates JWT, returns `JwtResponse`
- `register(RegisterRequest)` → creates `User`, hashes password via `BCryptPasswordEncoder`, saves, returns JWT

### 3. Verify UserRepository

Check that `UserRepository` has `findByEmail(String email)` — required by `UserDetailsServiceImpl`.

### 4. Activate protected routes in SecurityConfig

Uncomment the lines:
```java
.requestMatchers("/admin/**").hasRole("ADMIN")
.requestMatchers("/organizer/**").hasRole("ORGANIZER")
```

### 5. Write tests

Create `AuthControllerTest.java` covering:
- `login_validCredentials_returnsToken()`
- `login_invalidCredentials_returns401()`
- `register_newUser_returnsToken()`
- `register_duplicateEmail_returns400()`

### 6. Manual smoke test

```bash
# Register
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"123456","name":"Test User"}'

# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"123456"}'
```

### 7. Update CLAUDE.md

Move auth items from 🔲 to ✅ in the Roadmap.

## Important

- Never store passwords in plaintext — always `passwordEncoder.encode(password)`
- `JWT_SECRET` must come from an environment variable, never hardcoded
- Verify `application.properties` has `jwt.secret=${JWT_SECRET}` and that `JwtService` reads it correctly
