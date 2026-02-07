package com.bsys.reservation.repository;

import com.bsys.reservation.domain.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    @Query("""
                SELECT CASE\s
                WHEN COUNT(r) = 0 THEN true
                ELSE false
                END
                FROM Reservation r
                WHERE r.sessionId = :sessionId
                AND r.seatId IN :seatIds
                AND r.status = com.bsys.reservation.domain.entity.enums.ReserveState.CONFIRMED
            """)
    boolean noConfirmedReservationExists(
            @Param("sessionId") UUID sessionId,
            @Param("seatIds") List<UUID> seatIds);
}
