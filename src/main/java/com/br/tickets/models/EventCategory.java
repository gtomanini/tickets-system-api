package com.br.tickets.models;

import com.br.tickets.models.base.AutoIncrementIdEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "categories_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventCategory extends AutoIncrementIdEntity {

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;
}

