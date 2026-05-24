package com.br.tickets.services;

import com.br.tickets.models.Ticket;
import com.br.tickets.models.dto.TicketListDTO;
import com.br.tickets.models.dto.TicketVariantListDTO;
import com.br.tickets.repositories.TicketsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TicketsService {

    private final TicketsRepository ticketsRepository;

    public List<TicketListDTO> getTicketsByEventId(Long eventId) {
        return ticketsRepository.findByEventId(eventId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private TicketListDTO toDTO(Ticket ticket) {
        List<TicketVariantListDTO> variants = ticket.getVariants() == null ? List.of() :
                ticket.getVariants().stream()
                        .map(v -> new TicketVariantListDTO(
                                v.getId(),
                                v.getName(),
                                v.getAmount(),
                                v.getQuantity(),
                                v.getObs(),
                                v.getSaleEndsAt(),
                                v.getRequiresDocument()
                        ))
                        .toList();

        return new TicketListDTO(
                ticket.getId(),
                ticket.getName(),
                ticket.getObs(),
                ticket.getTotalCapacity(),
                variants
        );
    }
}
