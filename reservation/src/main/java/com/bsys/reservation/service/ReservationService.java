package com.bsys.reservation.service;

import com.bsys.reservation.clients.catalog.BatchClient;
import com.bsys.reservation.clients.catalog.DTO.BatchResDTO;
import com.bsys.reservation.clients.catalog.DTO.BatchReserveReqDTO;
import com.bsys.reservation.clients.catalog.DTO.SeatsExistenceResDTO;
import com.bsys.reservation.clients.catalog.DTO.SessionResDTO;
import com.bsys.reservation.clients.catalog.SeatsClient;
import com.bsys.reservation.clients.catalog.SessionClient;
import com.bsys.reservation.domain.dto.res.ReservationResDTO;
import com.bsys.reservation.domain.entity.Reservation;
import com.bsys.reservation.domain.entity.enums.ReserveState;
import com.bsys.reservation.domain.dto.req.CreateReservationDTO;
import com.bsys.reservation.infra.constants.ExceptionConstants;
import com.bsys.reservation.infra.exceptions.SessionUnavailableException;
import com.bsys.reservation.infra.exceptions.SeatDontExistsException;
import com.bsys.reservation.infra.exceptions.SeatUnavailableException;
import com.bsys.reservation.infra.exceptions.ReservationPersistenceException;
import com.bsys.reservation.infra.exceptions.SeatAlreadyReservedException;
import com.bsys.reservation.infra.exceptions.ReservationsNotFound;
import com.bsys.reservation.infra.padronize.SuccessResponse;
import com.bsys.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

        private final SessionClient sessionClient;
        private final SeatsClient seatsClient;
        private final RedisService redisService;
        private final ReservationRepository reservationRepository;
        private final BatchClient batchClient;

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

                SeatsExistenceResDTO resSeats = seatsClient.checkSeatsExistence(session.cinemaId().toString(),
                                session.room().id().toString(),
                                dto.seats()).getBody();

                if (resSeats == null) throw new SessionUnavailableException(ExceptionConstants.SEAT_VALIDATION_FAILED);

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

        public Page<ReservationResDTO> getUserReservation(UUID userId,
                        Pageable pageable) {

                Page<Reservation> reservations = reservationRepository.findAllByUserId(userId, pageable);

                if (reservations.isEmpty()) return Page.empty(pageable);

                List<Reservation> reservationsContent = reservations.getContent();

                List<BatchReserveReqDTO> batchReserveReqDTOS = reservationsContent
                                    .stream()
                                    .map(
                                                    reservation ->
                                                             new BatchReserveReqDTO(
                                                                     reservation.getId(),
                                                                     reservation.getSessionId(),
                                                                     reservation.getSeatId())
                                                    )
                                    .toList();

                List<BatchResDTO> reservesBatch = Optional
                                .ofNullable(batchClient.getReservesBatch(batchReserveReqDTOS).getBody())
                                .map(SuccessResponse::data)
                                .orElseThrow(() -> new ReservationsNotFound(ExceptionConstants.RESERVATIONS_NOT_FOUND));


                Map<UUID, ReserveState> statusMap = reservationsContent.stream()
                                    .collect(Collectors.toMap(
                                                    Reservation::getId,
                                                    Reservation::getStatus,
                                                    (a, b) -> a)
                                    );

                List<ReservationResDTO> listReservationRes = reservesBatch.stream().map(
                        reserveBatch -> {

                            ReserveState status = statusMap.getOrDefault(reserveBatch.reservationId(), ReserveState.CANCELED); // Default para o codigo nao quebrar se for null
                            String seat = reserveBatch.seatResDTO().rowLabel() + reserveBatch.seatResDTO().columnNumber();

                            return new ReservationResDTO(
                                    reserveBatch.reservationId(),
                                    status.toString(),
                                    reserveBatch.sessionResDTO().startTime(),
                                    reserveBatch.sessionResDTO().endTime(),
                                    seat);
                        }).toList();

                return new PageImpl<>(listReservationRes,
                        pageable,
                        reservations.getTotalElements());
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

        @Transactional
        public boolean expirePendingReservation(UUID sessionId, UUID seatId) {
                int updated = reservationRepository.updateReservationStatus(
                                sessionId,
                                seatId,
                                ReserveState.PENDING_PAYMENT,
                                ReserveState.EXPIRED);
                return updated > 0;
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
