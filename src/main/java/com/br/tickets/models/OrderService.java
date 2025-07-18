package com.br.tickets.models;

import com.br.tickets.models.base.UUIDIdEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderService extends UUIDIdEntity {

    @Column(name = "user_name")
    private String userName;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "net_amount", precision = 10, scale = 2)
    private BigDecimal netAmount;

    @Column(name = "verified")
    private Boolean verified;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

//    @Convert(converter = JsonMapConverter.class)
    @Column(columnDefinition = "TEXT")
    private String extra;

    @ManyToOne
    @JoinColumn(name = "service_id", referencedColumnName = "id")
    private Service service;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;
}

