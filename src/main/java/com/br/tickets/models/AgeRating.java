package com.br.tickets.models;

import com.br.tickets.models.base.AutoIncrementIdEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "age_ratings")
@Getter
@Setter
public class AgeRating extends AutoIncrementIdEntity {

    private String name;
    private String description;
    
}
