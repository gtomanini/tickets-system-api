package com.br.tickets.controllers;

import com.br.tickets.models.dto.CityListDTO;
import com.br.tickets.models.dto.CitySearchCriteria;
import com.br.tickets.models.dto.StateListDTO;
import com.br.tickets.services.GeoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Geography", description = "States and cities for Brazil")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class GeoController {

    private final GeoService geoService;

    @GetMapping("/states")
    public ResponseEntity<List<StateListDTO>> listStates() {
        return ResponseEntity.ok(geoService.listStates());
    }

    @GetMapping("/cities")
    public ResponseEntity<Page<CityListDTO>> listCities(
            @ModelAttribute CitySearchCriteria criteria,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(geoService.searchCities(criteria, pageable));
    }
}
