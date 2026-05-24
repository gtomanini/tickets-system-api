package com.br.tickets.services;

import com.br.tickets.models.Ticket;
import com.br.tickets.models.TicketVariant;
import com.br.tickets.models.dto.CreateTicketVariantDTO;
import com.br.tickets.models.dto.TicketVariantListDTO;
import com.br.tickets.repositories.TicketVariantRepository;
import com.br.tickets.repositories.TicketsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TicketVariantService {

    private final TicketVariantRepository ticketVariantRepository;
    private final TicketsRepository ticketsRepository;

    public List<TicketVariantListDTO> getVariantsByTicketId(UUID ticketId) {
        return ticketVariantRepository.findByTicketId(ticketId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public TicketVariantListDTO create(UUID ticketId, CreateTicketVariantDTO dto) {
        Ticket ticket = ticketsRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + ticketId));

        validateQuantity(ticket, dto.quantity());

        TicketVariant variant = TicketVariant.builder()
                .name(dto.name())
                .amount(dto.amount())
                .quantity(dto.quantity())
                .obs(dto.obs())
                .saleEndsAt(dto.saleEndsAt())
                .requiresDocument(dto.requiresDocument() != null ? dto.requiresDocument() : false)
                .ticket(ticket)
                .build();

        return toDTO(ticketVariantRepository.save(variant));
    }

    // Validates that the new variant quantity does not exceed the parent ticket capacity,
    // neither individually nor in combination with existing variants.
    private void validateQuantity(Ticket ticket, Integer newQuantity) {
        if (newQuantity > ticket.getTotalCapacity()) {
            throw new IllegalArgumentException(
                "Variant quantity (%d) cannot exceed ticket total capacity (%d)."
                    .formatted(newQuantity, ticket.getTotalCapacity())
            );
        }

        int currentAllocated = ticketVariantRepository.sumQuantityByTicketId(ticket.getId());
        if (currentAllocated + newQuantity > ticket.getTotalCapacity()) {
            int remaining = ticket.getTotalCapacity() - currentAllocated;
            throw new IllegalArgumentException(
                "Combined variant quantities would exceed ticket total capacity. Available: %d."
                    .formatted(remaining)
            );
        }
    }

    private TicketVariantListDTO toDTO(TicketVariant v) {
        return new TicketVariantListDTO(
                v.getId(),
                v.getName(),
                v.getAmount(),
                v.getQuantity(),
                v.getObs(),
                v.getSaleEndsAt(),
                v.getRequiresDocument()
        );
    }
}
