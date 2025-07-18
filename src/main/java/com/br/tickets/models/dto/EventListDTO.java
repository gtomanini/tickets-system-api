package com.br.tickets.models.dto;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class EventListDTO {
    private Long id;
    private String name;
    private LocalDateTime date;
    private String status;
}
