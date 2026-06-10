package com.br.tickets.models;

import com.br.tickets.models.base.AutoIncrementIdEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "countries")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Country extends AutoIncrementIdEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 2, unique = true)
    private String code; // ISO 3166-1 alpha-2 (e.g. "BR")
}
