package com.br.tickets.repositories;

import com.br.tickets.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EventsRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
}
