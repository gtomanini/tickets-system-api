package com.br.tickets.models.dto;

import com.br.tickets.enums.AgeRating;

public record AgeRatingDTO(String value, String description) {

    public static AgeRatingDTO from(AgeRating ageRating) {
        if (ageRating == null) return null;
        return new AgeRatingDTO(ageRating.name(), ageRating.getDescription());
    }
}
