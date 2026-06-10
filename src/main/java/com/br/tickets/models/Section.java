package com.br.tickets.models;

import com.br.tickets.models.base.AutoIncrementIdEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sections")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Section extends AutoIncrementIdEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Boolean numbered;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

//    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL)
//    private List<Seat> seats;

//    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL)
//    private List<Ticket> tickets;
}
