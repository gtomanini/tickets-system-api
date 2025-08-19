package com.br.tickets.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;

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
import com.br.tickets.models.Venue;
import com.br.tickets.models.dto.VenueListDTO;
import com.br.tickets.repositories.VenuesRepository;
import com.br.tickets.services.VenuesService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
public class VenuesControllerTest {

    private MockMvc mockMvc;

    @Mock
    private VenuesService venuesService;

    @Mock
    private VenuesRepository venuesRepository;

    @InjectMocks
    private VenuesController venuesController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(venuesController).build();
    }

    @Test
    void list_allVenues() throws Exception {

        VenueListDTO venueListDTO1 = new VenueListDTO(1L, "Venue One", "First Venue", "123 Main St", -46.625290, -23.533773, "http://example.com/venue1", "http://example.com/photo1.jpg", null);
        VenueListDTO venueListDTO2 = new VenueListDTO(2L, "Venue Two", "Second Venue", "456 Elm St", -46.625290, -23.533773, "http://example.com/venue2", "http://example.com/photo2.jpg", null);

        when(venuesService.getAllVenues()).thenReturn(List.of(
            venueListDTO1, venueListDTO2
        ));       

        mockMvc.perform(get("/api/venues")
            .contentType(MediaType.APPLICATION_JSON) 
            .content(this.objectMapper.writeValueAsString(Venue.class)))
            .andExpect(status().isOk());

    }
   
}
