package com.br.tickets.models;

import com.br.tickets.models.base.AutoIncrementIdEntity;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "sections")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Section extends AutoIncrementIdEntity {

    @Column(nullable = false)
    private String name;

    private Integer capacity;

    private Boolean isNumbered;

    @Column(name = "place_id", insertable = false, updatable = false)
    private Long placeId;

    @ManyToOne
    @JoinColumn(name = "place_id", referencedColumnName = "id")
    private Venue venue;

//    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL)
//    private List<Seat> seats;

//    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL)
//    private List<Ticket> tickets;
}
