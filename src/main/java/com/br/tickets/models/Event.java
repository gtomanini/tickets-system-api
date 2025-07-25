package com.br.tickets.models;

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


    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
//    @OrderBy("variantOf ASC, amount ASC")
    private List<Ticket> tickets;

//    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
//    @OrderBy("amount ASC")
//    private List<Ticket> ticketsWithoutVariants;

//    @ManyToMany
//    @JoinTable(
//            name = "event_chair",
//            joinColumns = @JoinColumn(name = "event_id"),
//            inverseJoinColumns = @JoinColumn(name = "chair_id")
//    )
//    private List<Seat> chairs;

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
//    @OneToOne
//    @JoinColumn(name = "age_rating_id", referencedColumnName = "id")
//    private AgeRatingEntity ageRating;
//
//    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
//    private List<EventTransferEntity> transfers;
//
//    @Transient
//    public BigDecimal getProgress() {
//        /*
//        BigDecimal sum = orders.stream()
//            .flatMap(order -> order.getRewards().stream())
//            .map(Reward::getAmount)
//            .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        BigDecimal goal = rewards.stream()
//            .map(reward -> reward.getAmount().multiply(BigDecimal.valueOf(reward.getQuantity())))
//            .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        try {
//            return sum.multiply(BigDecimal.valueOf(100)).divide(goal, 2, RoundingMode.HALF_UP);
//        } catch (Exception e) {
//            return BigDecimal.ZERO;
//        }
//        */
//        return BigDecimal.ZERO;
//    }
}

