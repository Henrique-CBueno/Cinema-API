package com.bsys.reservation.repository;

import com.bsys.reservation.domain.entity.Reservation;
import com.bsys.reservation.domain.entity.enums.ReserveState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
                            JOIN r.seats s
                            WHERE r.sessionId = :sessionId
                            AND s.seatId IN :seatIds
                            AND r.status = com.bsys.reservation.domain.entity.enums.ReserveState.CONFIRMED
                        """)
        boolean noConfirmedReservationExists(
                        @Param("sessionId") UUID sessionId,
                        @Param("seatIds") List<UUID> seatIds);

        @Modifying
        @Transactional
        @Query("""
                            UPDATE Reservation r
                            SET r.status = :newStatus
                            WHERE r.sessionId = :sessionId
                            AND EXISTS (SELECT s FROM r.seats s WHERE s.seatId = :seatId)
                            AND r.status = :currentStatus
                        """)
        int updateReservationStatus(@Param("sessionId") UUID sessionId,
                        @Param("seatId") UUID seatId,
                        @Param("currentStatus") ReserveState currentStatus,
                        @Param("newStatus") ReserveState newStatus);

        @Modifying
        @Transactional
        @Query("""
                            UPDATE Seats r
                            SET r.status = :newStatus
                            WHERE r.sessionId = :sessionId
                            AND r.status = :currentStatus
                        """)
        int updateSeatsStatus(@Param("sessionId") UUID sessionId,
                        @Param("seatId") UUID seatId,
                        @Param("currentStatus") ReserveState currentStatus,
                        @Param("newStatus") ReserveState newStatus);

        Page<Reservation> findAllByUserId(UUID userId, Pageable pageable);

        @Modifying
        @Transactional
        @Query("""
                            UPDATE Reservation r
                            SET r.status = 'CANCELED'
                            WHERE r.id = :reservationId
                            AND r.status = 'CONFIRMED'
                            AND (:isAdmin = true OR r.userId = :userId)
                        """)
        int cancelReservation(@Param("reservationId") UUID reservationId,
                        @Param("userId") UUID userId,
                        @Param("isAdmin") boolean isAdmin);

        @Modifying
        @Transactional
        @Query("""
                            UPDATE Seats r
                            SET r.status = 'CANCELED'
                            WHERE r.reservation.id = :reservationId
                            AND r.status = 'CONFIRMED'
                            AND (:isAdmin = true OR r.reservation.userId = :userId)
                        """)
        int cancelSeat(@Param("reservationId") UUID reservationId,
                        @Param("userId") UUID userId,
                        @Param("isAdmin") boolean isAdmin);

        @Modifying
        @Transactional
        @Query("""
                            UPDATE Reservation r
                            SET r.status = 'CONFIRMED'
                            WHERE r.id = :reservationId
                            AND r.status = 'PENDING_PAYMENT'
                        """)
        int setReservePaid(@Param("reservationId") UUID reservationId);

        @Modifying
        @Transactional
        @Query("""
                            UPDATE Seats s
                            SET s.status = 'CONFIRMED'
                            WHERE s.reservation.id = :reservationId
                            AND s.status = 'PENDING_PAYMENT'
                        """)
        int setSeatPaid(@Param("reservationId") UUID reservationId);
        @Query("SELECT r FROM Reservation r LEFT JOIN FETCH r.seats WHERE r.id IN :ids")
        List<Reservation> findAllByIdWithSeats(@Param("ids") List<UUID> ids);

        @Modifying
        @Transactional
        @Query("""
                            UPDATE Reservation r
                            SET r.consumed = true
                            WHERE r.id = :reservationId
                            AND r.status = 'CONFIRMED'
                        """)
        void setConsumed(@Param("reservationId") UUID reservationId);
}
