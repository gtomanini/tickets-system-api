package com.br.tickets.repositories;

import com.br.tickets.models.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {

    List<Section> findByVenueIdOrderByNameAsc(Long venueId);
}
