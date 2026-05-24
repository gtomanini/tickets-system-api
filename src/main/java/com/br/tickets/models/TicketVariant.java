package com.br.tickets.models;

import com.br.tickets.models.base.UUIDIdEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_variants")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TicketVariant extends UUIDIdEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(precision = 10, scale = 2)
    private BigDecimal netAmount;

    @Column(nullable = false)
    private Integer quantity;

    @Column(length = 1000)
    private String obs;

    @Column(name = "sale_ends_at")
    private LocalDateTime saleEndsAt;

    @Column(nullable = false)
    private Boolean requiresDocument = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;
}
