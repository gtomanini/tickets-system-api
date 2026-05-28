package com.br.tickets.controllers.advice;

import com.br.tickets.models.dto.ApiErrorDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    // Minimal stub controller whose endpoints throw the exceptions we want to test
    @RestController
    @RequestMapping("/test")
    @Validated
    static class StubController {

        @GetMapping("/not-found")
        public void notFound() {
            throw new RuntimeException("Ticket not found with id: 99");
        }

        @GetMapping("/invalid-credentials")
        public void invalidCredentials() {
            throw new RuntimeException("Invalid credentials provided");
        }

        @GetMapping("/already-in-use")
        public void alreadyInUse() {
            throw new RuntimeException("Email already in use: test@example.com");
        }

        @GetMapping("/unexpected")
        public void unexpected() {
            throw new RuntimeException("Something totally unexpected happened");
        }

        @GetMapping("/illegal-argument")
        public void illegalArgument() {
            throw new IllegalArgumentException("Variant quantity (200) cannot exceed ticket total capacity (100).");
        }

        @GetMapping("/bad-credentials")
        public void badCredentials() {
            throw new BadCredentialsException("Bad credentials");
        }

        @GetMapping("/data-integrity")
        public void dataIntegrity() {
            throw new DataIntegrityViolationException("Duplicate entry");
        }

        @GetMapping("/general-exception")
        public void generalException() throws Exception {
            throw new Exception("Unexpected checked exception");
        }

        @PostMapping("/validation")
        public void validation(@RequestBody @Valid ValidationPayload payload) {
        }

        record ValidationPayload(@NotBlank(message = "Name is required") String name) {}
    }

    @BeforeEach
    void setUp() {
        // Standalone MockMvc does not auto-configure bean validation; set it up explicitly
        // so @Valid on @RequestBody triggers MethodArgumentNotValidException properly.
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new StubController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void handleRuntimeException_notFoundMessage_returns404() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Ticket not found with id: 99"));
    }

    @Test
    void handleRuntimeException_invalidCredentialsMessage_returns401() throws Exception {
        mockMvc.perform(get("/test/invalid-credentials"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    void handleRuntimeException_alreadyInUseMessage_returns409() throws Exception {
        mockMvc.perform(get("/test/already-in-use"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"));
    }

    @Test
    void handleRuntimeException_unknownMessage_returns500() throws Exception {
        mockMvc.perform(get("/test/unexpected"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Internal server error"));
    }

    @Test
    void handleValidation_invalidPayload_returns400WithFieldError() throws Exception {
        String blankNamePayload = "{\"name\":\"\"}";

        mockMvc.perform(post("/test/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(blankNamePayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Name is required"));
    }

    @Test
    void handleIllegalArgument_returns400WithMessage() throws Exception {
        mockMvc.perform(get("/test/illegal-argument"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Variant quantity (200) cannot exceed ticket total capacity (100)."));
    }

    @Test
    void handleBadCredentials_returns401() throws Exception {
        mockMvc.perform(get("/test/bad-credentials"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    @Test
    void handleDataIntegrity_returns409() throws Exception {
        mockMvc.perform(get("/test/data-integrity"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Data conflict: a record with this value already exists"));
    }

    @Test
    void handleGeneral_checkedExceptionNotCovered_returns500() throws Exception {
        mockMvc.perform(get("/test/general-exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Internal server error"));
    }

    @Test
    void errorResponse_alwaysIncludesTimestamp() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }
}
