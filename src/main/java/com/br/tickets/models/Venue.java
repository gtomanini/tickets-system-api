package com.br.tickets.models;

import com.br.tickets.models.base.AutoIncrementIdEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "venues")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Venue extends AutoIncrementIdEntity {

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String address;

    private Double longitude;

    private Double latitude;

    @Column(columnDefinition = "TEXT")
    private String plant_url;

    @Column(columnDefinition = "TEXT")
    private String photos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Section> sections;
}

