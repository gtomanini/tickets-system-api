package com.br.tickets.models;

import com.br.tickets.models.base.UUIDIdEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_ticket") // mantendo nome da tabela original
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderTicket extends UUIDIdEntity {

    @Column(name = "user_name")
    private String userName;

    @Column(name = "user_email")
    private String userEmail;

    @Column(precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "net_amount", precision = 10, scale = 2)
    private BigDecimal netAmount;

    private Boolean verified;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @ManyToOne
    @JoinColumn(name = "ticket_id", referencedColumnName = "id")
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "seat_id", referencedColumnName = "id")
    private Seat seat;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;
}
