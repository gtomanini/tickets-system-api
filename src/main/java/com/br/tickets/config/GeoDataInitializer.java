package com.br.tickets.config;

import com.br.tickets.models.City;
import com.br.tickets.models.Country;
import com.br.tickets.models.State;
import com.br.tickets.repositories.CityRepository;
import com.br.tickets.repositories.CountryRepository;
import com.br.tickets.repositories.StateRepository;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * Seeds countries, states and cities from the IBGE public API on first startup.
 * Runs before DevDataSeeder (@Order(1)) on all profiles.
 * Skipped when app.geo.auto-seed=false (e.g. in tests).
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor  // only injects final fields via constructor; @Value handles the boolean
public class GeoDataInitializer implements ApplicationRunner {

    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;
    private final RestTemplate restTemplate;

    @Value("${app.geo.auto-seed:true}")
    private boolean autoSeed;

    private static final String IBGE_STATES_URL =
            "https://servicodados.ibge.gov.br/api/v1/localidades/estados?orderBy=nome";
    private static final String IBGE_CITIES_URL =
            "https://servicodados.ibge.gov.br/api/v1/localidades/estados/{uf}/municipios?orderBy=nome";

    @Override
    public void run(ApplicationArguments args) {
        if (!autoSeed || countryRepository.count() > 0) {
            log.info("[GeoDataInitializer] Geographic data already present — skipping.");
            return;
        }

        log.info("[GeoDataInitializer] Seeding geographic data from IBGE API...");

        try {
            Country brazil = countryRepository.save(
                    Country.builder().name("Brasil").code("BR").build()
            );

            IbgeStateResponse[] ibgeStates = restTemplate.getForObject(IBGE_STATES_URL, IbgeStateResponse[].class);

            if (ibgeStates == null) {
                log.error("[GeoDataInitializer] IBGE states API returned null — aborting geo seed.");
                return;
            }

            for (IbgeStateResponse ibgeState : ibgeStates) {
                State state = stateRepository.save(State.builder()
                        .name(ibgeState.nome())
                        .abbr(ibgeState.sigla())
                        .country(brazil)
                        .build());

                IbgeCityResponse[] ibgeCities = restTemplate.getForObject(
                        IBGE_CITIES_URL, IbgeCityResponse[].class, ibgeState.sigla());

                if (ibgeCities == null) continue;

                List<City> cities = Arrays.stream(ibgeCities)
                        .map(c -> City.builder()
                                .name(c.nome())
                                .ibgeCode(String.valueOf(c.id()))
                                .state(state)
                                .build())
                        .toList();

                cityRepository.saveAll(cities);
                log.info("[GeoDataInitializer] {} — {} cities", state.getAbbr(), cities.size());
            }

            log.info("[GeoDataInitializer] Done. {} states seeded.", ibgeStates.length);

        } catch (Exception e) {
            log.error("[GeoDataInitializer] Failed to seed geographic data: {}", e.getMessage(), e);
        }
    }

    // ── IBGE API response records ─────────────────────────────────────────────

    @JsonIgnoreProperties(ignoreUnknown = true)
    record IbgeStateResponse(Long id, String nome, String sigla) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record IbgeCityResponse(Long id, String nome) {}
}
