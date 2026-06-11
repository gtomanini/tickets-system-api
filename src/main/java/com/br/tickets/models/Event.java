package com.br.tickets.models;

import com.br.tickets.enums.AgeRating;
import com.br.tickets.models.base.AutoIncrementIdEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "events")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Event extends AutoIncrementIdEntity {

    private String name;
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private String status;
    private Boolean featured;
    private Boolean closed;

    @Enumerated(EnumType.STRING)
    private AgeRating ageRating;

    @ElementCollection
    @CollectionTable(name = "event_tags", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "tag")
    private List<String> tags;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "event_category",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "event_category_id")
    )
    private List<EventCategory> categories;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id")
    private Venue venue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id")
    private Organizer organizer;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    private List<Ticket> tickets;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Order> orders;
}
