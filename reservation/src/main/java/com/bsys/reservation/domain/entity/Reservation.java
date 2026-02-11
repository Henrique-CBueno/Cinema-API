package com.bsys.reservation.domain.entity;

import com.bsys.reservation.domain.entity.enums.ReserveState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "reserve")
@Getter
@Setter
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(nullable = false)
    UUID userId;

    @Column(name = "session_id", nullable = false)
    UUID sessionId;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    List<Seats> seats;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ReserveState status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}
