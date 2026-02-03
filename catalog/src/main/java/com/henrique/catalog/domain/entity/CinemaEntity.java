package com.henrique.catalog.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cinema",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_cinema_name_city",
                        columnNames = {"name", "city"}
                )
        })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
// Esta anotação faz com que Todos "SELECT" adicione automaticamente essa cláusula
@SQLRestriction("active = true")
public class CinemaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false, updatable = false)
    private UUID createdByUserId;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
