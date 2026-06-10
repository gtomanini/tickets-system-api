package com.br.tickets.services;

import com.br.tickets.models.City;
import com.br.tickets.models.dto.CityListDTO;
import com.br.tickets.models.dto.CitySearchCriteria;
import com.br.tickets.models.dto.StateListDTO;
import com.br.tickets.repositories.CityRepository;
import com.br.tickets.repositories.StateRepository;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class GeoService {

    private final StateRepository stateRepository;
    private final CityRepository cityRepository;

    public List<StateListDTO> listStates() {
        return stateRepository.findAllByOrderByNameAsc().stream()
                .map(s -> new StateListDTO(s.getId(), s.getName(), s.getAbbr()))
                .toList();
    }

    public Page<CityListDTO> searchCities(CitySearchCriteria criteria, Pageable pageable) {
        Specification<City> spec = (root, query, cb) -> {
            // JOIN FETCH state to avoid N+1 on the data query; skip for count query
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("state", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            if (criteria.stateId() != null) {
                predicates.add(cb.equal(root.get("state").get("id"), criteria.stateId()));
            }
            if (criteria.name() != null && !criteria.name().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")),
                        "%" + criteria.name().toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return cityRepository.findAll(spec, pageable).map(this::toDTO);
    }

    private CityListDTO toDTO(City city) {
        return new CityListDTO(
                city.getId(),
                city.getName(),
                city.getIbgeCode(),
                city.getState().getId(),
                city.getState().getName(),
                city.getState().getAbbr()
        );
    }
}
