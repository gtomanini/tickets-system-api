package com.br.tickets.config;

import com.br.tickets.models.*;
import com.br.tickets.repositories.EventCategoryRepository;
import com.br.tickets.repositories.EventsRepository;
import com.br.tickets.repositories.TicketVariantRepository;
import com.br.tickets.repositories.TicketsRepository;
import com.br.tickets.repositories.VenuesRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Seeds the database with realistic dev data on startup.
 * Only runs with the "dev" Spring profile (SPRING_PROFILES_ACTIVE=dev).
 * Idempotent: skips seeding if states already exist.
 */
@Slf4j
@Component
@Profile("dev")
@Order(2)
@AllArgsConstructor
public class DevDataSeeder implements ApplicationRunner {

    private final EventCategoryRepository eventCategoryRepository;
    private final VenuesRepository venuesRepository;
    private final EventsRepository eventsRepository;
    private final TicketsRepository ticketsRepository;
    private final TicketVariantRepository ticketVariantRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (eventCategoryRepository.count() > 0) {
            log.info("[DevDataSeeder] Database already seeded — skipping.");
            return;
        }

        log.info("[DevDataSeeder] Seeding dev database...");

        seedEventCategories();
        seedVenuesAndEvents();

        log.info("[DevDataSeeder] Done.");
    }

    private void seedEventCategories() {
        eventCategoryRepository.saveAll(List.of(
                EventCategory.builder().name("Music & Shows").description("Live concerts and music festivals").build(),
                EventCategory.builder().name("Theater & Dance").description("Performing arts events").build(),
                EventCategory.builder().name("Sports").description("Sporting events and championships").build(),
                EventCategory.builder().name("Festivals").description("Multi-day cultural festivals").build(),
                EventCategory.builder().name("Conferences").description("Professional and academic conferences").build()
        ));
        log.info("[DevDataSeeder] Event categories seeded.");
    }

    private void seedVenuesAndEvents() {
        Venue allianzParque = venuesRepository.save(Venue.builder()
                .name("Allianz Parque")
                .description("Multi-purpose arena in São Paulo, capacity 45,000.")
                .address("Av. Francisco Matarazzo, 1705 - Água Branca, São Paulo - SP")
                .longitude(-46.6858)
                .latitude(-23.5273)
                .build());

        Venue maracana = venuesRepository.save(Venue.builder()
                .name("Maracanã")
                .description("Iconic football stadium in Rio de Janeiro, capacity 78,000.")
                .address("Av. Presidente Castelo Branco - Maracanã, Rio de Janeiro - RJ")
                .longitude(-43.2302)
                .latitude(-22.9122)
                .build());

        Venue mineirão = venuesRepository.save(Venue.builder()
                .name("Mineirão")
                .description("Major stadium in Belo Horizonte, capacity 61,000.")
                .address("Av. Antônio Abrahão Caram, 1001 - Pampulha, Belo Horizonte - MG")
                .longitude(-43.9705)
                .latitude(-19.8658)
                .build());

        log.info("[DevDataSeeder] Venues seeded.");

        // ── Events ───────────────────────────────────────────────────────────

        Event rockInRio = eventsRepository.save(Event.builder()
                .name("Rock in Rio 2025")
                .description("The biggest music festival in the world returns to Rio de Janeiro.")
                .status("ACTIVE")
                .featured(true)
                .closed(false)
                .startDate(LocalDateTime.now().plusMonths(2))
                .endDate(LocalDateTime.now().plusMonths(2).plusDays(7))
                .venue(maracana)
                .build());

        Event lolla = eventsRepository.save(Event.builder()
                .name("Lollapalooza Brasil 2025")
                .description("Three days of rock, pop, hip-hop and electronic music in São Paulo.")
                .status("ACTIVE")
                .featured(true)
                .closed(false)
                .startDate(LocalDateTime.now().plusMonths(1))
                .endDate(LocalDateTime.now().plusMonths(1).plusDays(3))
                .venue(allianzParque)
                .build());

        Event classicoMineiro = eventsRepository.save(Event.builder()
                .name("Clássico Mineiro — Atlético x Cruzeiro")
                .description("The biggest rivalry in Minas Gerais football.")
                .status("ACTIVE")
                .featured(false)
                .closed(false)
                .startDate(LocalDateTime.now().plusWeeks(3))
                .endDate(LocalDateTime.now().plusWeeks(3).plusHours(3))
                .venue(mineirão)
                .build());

        Event pastEvent = eventsRepository.save(Event.builder()
                .name("Festival de Inverno 2024")
                .description("A past event kept for historical testing purposes.")
                .status("INACTIVE")
                .featured(false)
                .closed(true)
                .startDate(LocalDateTime.now().minusMonths(3))
                .endDate(LocalDateTime.now().minusMonths(3).plusDays(2))
                .venue(mineirão)
                .build());

        log.info("[DevDataSeeder] Events seeded.");

        // ── Tickets & Variants ───────────────────────────────────────────────

        seedTicketsForEvent(rockInRio, List.of(
                new TicketSeed("Área VIP", "Exclusive area with open bar", 150, 80.0),
                new TicketSeed("Pista Premium", "Floor access close to stage", 800, 200.0),
                new TicketSeed("Arquibancada", "General admission stands", 2000, 100.0)
        ));

        seedTicketsForEvent(lolla, List.of(
                new TicketSeed("Camarote", "Private camarote with premium service", 200, 150.0),
                new TicketSeed("Pista", "General floor access", 3000, 180.0)
        ));

        seedTicketsForEvent(classicoMineiro, List.of(
                new TicketSeed("Cadeira Superior", "Upper tier seats", 5000, 80.0),
                new TicketSeed("Cadeira Inferior", "Lower tier seats", 3000, 120.0)
        ));

        log.info("[DevDataSeeder] Tickets and variants seeded.");
    }

    private void seedTicketsForEvent(Event event, List<TicketSeed> seeds) {
        for (TicketSeed seed : seeds) {
            Ticket ticket = ticketsRepository.save(Ticket.builder()
                    .name(seed.name())
                    .obs(seed.obs())
                    .totalCapacity(seed.capacity())
                    .event(event)
                    .build());

            int halfCapacity = seed.capacity() / 2;

            ticketVariantRepository.saveAll(List.of(
                    TicketVariant.builder()
                            .name("Full Price")
                            .amount(BigDecimal.valueOf(seed.price()))
                            .quantity(seed.capacity())
                            .requiresDocument(false)
                            .saleEndsAt(event.getEndDate())
                            .ticket(ticket)
                            .build(),
                    TicketVariant.builder()
                            .name("Half Price — Student")
                            .amount(BigDecimal.valueOf(seed.price() / 2))
                            .quantity(halfCapacity)
                            .requiresDocument(true)
                            .obs("Requires valid student ID")
                            .saleEndsAt(event.getEndDate())
                            .ticket(ticket)
                            .build(),
                    TicketVariant.builder()
                            .name("Half Price — Senior (60+)")
                            .amount(BigDecimal.valueOf(seed.price() / 2))
                            .quantity(halfCapacity)
                            .requiresDocument(true)
                            .obs("Requires ID proving age 60 or over")
                            .saleEndsAt(event.getEndDate())
                            .ticket(ticket)
                            .build()
            ));
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private record TicketSeed(String name, String obs, int capacity, double price) {}
}
