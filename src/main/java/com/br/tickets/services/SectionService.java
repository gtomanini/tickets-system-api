package com.br.tickets.services;

import com.br.tickets.models.Section;
import com.br.tickets.models.Venue;
import com.br.tickets.models.dto.CreateSectionDTO;
import com.br.tickets.models.dto.SectionListDTO;
import com.br.tickets.repositories.SectionRepository;
import com.br.tickets.repositories.VenuesRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SectionService {

    private final SectionRepository sectionRepository;
    private final VenuesRepository venuesRepository;

    public List<SectionListDTO> listByVenue(Long venueId) {
        if (!venuesRepository.existsById(venueId)) {
            throw new RuntimeException("Venue not found with id: " + venueId);
        }
        return sectionRepository.findByVenueIdOrderByNameAsc(venueId).stream()
                .map(this::toDTO)
                .toList();
    }

    public SectionListDTO create(Long venueId, CreateSectionDTO dto) {
        Venue venue = venuesRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("Venue not found with id: " + venueId));

        Section section = Section.builder()
                .name(dto.name())
                .capacity(dto.capacity())
                .numbered(dto.numbered())
                .venue(venue)
                .build();

        return toDTO(sectionRepository.save(section));
    }

    public SectionListDTO update(Long venueId, Long sectionId, CreateSectionDTO dto) {
        if (!venuesRepository.existsById(venueId)) {
            throw new RuntimeException("Venue not found with id: " + venueId);
        }

        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Section not found with id: " + sectionId));

        section.setName(dto.name());
        section.setCapacity(dto.capacity());
        section.setNumbered(dto.numbered());

        return toDTO(sectionRepository.save(section));
    }

    private SectionListDTO toDTO(Section section) {
        return new SectionListDTO(
                section.getId(),
                section.getName(),
                section.getCapacity(),
                section.getNumbered()
        );
    }
}
