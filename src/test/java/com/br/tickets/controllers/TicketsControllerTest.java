package com.br.tickets.controllers;

import com.br.tickets.models.dto.TicketListDTO;
import com.br.tickets.services.TicketsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TicketsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TicketsService ticketsService;

    @InjectMocks
    private TicketsController ticketsController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ticketsController).build();
    }

    @Test
    void list_ticketsByEventId_returnsTicketsWithVariants() throws Exception {
        TicketListDTO ticket = new TicketListDTO(UUID.randomUUID(), "VIP", "Includes free drinks", 200, List.of());

        when(ticketsService.getTicketsByEventId(any())).thenReturn(List.of(ticket));

        mockMvc.perform(get("/api/tickets").param("eventId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("VIP"))
                .andExpect(jsonPath("$[0].totalCapacity").value(200))
                .andExpect(jsonPath("$[0].variants").isArray());
    }
}
