package com.br.tickets.models;

import com.br.tickets.models.base.UUIDIdEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Service extends UUIDIdEntity {

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "net_amount", precision = 10, scale = 2)
    private BigDecimal netAmount;

//    @ManyToOne
//    @JoinColumn(name = "category_id", referencedColumnName = "id")
//    private ServiceCategoryEntity category;
}

