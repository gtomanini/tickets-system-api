package com.br.tickets.repositories;

import com.br.tickets.models.Event;
import com.br.tickets.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface  UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<Event> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailIgnoreCase(String email);
}
