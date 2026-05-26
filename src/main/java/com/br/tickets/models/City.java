package com.br.tickets.models;

import com.br.tickets.models.base.AutoIncrementIdEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cities")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class City extends AutoIncrementIdEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String code;

    @Column(name = "state_abbr")
    private String stateAbbr;

    @Column(name = "state_id", insertable = false, updatable = false)
    private Long stateId;

    @ManyToOne
    @JoinColumn(name = "state_id", referencedColumnName = "id")
    private State state;
}
