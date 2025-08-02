package com.br.tickets.models;

import com.br.tickets.models.base.AutoIncrementIdEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.Fetch;

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

    @Column(name = "tag_id", insertable = false, updatable = false)
    private Long tagId;

    @Column(name = "age_rating_id", insertable = false, updatable = false)
    private Long ageRatingId;

    @ManyToMany
    @JoinTable(
            name = "event_category",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "event_category_id")
    )
    private List<EventCategory> categories;

    @ManyToOne
    @JoinColumn(name = "venue_id")
    private Venue venue;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private Organizer organizer;


    @OneToMany(mappedBy = "event", fetch = FetchType.EAGER)
    private List<Ticket> tickets;

//    @ManyToMany
//    @JoinTable(
//            name = "event_seats",
//            joinColumns = @JoinColumn(name = "event_id"),
//            inverseJoinColumns = @JoinColumn(name = "seat_id")
//    )
//    private List<Seat> seats;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Order> orders;
//
//    @OneToOne
//    @JoinColumn(name = "tag_id", referencedColumnName = "id")
//    private EventTagEntity tag;
//
//    @ManyToMany
//    @JoinTable(
//            name = "event_service",
//            joinColumns = @JoinColumn(name = "event_id"),
//            inverseJoinColumns = @JoinColumn(name = "service_id")
//    )
//    private List<Service> services;
//
//    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
//    private List<DiscountEntity> discounts;
//
   @OneToOne
   @JoinColumn(name = "age_rating_id", referencedColumnName = "id")
   private AgeRating ageRating;
//
//    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
//    private List<EventTransferEntity> transfers;
//

}

