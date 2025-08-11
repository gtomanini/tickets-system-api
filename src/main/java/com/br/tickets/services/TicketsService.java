package com.br.tickets.services;

import java.util.List;
import com.br.tickets.models.dto.TicketListDTO;

import org.springframework.stereotype.Service;

import com.br.tickets.models.Ticket;
import com.br.tickets.repositories.TicketsRepository;

import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class TicketsService {

    private final TicketsRepository ticketsRepository;
    

    public List<TicketListDTO> getTicketsByEventId(Long eventId) {
        List<Ticket> tickets = ticketsRepository.findByEventId(eventId);

        return tickets.stream().map(ticket -> new TicketListDTO(
            ticket.getId(), 
            ticket.getName(),
            ticket.getAmount(),
            ticket.getObs()
        )).toList();
    }

}
