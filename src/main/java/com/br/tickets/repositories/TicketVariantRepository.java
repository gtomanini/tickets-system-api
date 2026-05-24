package com.br.tickets.repositories;

import com.br.tickets.models.TicketVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketVariantRepository extends JpaRepository<TicketVariant, UUID> {

    List<TicketVariant> findByTicketId(UUID ticketId);

    @Query("SELECT COALESCE(SUM(v.quantity), 0) FROM TicketVariant v WHERE v.ticket.id = :ticketId")
    Integer sumQuantityByTicketId(@Param("ticketId") UUID ticketId);
}
