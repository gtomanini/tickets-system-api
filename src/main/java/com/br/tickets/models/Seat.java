package com.br.tickets.models;

import com.br.tickets.models.base.UUIDIdEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seats")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat extends UUIDIdEntity {

    @Column(nullable = false)
    private String name;

    private Integer xOnMap;

    private Integer yOnMap;

    private String colorOnMap;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "section_id", insertable = false, updatable = false)
    private Long sectionId;

    @ManyToOne
    @JoinColumn(name = "section_id", referencedColumnName = "id")
    private Section section;
}

