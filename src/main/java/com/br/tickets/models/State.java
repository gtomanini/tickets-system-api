package com.br.tickets.models;

import com.br.tickets.models.base.AutoIncrementIdEntity;
import jakarta.persistence.*;

import lombok.*;

import java.util.List;

@Entity
@Table(name = "states")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class State extends AutoIncrementIdEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 2)
    private String abbr;

    @OneToMany(mappedBy = "state")
    private List<City> cities;
}

