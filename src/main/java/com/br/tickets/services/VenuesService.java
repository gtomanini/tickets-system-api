package com.br.tickets.services;

import java.util.List;
import com.br.tickets.models.dto.VenueListDTO;

import org.springframework.stereotype.Service;

import com.br.tickets.models.Venue;
import com.br.tickets.repositories.VenuesRepository;

import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class VenuesService {

    private final VenuesRepository venuesRepository;
    
    public VenueListDTO getVenueById(Long venueId) {
        Venue venue = venuesRepository.findById(venueId)
            .orElseThrow(() -> new RuntimeException("Venue not found with id: " + venueId));

        return new VenueListDTO(
            venue.getId(), 
            venue.getName(),
            venue.getDescription(),
            venue.getAddress(),
            venue.getLongitude(),
            venue.getLatitude(),
            venue.getPlant_url(),
            venue.getPhotos(),
            venue.getCity()
        );
    }

    public List<VenueListDTO> getAllVenues() {
        List<Venue> venues = venuesRepository.findAll();

        return venues.stream().map(venue -> new VenueListDTO(
            venue.getId(), 
            venue.getName(),
            venue.getDescription(),
            venue.getAddress(),
            venue.getLongitude(),
            venue.getLatitude(),
            venue.getPlant_url(),
            venue.getPhotos(),
            venue.getCity()
        )).toList();
    }

}
