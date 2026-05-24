package com.br.tickets.controllers;

import com.br.tickets.models.dto.CreateTicketVariantDTO;
import com.br.tickets.models.dto.TicketVariantListDTO;
import com.br.tickets.services.TicketVariantService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class TicketVariantController {

    private final TicketVariantService ticketVariantService;

    @GetMapping("/tickets/{ticketId}/variants")
    public ResponseEntity<List<TicketVariantListDTO>> list(@PathVariable UUID ticketId) {
        return ResponseEntity.ok(ticketVariantService.getVariantsByTicketId(ticketId));
    }

    @PostMapping("/tickets/{ticketId}/variants")
    public ResponseEntity<TicketVariantListDTO> create(
            @PathVariable UUID ticketId,
            @RequestBody @Valid CreateTicketVariantDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ticketVariantService.create(ticketId, dto));
    }
}
