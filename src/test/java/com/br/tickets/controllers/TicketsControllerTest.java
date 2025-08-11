package com.br.tickets.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.br.tickets.config.TestSecurityConfig;
import com.br.tickets.models.Event;
import com.br.tickets.models.Ticket;
import com.br.tickets.models.dto.TicketListDTO;
import com.br.tickets.repositories.EventsRepository;
import com.br.tickets.repositories.TicketsRepository;
import com.br.tickets.services.EventsService;
import com.br.tickets.services.TicketsService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
public class TicketsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TicketsService ticketsService;

    @Mock
    private TicketsRepository ticketsRepository;

    @InjectMocks
    private TicketsController ticketsController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ticketsController).build();
    }

    @Test
    void list_ticketsByEventId() throws Exception {

        Ticket ticket1 = Ticket.builder()
                .name("VIP Ticket")
                .amount(100)
                .event(Event.builder().build())
                .netAmount(new BigDecimal("90.00"))
                .quantity(50)
                .obs("Includes free drinks")
                .build();

        Ticket ticket2 = Ticket.builder()
                .name("Regular Ticket")
                .amount(50)
                .event(Event.builder().build())
                .netAmount(new BigDecimal("45.00"))
                .quantity(100)
                .obs("Standard entry")
                .build();

        TicketListDTO ticketListDTO1 = new TicketListDTO(
                ticket1.getId(), 
                ticket1.getName(),
                ticket1.getAmount(),
                ticket1.getObs()
        );

        TicketListDTO ticketListDTO2 = new TicketListDTO(
                ticket2.getId(), 
                ticket2.getName(),
                ticket2.getAmount(),
                ticket2.getObs()
        );

        
        when(ticketsService.getTicketsByEventId(any())).thenReturn(List.of(ticketListDTO1, ticketListDTO2));

        
        mockMvc.perform(get("/api/tickets?eventId=1")
            .contentType(MediaType.APPLICATION_JSON) 
            .content(this.objectMapper.writeValueAsString(Ticket.class)))
            .andExpect(status().isOk());
            // .andExpect(jsonPath("$.name").value("VIP Ticket"));

    }
    
}
