package com.br.tickets.controllers;

import com.br.tickets.controllers.advice.GlobalExceptionHandler;
import com.br.tickets.models.dto.CreateSectionDTO;
import com.br.tickets.models.dto.CreateVenueDTO;
import com.br.tickets.models.dto.SectionListDTO;
import com.br.tickets.models.dto.VenueListDTO;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class VenuesControllerTest {

    private MockMvc mockMvc;

    @Mock private VenuesService venuesService;
    @Mock private SectionService sectionService;

    @InjectMocks
    private VenuesController venuesController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final List<CreateSectionDTO> ONE_SECTION =
            List.of(new CreateSectionDTO("Pista", 5000, false));

    private static final List<SectionListDTO> ONE_SECTION_DTO =
            List.of(new SectionListDTO(1L, "Pista", 5000, false));

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
    void list_allVenues_returnsOk() throws Exception {
        VenueListDTO v1 = new VenueListDTO(1L, "Allianz Parque", "Arena SP", "Av. Francisco Matarazzo", -46.68, -23.52, null, null, 1L, "São Paulo", 35L, ONE_SECTION_DTO);
        VenueListDTO v2 = new VenueListDTO(2L, "Maracanã", "Estádio RJ", "Av. Presidente Castelo Branco", -43.23, -22.91, null, null, 2L, "Rio de Janeiro", 19L, List.of());

        when(venuesService.getAllVenues()).thenReturn(List.of(v1, v2));

        mockMvc.perform(get("/api/venues"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Allianz Parque"))
                .andExpect(jsonPath("$[0].sections[0].name").value("Pista"))
                .andExpect(jsonPath("$[1].name").value("Maracanã"));
    }

    @Test
    void create_validPayload_returnsCreated() throws Exception {
        CreateVenueDTO dto = new CreateVenueDTO("Allianz Parque", 1L, ONE_SECTION, "Arena SP", "Av. Francisco Matarazzo", -46.68, -23.52, null, null);
        VenueListDTO response = new VenueListDTO(1L, "Allianz Parque", "Arena SP", "Av. Francisco Matarazzo", -46.68, -23.52, null, null, 1L, "São Paulo", 35L, ONE_SECTION_DTO);

        when(venuesService.createVenue(any(CreateVenueDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Allianz Parque"))
                .andExpect(jsonPath("$.sections[0].name").value("Pista"));
    }

    @Test
    void create_missingSections_returnsBadRequest() throws Exception {
        CreateVenueDTO dto = new CreateVenueDTO("Allianz Parque", 1L, List.of(), null, null, null, null, null, null);

        mockMvc.perform(post("/api/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_missingName_returnsBadRequest() throws Exception {
        CreateVenueDTO dto = new CreateVenueDTO("", 1L, ONE_SECTION, null, null, null, null, null, null);

        mockMvc.perform(post("/api/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_missingCityId_returnsBadRequest() throws Exception {
        CreateVenueDTO dto = new CreateVenueDTO("Allianz Parque", null, ONE_SECTION, null, null, null, null, null, null);

        mockMvc.perform(post("/api/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_validPayload_returnsOk() throws Exception {
        CreateVenueDTO dto = new CreateVenueDTO("Allianz Parque Updated", 1L, ONE_SECTION, "Updated desc", null, null, null, null, null);
        VenueListDTO response = new VenueListDTO(1L, "Allianz Parque Updated", "Updated desc", null, null, null, null, null, 1L, "São Paulo", 35L, ONE_SECTION_DTO);

        when(venuesService.updateVenue(eq(1L), any(CreateVenueDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/venues/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Allianz Parque Updated"));
    }

    @Test
    void update_notFound_returns404() throws Exception {
        CreateVenueDTO dto = new CreateVenueDTO("Allianz Parque", 1L, ONE_SECTION, null, null, null, null, null, null);

        when(venuesService.updateVenue(eq(99L), any(CreateVenueDTO.class)))
                .thenThrow(new RuntimeException("Venue not found with id: 99"));

        mockMvc.perform(put("/api/venues/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }
}
