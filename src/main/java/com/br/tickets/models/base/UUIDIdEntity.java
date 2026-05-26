package com.br.tickets.models.base;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
public abstract class UUIDIdEntity extends SoftDeletableEntity {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    protected UUID id;
}
