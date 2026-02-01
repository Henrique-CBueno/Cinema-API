package com.henrique.catalog.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "cinema")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CinemaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false, updatable = false)
    private UUID createdByUserId;

    @Column(nullable = false)
    private Boolean active = true;
}
