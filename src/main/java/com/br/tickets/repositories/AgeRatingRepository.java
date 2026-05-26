package com.br.tickets.repositories;

import com.br.tickets.models.AgeRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgeRatingRepository extends JpaRepository<AgeRating, Long> {
}
