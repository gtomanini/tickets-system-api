package com.br.tickets.services;

import com.br.tickets.models.Event;
import com.br.tickets.models.Section;
import com.br.tickets.models.Ticket;
import com.br.tickets.models.dto.CreateTicketDTO;
import com.br.tickets.models.dto.TicketListDTO;
import com.br.tickets.models.dto.TicketVariantListDTO;
import com.br.tickets.repositories.EventsRepository;
import com.br.tickets.repositories.SectionRepository;
import com.br.tickets.repositories.TicketsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TicketsService {

    private final TicketsRepository ticketsRepository;
    private final EventsRepository eventsRepository;
    private final SectionRepository sectionRepository;

    public List<TicketListDTO> getTicketsByEventId(Long eventId) {
        return ticketsRepository.findByEventId(eventId).stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public TicketListDTO createTicket(Long eventId, CreateTicketDTO dto) {
        Event event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));

        Section section = sectionRepository.findById(dto.sectionId())
                .orElseThrow(() -> new RuntimeException("Section not found with id: " + dto.sectionId()));

        Ticket ticket = Ticket.builder()
                .name(dto.name())
                .obs(dto.obs())
                .totalCapacity(dto.totalCapacity())
                .event(event)
                .section(section)
                .build();

        return toDTO(ticketsRepository.save(ticket));
    }

    @Transactional
    public TicketListDTO updateTicket(Long eventId, UUID ticketId, CreateTicketDTO dto) {
        Event event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));

        Ticket ticket = ticketsRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + ticketId));

        Section section = sectionRepository.findById(dto.sectionId())
                .orElseThrow(() -> new RuntimeException("Section not found with id: " + dto.sectionId()));

        ticket.setName(dto.name());
        ticket.setObs(dto.obs());
        ticket.setTotalCapacity(dto.totalCapacity());
        ticket.setSection(section);

        return toDTO(ticketsRepository.save(ticket));
    }

    @Transactional
    public void deleteTicket(Long eventId, UUID ticketId) {
        Event event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));

        Ticket ticket = ticketsRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + ticketId));

        ticketsRepository.delete(ticket);
    }

    private TicketListDTO toDTO(Ticket ticket) {
        Long sectionId = ticket.getSection() != null ? ticket.getSection().getId() : null;
        String sectionName = ticket.getSection() != null ? ticket.getSection().getName() : null;

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
                sectionId,
                sectionName,
                variants
        );
    }
}
