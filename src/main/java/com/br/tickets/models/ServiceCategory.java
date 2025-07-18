package com.br.tickets.models;

import com.br.tickets.models.base.UUIDIdEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "service_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceCategory extends UUIDIdEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "pdf_name")
    private String pdfName;

//    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
//    private List<Service> services;
}

