package com.br.tickets.controllers;

import com.br.tickets.controllers.advice.GlobalExceptionHandler;
import com.br.tickets.models.dto.CreateTicketDTO;
import com.br.tickets.models.dto.TicketListDTO;
import com.br.tickets.services.TicketsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

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
class TicketsControllerTest {

    private MockMvc mockMvc;

    @Mock private TicketsService ticketsService;
    @InjectMocks private TicketsController ticketsController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(ticketsController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void list_ticketsByEventId_returnsTicketsWithVariants() throws Exception {
        TicketListDTO ticket = new TicketListDTO(
                UUID.randomUUID(), "Inteira", "Standard ticket", 5000, 1L, "Pista", List.of());

        when(ticketsService.getTicketsByEventId(any())).thenReturn(List.of(ticket));

        mockMvc.perform(get("/api/tickets").param("eventId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Inteira"))
                .andExpect(jsonPath("$[0].totalCapacity").value(5000))
                .andExpect(jsonPath("$[0].sectionName").value("Pista"))
                .andExpect(jsonPath("$[0].variants").isArray());
    }

    @Test
    void create_validPayload_returnsCreated() throws Exception {
        CreateTicketDTO dto = new CreateTicketDTO("Inteira", 1L, 5000, null);
        TicketListDTO response = new TicketListDTO(
                UUID.randomUUID(), "Inteira", null, 5000, 1L, "Pista", List.of());

        when(ticketsService.createTicket(eq(1L), any(CreateTicketDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/events/1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Inteira"))
                .andExpect(jsonPath("$.sectionName").value("Pista"));
    }

    @Test
    void create_missingSectionId_returnsBadRequest() throws Exception {
        CreateTicketDTO dto = new CreateTicketDTO("Inteira", null, 5000, null);

        mockMvc.perform(post("/api/events/1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_eventNotFound_returns404() throws Exception {
        CreateTicketDTO dto = new CreateTicketDTO("Inteira", 1L, 5000, null);

        when(ticketsService.createTicket(eq(99L), any(CreateTicketDTO.class)))
                .thenThrow(new RuntimeException("Event not found with id: 99"));

        mockMvc.perform(post("/api/events/99/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }
}
