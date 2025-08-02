package com.br.tickets.controllers;

import com.br.tickets.config.TestSecurityConfig;
import com.br.tickets.models.Event;
import com.br.tickets.models.dto.CreateEventDTO;
import com.br.tickets.models.dto.EventListDTO;
import com.br.tickets.models.dto.EventSearchCriteria;
import com.br.tickets.repositories.EventsRepository;
import com.br.tickets.services.EventsService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
public class EventsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EventsService eventsService;

    @Mock
    private EventsRepository eventsRepository;

    @InjectMocks
    private EventsController eventsController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventsController).build();
    }

    @Test
    void list_noParams_returnsPageOfEvents() throws Exception {

        Pageable pageable = PageRequest.of(0, 10);

        EventListDTO eventListDTO = new EventListDTO(1L, "Test Event", "ACTIVE", "Test Description", null, null, false, false);   
        List<EventListDTO> content = Collections.singletonList(eventListDTO);

        Page<EventListDTO> expectedPage = new PageImpl<>(content, pageable, content.size());
        
        when(eventsService.searchEvents(any(EventSearchCriteria.class), any(Pageable.class)))
                .thenReturn(expectedPage);

        mockMvc.perform(get("/api/events"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].name").value("Test Event"))
            .andExpect(jsonPath("$.pageable.pageSize").value(10)) // Exemplo de outra verificação
            .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void list_withNameParam_returnsFilteredPageOfEvents() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        EventListDTO eventListDTO = new EventListDTO(1L, "Filtered Event", "ACTIVE", "Desc", null, null, false, false);
        List<EventListDTO> content = Collections.singletonList(eventListDTO);
        Page<EventListDTO> expectedPage = new PageImpl<>(content, pageable, content.size());

        when(eventsService.searchEvents(any(EventSearchCriteria.class), any(Pageable.class)))
            .thenReturn(expectedPage);

        mockMvc.perform(get("/api/events")
            .param("name", "Filtered Event")) // Adiciona o parâmetro à URL
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].name").value("Filtered Event"));
    }


    @Test
    void create_validEvent_returnsCreatedEvent() throws Exception {
        CreateEventDTO createDto = new CreateEventDTO("New Event", "Description of new event", null, null, null, null); // Supondo que seu DTO de criação tenha setters

        Event createdEvent = new Event();
        createdEvent.setId(1L);
        createdEvent.setName("New Event");
        createdEvent.setDescription("Description of new event");
        createdEvent.setStatus("ACTIVE"); 

        when(eventsService.saveEvent(any(CreateEventDTO.class)))
            .thenReturn(createdEvent);

        mockMvc.perform(post("/api/events") 
            .contentType(MediaType.APPLICATION_JSON) 
            .content(this.objectMapper.writeValueAsString(createDto))) 
            .andExpect(status().isOk()) 
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("New Event"))
            .andExpect(jsonPath("$.description").value("Description of new event"));
    }
}
