package com.br.tickets.services;

import com.br.tickets.models.City;
import com.br.tickets.models.Section;
import com.br.tickets.models.Venue;
import com.br.tickets.models.dto.CreateVenueDTO;
import com.br.tickets.models.dto.SectionListDTO;
import com.br.tickets.models.dto.VenueListDTO;
import com.br.tickets.repositories.CityRepository;
import com.br.tickets.repositories.SectionRepository;
import com.br.tickets.repositories.VenuesRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class VenuesService {

    private final VenuesRepository venuesRepository;
    private final CityRepository cityRepository;
    private final SectionRepository sectionRepository;

    public List<VenueListDTO> getAllVenues() {
        return venuesRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public VenueListDTO getVenueById(Long id) {
        Venue venue = venuesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venue not found with id: " + id));
        return toDTO(venue);
    }

    @Transactional
    public VenueListDTO createVenue(CreateVenueDTO dto) {
        City city = cityRepository.findById(dto.cityId())
                .orElseThrow(() -> new RuntimeException("City not found with id: " + dto.cityId()));

        Venue venue = venuesRepository.save(Venue.builder()
                .name(dto.name())
                .description(dto.description())
                .address(dto.address())
                .longitude(dto.longitude())
                .latitude(dto.latitude())
                .plant_url(dto.plantUrl())
                .photos(dto.photos())
                .city(city)
                .build());

        List<Section> sections = dto.sections().stream()
                .map(s -> Section.builder()
                        .name(s.name())
                        .capacity(s.capacity())
                        .numbered(s.numbered())
                        .venue(venue)
                        .build())
                .toList();

        venue.setSections(sectionRepository.saveAll(sections));

        return toDTO(venue);
    }

    public VenueListDTO updateVenue(Long id, CreateVenueDTO dto) {
        Venue venue = venuesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venue not found with id: " + id));

        City city = cityRepository.findById(dto.cityId())
                .orElseThrow(() -> new RuntimeException("City not found with id: " + dto.cityId()));

        venue.setName(dto.name());
        venue.setDescription(dto.description());
        venue.setAddress(dto.address());
        venue.setLongitude(dto.longitude());
        venue.setLatitude(dto.latitude());
        venue.setPlant_url(dto.plantUrl());
        venue.setPhotos(dto.photos());
        venue.setCity(city);

        return toDTO(venuesRepository.save(venue));
    }

    private VenueListDTO toDTO(Venue venue) {
        Long cityId = venue.getCity() != null ? venue.getCity().getId() : null;
        String cityName = venue.getCity() != null ? venue.getCity().getName() : null;
        Long stateId = venue.getCity() != null ? venue.getCity().getState().getId() : null;
        List<SectionListDTO> sections = venue.getSections() != null
                ? venue.getSections().stream()
                        .map(s -> new SectionListDTO(s.getId(), s.getName(), s.getCapacity(), s.getNumbered()))
                        .toList()
                : List.of();
        return new VenueListDTO(
                venue.getId(),
                venue.getName(),
                venue.getDescription(),
                venue.getAddress(),
                venue.getLongitude(),
                venue.getLatitude(),
                venue.getPlant_url(),
                venue.getPhotos(),
                cityId,
                cityName,
                stateId,
                sections
        );
    }
}
