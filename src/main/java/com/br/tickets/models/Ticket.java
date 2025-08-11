package com.br.tickets.models;

import com.br.tickets.models.base.UUIDIdEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Ticket extends UUIDIdEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private Integer amount;

    @Column(precision = 10, scale = 2)
    private BigDecimal netAmount;

    private Integer quantity;

    @Column(length = 1000)
    private String obs;

    @Column(name = "sales_ends_at")
    private LocalDateTime saleEndsAt;


//    @ManyToOne
//    @JoinColumn(name = "variant_of")
//    private Ticket variantOf;
//
//    @OneToMany(mappedBy = "variantOf")
//    private List<Ticket> variants;
//
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
//
//    @ManyToOne
//    @JoinColumn(name = "section_id")
//    private Section section;

}

