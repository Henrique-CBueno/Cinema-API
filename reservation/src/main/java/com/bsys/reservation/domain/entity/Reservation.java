package com.bsys.reservation.domain.entity;

import com.bsys.reservation.domain.entity.enums.ReserveState;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reserve",
        uniqueConstraints = {
            @UniqueConstraint(name = "uk_session_id_seat_id", columnNames = {"session_id", "seat_id"})
        })
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(nullable = false)
    UUID userId;

    @Column(name = "session_id", nullable = false)
    UUID sessionId;

    @Column(name = "seat_id", nullable = false)
    UUID seatId;

    @Column(nullable = false)
    ReserveState status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();


}
