package com.br.tickets.models;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class Organizer extends User {

    @OneToMany
    private List<Event> events;
}
