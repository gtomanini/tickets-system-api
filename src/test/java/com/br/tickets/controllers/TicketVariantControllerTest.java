package com.br.tickets.controllers;

import com.br.tickets.models.dto.CreateTicketVariantDTO;
import com.br.tickets.models.dto.TicketVariantListDTO;
import com.br.tickets.services.TicketVariantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TicketVariantControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TicketVariantService ticketVariantService;

    @InjectMocks
    private TicketVariantController ticketVariantController;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ticketVariantController).build();
    }

    @Test
    void list_existingTicket_returnsVariants() throws Exception {
        UUID ticketId = UUID.randomUUID();
        TicketVariantListDTO variant = new TicketVariantListDTO(
                UUID.randomUUID(), "Full Price", new BigDecimal("100.00"), 200, null, null, false
        );

        when(ticketVariantService.getVariantsByTicketId(ticketId)).thenReturn(List.of(variant));

        mockMvc.perform(get("/api/tickets/{ticketId}/variants", ticketId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Full Price"))
                .andExpect(jsonPath("$[0].quantity").value(200));
    }

    @Test
    void create_validVariant_returnsCreated() throws Exception {
        UUID ticketId = UUID.randomUUID();
        CreateTicketVariantDTO dto = new CreateTicketVariantDTO(
                "Half Price - Student", new BigDecimal("50.00"), 100, null, null, true
        );
        TicketVariantListDTO created = new TicketVariantListDTO(
                UUID.randomUUID(), "Half Price - Student", new BigDecimal("50.00"), 100, null, null, true
        );

        when(ticketVariantService.create(eq(ticketId), any(CreateTicketVariantDTO.class))).thenReturn(created);

        mockMvc.perform(post("/api/tickets/{ticketId}/variants", ticketId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Half Price - Student"))
                .andExpect(jsonPath("$.quantity").value(100))
                .andExpect(jsonPath("$.requiresDocument").value(true));
    }

    // Quantity-exceeded error handling is tested in TicketVariantServiceTest.
    // A proper 400 response requires the GlobalExceptionHandler (@ControllerAdvice) — see /add-exception-handler.
}
