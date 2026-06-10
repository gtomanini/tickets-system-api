package com.br.tickets.controllers;

import com.br.tickets.models.dto.CreateVenueDTO;
import com.br.tickets.models.dto.VenueListDTO;
import com.br.tickets.services.VenuesService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Venues")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class VenuesController {

    private final VenuesService venuesService;

    @GetMapping("/venues")
    public ResponseEntity<List<VenueListDTO>> list() {
        return ResponseEntity.ok(venuesService.getAllVenues());
    }

    @GetMapping("/venues/{id}")
    public ResponseEntity<VenueListDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(venuesService.getVenueById(id));
    }

    @PostMapping("/venues")
    public ResponseEntity<VenueListDTO> create(@RequestBody @Valid CreateVenueDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(venuesService.createVenue(dto));
    }
}
