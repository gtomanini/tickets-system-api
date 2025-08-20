package com.br.tickets.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.br.tickets.models.dto.VenueListDTO;
import com.br.tickets.services.VenuesService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;




@RestController
@RequestMapping("/api")
public class VenuesController {

    private final VenuesService venuesService;

    public VenuesController(VenuesService venuesService) {
        this.venuesService = venuesService;
    }
    
    @GetMapping("/venues")
    public List<VenueListDTO> getAllVenues() {
        return venuesService.getAllVenues();
    }

    @GetMapping("/venues/{venueId}")
    public VenueListDTO getVenueBYId(@RequestParam("venueId") Long venueId) {
        return venuesService.getVenueById(venueId);
    }

    @PostMapping("/venues")
    public String createVenue(@RequestBody String entity) {
        // TODO implement logic to create a venue
        return entity;
    }
    
    @PutMapping("path/{id}")
    public String putMethodName(@PathVariable Long id, @RequestBody String entity) {
        // TODO implement logic to update a venue
        return entity;
    }

}
