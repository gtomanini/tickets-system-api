package com.br.tickets.models;

import com.br.tickets.models.base.AutoIncrementIdEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "states")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class State extends AutoIncrementIdEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 2)
    private String abbr;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;
}
