package com.br.tickets.controllers;

import com.br.tickets.controllers.advice.GlobalExceptionHandler;
import com.br.tickets.enums.AgeRating;
import com.br.tickets.models.dto.*;
import com.br.tickets.services.EventsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class EventsControllerTest {

    private MockMvc mockMvc;

    @Mock private EventsService eventsService;
    @InjectMocks private EventsController eventsController;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private static final EventListDTO SAMPLE_EVENT = new EventListDTO(
            1L, "Test Event", "ACTIVE", "Description",
            LocalDateTime.now(), LocalDateTime.now().plusDays(3),
            null, false, false,
            new AgeRatingDTO("FREE", "Livre"),
            List.of(), List.of()
    );

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(eventsController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setValidator(validator)
                .build();
    }

    @Test
    void list_noParams_returnsPageOfEvents() throws Exception {
        Page<EventListDTO> page = new PageImpl<>(List.of(SAMPLE_EVENT), PageRequest.of(0, 10), 1);
        when(eventsService.searchEvents(any(EventSearchCriteria.class), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Test Event"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void list_withNameParam_returnsFilteredPage() throws Exception {
        Page<EventListDTO> page = new PageImpl<>(List.of(SAMPLE_EVENT), PageRequest.of(0, 10), 1);
        when(eventsService.searchEvents(any(EventSearchCriteria.class), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/events").param("name", "Test Event"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Test Event"));
    }

    @Test
    void create_validPayload_returnsCreated() throws Exception {
        CreateEventDTO dto = new CreateEventDTO(
                "Rock in Rio", 1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(7), "ACTIVE",
                "Festival", false, AgeRating.FREE, null, List.of("rock", "festival"));

        when(eventsService.createEvent(any(CreateEventDTO.class))).thenReturn(SAMPLE_EVENT);

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ageRating.value").value("FREE"));
    }

    @Test
    void create_missingRequiredFields_returnsBadRequest() throws Exception {
        CreateEventDTO dto = new CreateEventDTO(
                "", null, null, null, "", null, null, null, null, null);

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_validPayload_returnsOk() throws Exception {
        CreateEventDTO dto = new CreateEventDTO(
                "Rock in Rio Updated", 1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(7), "ACTIVE",
                null, null, AgeRating.AGE_18, null, null);

        when(eventsService.updateEvent(eq(1L), any(CreateEventDTO.class))).thenReturn(SAMPLE_EVENT);

        mockMvc.perform(put("/api/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void update_notFound_returns404() throws Exception {
        CreateEventDTO dto = new CreateEventDTO(
                "Test", 1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(7), "ACTIVE", null, null, null, null, null);

        when(eventsService.updateEvent(eq(99L), any(CreateEventDTO.class)))
                .thenThrow(new RuntimeException("Event not found with id: 99"));

        mockMvc.perform(put("/api/events/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }
}
