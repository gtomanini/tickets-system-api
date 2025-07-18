package com.br.tickets.models.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class EventSearchCriteria {
    private String name;
    private String status;
}
