package com.br.tickets.controllers;

import com.br.tickets.controllers.advice.GlobalExceptionHandler;
import com.br.tickets.models.dto.CreateSectionDTO;
import com.br.tickets.models.dto.SectionListDTO;
import com.br.tickets.services.SectionService;
import com.br.tickets.services.VenuesService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SectionControllerTest {

    private MockMvc mockMvc;

    @Mock private SectionService sectionService;
    @Mock private VenuesService venuesService;

    @InjectMocks
    private VenuesController venuesController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(venuesController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void listSections_existingVenue_returnsOk() throws Exception {
        SectionListDTO s1 = new SectionListDTO(1L, "Pista", 5000, false);
        SectionListDTO s2 = new SectionListDTO(2L, "Camarote", 500, true);

        when(sectionService.listByVenue(1L)).thenReturn(List.of(s1, s2));

        mockMvc.perform(get("/api/venues/1/sections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Pista"))
                .andExpect(jsonPath("$[1].name").value("Camarote"));
    }

    @Test
    void listSections_venueNotFound_returns404() throws Exception {
        when(sectionService.listByVenue(99L))
                .thenThrow(new RuntimeException("Venue not found with id: 99"));

        mockMvc.perform(get("/api/venues/99/sections"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createSection_validPayload_returnsCreated() throws Exception {
        CreateSectionDTO dto = new CreateSectionDTO("Pista", 5000, false);
        SectionListDTO response = new SectionListDTO(1L, "Pista", 5000, false);

        when(sectionService.create(eq(1L), any(CreateSectionDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/venues/1/sections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Pista"))
                .andExpect(jsonPath("$.capacity").value(5000));
    }

    @Test
    void createSection_missingName_returnsBadRequest() throws Exception {
        CreateSectionDTO dto = new CreateSectionDTO("", 5000, false);

        mockMvc.perform(post("/api/venues/1/sections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateSection_validPayload_returnsOk() throws Exception {
        CreateSectionDTO dto = new CreateSectionDTO("Pista Premium", 3000, false);
        SectionListDTO response = new SectionListDTO(1L, "Pista Premium", 3000, false);

        when(sectionService.update(eq(1L), eq(1L), any(CreateSectionDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/venues/1/sections/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pista Premium"))
                .andExpect(jsonPath("$.capacity").value(3000));
    }
}
