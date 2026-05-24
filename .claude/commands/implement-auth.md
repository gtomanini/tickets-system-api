Ative e complete o sistema de autenticação JWT que está parcialmente implementado neste projeto.

A infraestrutura JWT já existe em `src/main/java/com/br/tickets/auth/`. O que precisa ser feito:

## Estado atual (verifique antes de começar)

- `auth/JwtUtil.java` — utilitário JWT
- `auth/services/JwtService.java` — geração e validação de tokens
- `auth/services/AuthService.java` — lógica de autenticação
- `auth/filter/JwtAuthenticationFilter.java` — filtro Spring Security
- `auth/impl/UserDetailsServiceImpl.java` — integração com Spring Security
- `auth/config/SecurityConfig.java` — configuração (rotas admin comentadas)
- `controllers/AuthController.java` — **comentado**, precisa ser descomentado
- `models/dto/AuthRequest.java` — DTO de login existente

## O que implementar

### 1. Descomentar e completar o AuthController

Ative o `AuthController.java` e garanta que ele tem:
- `POST /auth/login` — recebe `AuthRequest` (email + password), retorna `JwtResponse`
- `POST /auth/register` — recebe `RegisterRequest`, cria usuário, retorna `JwtResponse`

Verifique `auth/requests/RegisterRequest.java` para os campos do registro.

### 2. Completar o AuthService

Garanta que `AuthService` tem os métodos:
- `login(AuthRequest)` → valida credenciais via `AuthenticationManager`, gera JWT, retorna `JwtResponse`
- `register(RegisterRequest)` → cria `User`, hash da senha via `BCryptPasswordEncoder`, salva, retorna JWT

### 3. Ativar UserRepository

Verificar se `UserRepository` tem o método `findByEmail(String email)` — necessário para `UserDetailsServiceImpl`.

### 4. Ativar rotas protegidas no SecurityConfig

Descomentar as linhas:
```java
.requestMatchers("/admin/**").hasRole("ADMIN")
.requestMatchers("/organizer/**").hasRole("ORGANIZER")
```

### 5. Criar testes

Criar `AuthControllerTest.java` cobrindo:
- `login_validCredentials_returnsToken()`
- `login_invalidCredentials_returns401()`
- `register_newUser_returnsToken()`
- `register_duplicateEmail_returns400()`

### 6. Testar manualmente

```bash
# Registrar
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"123456","name":"Test User"}'

# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"123456"}'
```

### 7. Atualizar CLAUDE.md

Mova os itens de auth de 🔲 para ✅ no Roadmap.

## Atenção

- Nunca armazenar senha em plaintext — sempre `passwordEncoder.encode(senha)`
- O `JWT_SECRET` deve vir de variável de ambiente, não hardcoded
- Verificar se `application.properties` tem `jwt.secret=${JWT_SECRET}` e se `JwtService` lê corretamente
