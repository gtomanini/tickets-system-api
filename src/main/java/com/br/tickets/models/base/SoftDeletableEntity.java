package com.br.tickets.models.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@SQLDelete(sql = "UPDATE #{#entityName} SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
@Getter
@Setter
public abstract class SoftDeletableEntity extends AuditableEntity{

    @Column(nullable = true, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean deleted = false;
}
