package com.br.tickets.controllers;

import com.br.tickets.models.Event;
import com.br.tickets.models.dto.CreateEventDTO;
import com.br.tickets.models.dto.EventListDTO;
import com.br.tickets.models.dto.EventSearchCriteria;
import com.br.tickets.services.EventsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EventsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private EventsService eventsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateEvent() throws Exception {
        CreateEventDTO dto = new CreateEventDTO(
                "Show Teste",
                "1",
                "Descrição do evento",
                LocalDateTime.of(2025, 8, 10, 19, 0),
                LocalDateTime.of(2025, 8, 10, 23, 0),
                true
        );

        Event savedEvent = new Event();
        savedEvent.setId(1L);
        savedEvent.setName(dto.name());
        savedEvent.setStatus(dto.status());
        savedEvent.setDescription(dto.description());
        savedEvent.setStartDate(dto.startDate());
        savedEvent.setEndDate(dto.endDate());
        savedEvent.setFeatured(dto.featured());
        savedEvent.setClosed(false);

        Mockito.when(eventsService.saveEvent(Mockito.any())).thenReturn(savedEvent);

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Show Teste")));
    }

    @Test
    void testListEvents() throws Exception {
        EventListDTO eventDto = new EventListDTO(
                1L,
                "Evento 1",
                LocalDateTime.of(2025, 8, 10, 20, 0),
                "active",
                "Descrição",
                LocalDateTime.of(2025, 8, 10, 19, 0),
                LocalDateTime.of(2025, 8, 10, 23, 0),
                true,
                false
        );

        Page<EventListDTO> page = new PageImpl<>(List.of(eventDto));

        Mockito.when(eventsService.searchEvents(Mockito.any(), Mockito.any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/events")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "name,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].name", is("Evento 1")));
    }
}
