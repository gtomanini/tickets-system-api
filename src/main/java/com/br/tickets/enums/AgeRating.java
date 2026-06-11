package com.br.tickets.enums;

public enum AgeRating {
    FREE("Livre"),
    AGE_10("Maiores de 10 anos"),
    AGE_12("Maiores de 12 anos"),
    AGE_14("Maiores de 14 anos"),
    AGE_16("Maiores de 16 anos"),
    AGE_18("Maiores de 18 anos");

    private final String description;

    AgeRating(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
