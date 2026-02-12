package com.bsys.reservation.service;

import com.bsys.reservation.clients.catalog.BatchClient;
import com.bsys.reservation.clients.catalog.DTO.*;
import com.bsys.reservation.clients.catalog.SeatsClient;
import com.bsys.reservation.clients.catalog.SessionClient;
import com.bsys.reservation.clients.payment.PaymentClient;
import com.bsys.reservation.clients.payment.dto.BillingSuccessResponse;
import com.bsys.reservation.clients.payment.dto.ReservationReqDTO;
import com.bsys.reservation.domain.dto.res.ReservationResDTO;
import com.bsys.reservation.domain.entity.Reservation;
import com.bsys.reservation.domain.entity.Seats;
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
import org.jspecify.annotations.NonNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
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
        private final PaymentClient paymentClient;

        @Transactional
        public BillingSuccessResponse createReservation(CreateReservationDTO dto,
                        String email,
                        String username,
                        String userId) {

                List<UUID> seatIds = dto.seats();
                assertSeatsNotLocked(dto.sessionId(), seatIds);

                SessionResDTO session = Objects.requireNonNull(sessionClient.getSession(dto.sessionId().toString())
                                .getBody())
                                .data();

                if (session.startTime() == null || !session.startTime().isAfter(LocalDateTime.now()) || session.status() != SessionStatus.SCHEDULED) {
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

                boolean seatIsntReservedYet = reservationRepository.noConfirmedReservationExists(dto.sessionId(),
                                dto.seats());

                if (!seatIsntReservedYet) {
                        throw new SeatAlreadyReservedException(ExceptionConstants.SEAT_ALREADY_RESERVED);
                }

                Reservation reservation = buildPendingReservation(dto, seatIds, userId);
                Reservation saved;
                try {
                        saved = reservationRepository.save(reservation);
                } catch (DataIntegrityViolationException ex) {
                        throw new ReservationPersistenceException(ExceptionConstants.RESERVATION_ALREADY_EXISTS, ex);
                }

                lockSeatsOrFail(dto.sessionId(), seatIds, userId);

                BigDecimal totalPrice = session.price().multiply(BigDecimal.valueOf(seatIds.size()));
                List<String> seatsAsString = seatIds.stream().map(UUID::toString).toList();

                BillingSuccessResponse billing = paymentClient.createBilling(new ReservationReqDTO(
                                saved.getId(),
                                session.movie().title(),
                                totalPrice,
                                saved.getStatus().toString(),
                                session.startTime(),
                                session.endTime(),
                                seatsAsString,
                                saved.getUserId()),
                        userId).getBody().data();

            return billing;
        }

        public Page<ReservationResDTO> getUserReservation(UUID userId,
                        Pageable pageable) {

                Page<Reservation> reservations = reservationRepository.findAllByUserId(userId, pageable);

                return getReservationResDTOS(pageable, reservations);
        }

        public Page<ReservationResDTO> getAllReservation(Pageable pageable) {

                Page<Reservation> reservations = reservationRepository.findAll(pageable);

                return getReservationResDTOS(pageable, reservations);
        }

        public void cancelReservation(UUID reservationId, UUID userId, Boolean isAdmin) {

                int affectedRows = reservationRepository.cancelReservation(reservationId, userId, isAdmin)
                                    + reservationRepository.cancelSeat(reservationId, userId, isAdmin);

            if (affectedRows < 1) throw new ReservationsNotFound(String.format(
                                        ExceptionConstants.RESERVATIONS_WITH_ID_NOT_FOUND,
                    reservationId
            ));
        }



        @NonNull
        private Page<ReservationResDTO> getReservationResDTOS(Pageable pageable, Page<Reservation> reservations) {
        if (reservations.isEmpty()) return Page.empty(pageable);

            List<Reservation> reservationsContent = reservations.getContent();
            List<BatchReserveReqDTO> batchRequests = getBatchReserveReqDTOS(reservationsContent);

            if (batchRequests.isEmpty()) {
                        return Page.empty(pageable);
                }

                List<BatchResDTO> reservesBatch = Optional
                                .ofNullable(batchClient.getReservesBatch(batchRequests).getBody())
                                .map(SuccessResponse::data)
                                .orElse(Collections.emptyList());

                Map<UUID, List<BatchResDTO>> groupedByReservation = reservesBatch.stream()
                                .collect(Collectors.groupingBy(BatchResDTO::reservationId));

                List<ReservationResDTO> listReservationRes = reservationsContent.stream().map(
                                r -> {
                                        List<BatchResDTO> batches = groupedByReservation.getOrDefault(r.getId(),
                                                        Collections.emptyList());

                                        String title = "Unknown";
                                        LocalDateTime start = null;
                                        LocalDateTime end = null;
                                        BigDecimal unitPrice = BigDecimal.ZERO;

                                        if (!batches.isEmpty()) {
                                                BatchResDTO first = batches.getFirst();
                                                title = first.sessionResDTO().movie().title();
                                                start = first.sessionResDTO().startTime();
                                                end = first.sessionResDTO().endTime();
                                                unitPrice = first.sessionResDTO().price();
                                        }

                                        List<String> seatLabels = batches.stream()
                                                        .map(b -> b.seatResDTO().rowLabel()
                                                                        + b.seatResDTO().columnNumber())
                                                        .toList();

                                        BigDecimal totalPrice = unitPrice
                                                        .multiply(BigDecimal.valueOf(seatLabels.size()));

                                        return new ReservationResDTO(
                                                        r.getId(),
                                                        title,
                                                        totalPrice,
                                                        r.getStatus().toString(),
                                                        start,
                                                        end,
                                                        seatLabels,
                                                        r.getUserId());
                                }).toList();

                return new PageImpl<>(listReservationRes,
                                pageable,
                                reservations.getTotalElements());
        }

    private static @NonNull List<BatchReserveReqDTO> getBatchReserveReqDTOS(List<Reservation> reservationsContent) {
        List<BatchReserveReqDTO> batchRequests = new ArrayList<>();

        for (Reservation r : reservationsContent) {
                if (r.getSeats() != null) {
                        for (Seats s : r.getSeats()) {
                                batchRequests.add(new BatchReserveReqDTO(
                                                r.getId(),
                                                r.getSessionId(),
                                                s.getSeatId(),
                                                r.getUserId()));
                        }
                }
        }
        return batchRequests;
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
                                ReserveState.EXPIRED)
                        + reservationRepository.updateSeatsStatus(
                                sessionId,
                                seatId,
                                ReserveState.PENDING_PAYMENT,
                                ReserveState.EXPIRED);

                return updated > 0;
        }

        private Reservation buildPendingReservation(CreateReservationDTO dto,
                        List<UUID> seatIds,
                        String userId) {

                UUID userUuid = UUID.fromString(userId);

                Reservation reservation = new Reservation();
                reservation.setUserId(userUuid);
                reservation.setSessionId(dto.sessionId());
                reservation.setStatus(ReserveState.PENDING_PAYMENT);

                List<Seats> seats = seatIds.stream()
                                .map(seatId -> Seats.builder()
                                                .seatId(seatId)
                                                .sessionId(dto.sessionId())
                                                .status(ReserveState.PENDING_PAYMENT)
                                                .reservation(reservation)
                                                .build())
                                .collect(Collectors.toList());

                reservation.setSeats(seats);
                return reservation;
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
