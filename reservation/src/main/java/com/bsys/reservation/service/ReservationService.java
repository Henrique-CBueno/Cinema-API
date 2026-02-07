package com.bsys.reservation.service;

import com.bsys.reservation.clients.catalog.DTO.SeatsExistenceResDTO;
import com.bsys.reservation.clients.catalog.DTO.SessionResDTO;
import com.bsys.reservation.clients.catalog.SeatsClient;
import com.bsys.reservation.clients.catalog.SessionClient;
import com.bsys.reservation.domain.entity.Reservation;
import com.bsys.reservation.domain.entity.enums.ReserveState;
import com.bsys.reservation.domain.dto.req.CreateReservationDTO;
import com.bsys.reservation.infra.constants.ExceptionConstants;
import com.bsys.reservation.infra.exceptions.SessionUnavailableException;
import com.bsys.reservation.infra.exceptions.SeatDontExistsException;
import com.bsys.reservation.infra.exceptions.SeatUnavailableException;
import com.bsys.reservation.infra.exceptions.ReservationPersistenceException;
import com.bsys.reservation.infra.exceptions.SeatAlreadyReservedException;
import com.bsys.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final SessionClient sessionClient;
    private final SeatsClient seatsClient;
    private final RedisService redisService;
    private final ReservationRepository reservationRepository;

    @Transactional
    public UUID createReservation(CreateReservationDTO dto,
            String email,
            String username,
            String userId) {

        List<UUID> seatIds = dto.seats();
        assertSeatsNotLocked(dto.sessionId(), seatIds);

        SessionResDTO session = Objects.requireNonNull(sessionClient.getSession(dto.sessionId().toString())
                .getBody())
                .data();

        if (session.startTime() == null || !session.startTime().isAfter(LocalDateTime.now())) {
            throw new SessionUnavailableException(ExceptionConstants.UNAVAIBLE_SESSION);
        }

        SeatsExistenceResDTO resSeats = seatsClient.checkSeatsExistence(dto.cinemaId().toString(),
                session.room().id().toString(),
                dto.seats()).getBody();

        if (!resSeats.allExists()) {
            throw new SeatDontExistsException(String.format(
                    ExceptionConstants.SEAT_DONT_EXISTS_IN_ROOM,
                    resSeats.missingSeatIds()));
        }

        boolean seetIsntReservedYet = reservationRepository.noConfirmedReservationExists(dto.sessionId(),
                dto.seats());

        if (!seetIsntReservedYet) {
            throw new SeatAlreadyReservedException(ExceptionConstants.SEAT_ALREADY_RESERVED);
        }

        List<Reservation> reservations = buildPendingReservations(dto, seatIds, userId);
        List<Reservation> saved;
        try {
            saved = reservationRepository.saveAll(reservations);
        } catch (DataIntegrityViolationException ex) {
            throw new ReservationPersistenceException(ExceptionConstants.RESERVATION_ALREADY_EXISTS, ex);
        }

        lockSeatsOrFail(dto.sessionId(), seatIds, userId);

        return saved.getFirst().getId();
    }

    private void assertSeatsNotLocked(UUID sessionId, List<UUID> seatIds) {
        for (UUID seatId : seatIds) {
            if (redisService.isSeatLocked(sessionId, seatId.toString())) {
                throw new SeatUnavailableException(String.format(
                        ExceptionConstants.SEAT_UNAVAIBLE,
                        seatId));
            }
        }
    }

    private List<Reservation> buildPendingReservations(CreateReservationDTO dto,
            List<UUID> seatIds,
            String userId) {

        UUID userUuid = UUID.fromString(userId);
        return seatIds.stream()
                .map(seatId -> {
                    Reservation reservation = new Reservation();
                    reservation.setUserId(userUuid);
                    reservation.setSessionId(dto.sessionId());
                    reservation.setSeatId(seatId);
                    reservation.setStatus(ReserveState.PENDING_PAYMENT);
                    return reservation;
                })
                .toList();
    }

    private void lockSeatsOrFail(UUID sessionId, List<UUID> seatIds, String userId) {
        List<String> seatNumbers = seatIds.stream()
                .map(UUID::toString)
                .toList();

        if (!redisService.lockSeats(sessionId, seatNumbers, userId)) {
            throw new SeatUnavailableException(String.format(
                    ExceptionConstants.SEAT_UNAVAIBLE,
                    String.join(",", seatNumbers)));
        }
    }
}
