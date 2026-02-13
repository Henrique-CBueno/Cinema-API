package com.bsys.reservation.webhook;

import com.bsys.reservation.clients.catalog.BatchClient;
import com.bsys.reservation.clients.catalog.DTO.BatchResDTO;
import com.bsys.reservation.clients.catalog.DTO.BatchReserveReqDTO;
import com.bsys.reservation.clients.catalog.DTO.SessionResDTO;
import com.bsys.reservation.clients.customer.Customer;
import com.bsys.reservation.clients.customer.CustomerClient;
import com.bsys.reservation.domain.entity.Reservation;
import com.bsys.reservation.domain.entity.Seats;
import com.bsys.reservation.infra.constants.MessageConstants;
import com.bsys.reservation.infra.padronize.SuccessResponse;
import com.bsys.reservation.publisher.SnsPublisherExchanges;
import com.bsys.reservation.publisher.SnsPublisherService;
import com.bsys.reservation.publisher.dto.CustomerDTO;
import com.bsys.reservation.publisher.dto.ReservationPaidConsumerDTO;
import com.bsys.reservation.publisher.dto.ReservationPaidPublish;
import com.bsys.reservation.repository.ReservationRepository;
import com.bsys.reservation.webhook.dto.req.PaidDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookService {

    private final ReservationRepository reservationRepository;
    private final CustomerClient customerClient;
    private final SnsPublisherService snsPublisherService;
    private final BatchClient batchClient;

    @Transactional
    public void processPayment(Reservation reservation, List<BatchResDTO> details, String paymentExternalId) {
        if (reservation.isConsumed()) {
            log.info("Reservation {} already consumed, skipping payment processing logic.", reservation.getId());
            return;
        }

        markReservationAsPaid(reservation.getId());

        publishReservationPaidEvent(paymentExternalId, reservation, details);
    }

    @Transactional
    public void handleRawPayment(String reservationIdStr) {
        UUID reservationId = UUID.fromString(reservationIdStr);
        
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found: " + reservationId)); // Simple exception for now, can be improved

        if (reservation.isConsumed()) {
             log.info("Reservation {} already consumed (checked in handleRawPayment).", reservationId);
             return;
        }

        // We need the seats to get batch details
        List<BatchReserveReqDTO> batchRequests = getBatchReserveReqDTOS(Collections.singletonList(reservation));
        
        if (batchRequests.isEmpty()) {
            log.warn("No seats found for reservation {}", reservationId);
            return;
        }

        List<BatchResDTO> batchDetails = fetchBatchDetails(batchRequests);
        if (batchDetails == null || batchDetails.isEmpty()) {
             log.warn("Could not fetch batch details for reservation {}", reservationId);
             throw new RuntimeException("Could not fetch batch details");
        }

        processPayment(reservation, batchDetails, reservationIdStr);
    }


    private void markReservationAsPaid(UUID reservationId) {
        reservationRepository.setReservePaid(reservationId);
        reservationRepository.setSeatPaid(reservationId);
    }
    
    // --- Helper Methods moved/adapted from ReservesPaidSchedule ---

    private void publishReservationPaidEvent(
            String paymentExternalId,
            Reservation reservation,
            List<BatchResDTO> details) {

        BatchResDTO firstDetail = details.getFirst();
        SessionResDTO session = firstDetail.sessionResDTO();

        List<String> seatLabels = details.stream()
                .map(d -> d.seatResDTO().rowLabel() + d.seatResDTO().columnNumber())
                .toList();

        String formattedDateHour = session.startTime()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        Customer customer = fetchCustomer(reservation.getUserId());

        log.info("Publishing RESERVATION_PAID_EXCHANGE for reservation {}", reservation.getId());

        ReservationPaidConsumerDTO<ReservationPaidPublish> message = buildReservationPaidMessage(
                customer,
                paymentExternalId,
                seatLabels,
                formattedDateHour,
                session
        );

        snsPublisherService.sendMessage(SnsPublisherExchanges.RESERVATION_PAID_EXCHANGE, message);

        reservationRepository.setConsumed(reservation.getId());
    }

    private Customer fetchCustomer(UUID userId) {
        return Objects.requireNonNull(
                customerClient.getCustomerById(userId.toString()).getBody()
        ).data();
    }
    
    private List<BatchResDTO> fetchBatchDetails(List<BatchReserveReqDTO> batchRequests) {
        try {
            ResponseEntity<SuccessResponse<List<BatchResDTO>>> batchResponse =
                    batchClient.getReservesBatch(batchRequests);

            if (batchResponse.getBody() == null || batchResponse.getBody().data() == null) {
                return null;
            }
            return batchResponse.getBody().data();

        } catch (Exception e) {
            log.error("Failed to fetch batch details from Catalog Service", e);
            return null;
        }
    }

    private ReservationPaidConsumerDTO<ReservationPaidPublish> buildReservationPaidMessage(
            Customer customer,
            String externalId,
            List<String> seatLabels,
            String formattedDateHour,
            SessionResDTO session) {

        return new ReservationPaidConsumerDTO<ReservationPaidPublish>(
                new ReservationPaidPublish(
                        new CustomerDTO(
                                customer.name(),
                                customer.taxId(),
                                customer.email(),
                                formatPhoneNumber(customer.phone())
                        ),
                        externalId,
                        String.join(", ", seatLabels),
                        formattedDateHour,
                        session.movie().title(),
                        session.room().cinema().name(),
                        session.room().name(),
                        String.format(MessageConstants.MESSAGE,
                                customer.name().split("@")[0],
                                formattedDateHour,
                                "Confirmada"),
                        session.price()
                )
        );
    }

    private String formatPhoneNumber(String phone) {
        return "+55" + phone.replaceAll("\\D", "");
    }

    @NonNull
    public static List<BatchReserveReqDTO> getBatchReserveReqDTOS(Collection<Reservation> reservations) {
        List<BatchReserveReqDTO> batchRequests = new ArrayList<>();

        for (Reservation r : reservations) {
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
}
