package com.br.tickets.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.br.tickets.models.Venue;

@Repository
public interface VenuesRepository extends JpaRepository<Venue, Long>, JpaSpecificationExecutor<Venue> {
   
}
