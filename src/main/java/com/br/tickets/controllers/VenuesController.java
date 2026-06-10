package com.br.tickets.controllers;

import com.br.tickets.models.dto.CreateSectionDTO;
import com.br.tickets.models.dto.CreateVenueDTO;
import com.br.tickets.models.dto.SectionListDTO;
import com.br.tickets.models.dto.VenueListDTO;
import com.br.tickets.services.SectionService;
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
    private final SectionService sectionService;

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

    @PutMapping("/venues/{id}")
    public ResponseEntity<VenueListDTO> update(@PathVariable Long id, @RequestBody @Valid CreateVenueDTO dto) {
        return ResponseEntity.ok(venuesService.updateVenue(id, dto));
    }

    // ── Sections ──────────────────────────────────────────────────────────────

    @GetMapping("/venues/{venueId}/sections")
    public ResponseEntity<List<SectionListDTO>> listSections(@PathVariable Long venueId) {
        return ResponseEntity.ok(sectionService.listByVenue(venueId));
    }

    @PostMapping("/venues/{venueId}/sections")
    public ResponseEntity<SectionListDTO> createSection(
            @PathVariable Long venueId,
            @RequestBody @Valid CreateSectionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sectionService.create(venueId, dto));
    }

    @PutMapping("/venues/{venueId}/sections/{sectionId}")
    public ResponseEntity<SectionListDTO> updateSection(
            @PathVariable Long venueId,
            @PathVariable Long sectionId,
            @RequestBody @Valid CreateSectionDTO dto) {
        return ResponseEntity.ok(sectionService.update(venueId, sectionId, dto));
    }
}
