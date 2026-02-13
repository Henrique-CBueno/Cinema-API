package com.bsys.reservation.schedules;

import com.bsys.reservation.clients.payment.ExternalPaymentClient;
import com.bsys.reservation.clients.payment.dto.BillingListResponse;
import com.bsys.reservation.clients.payment.dto.BillingSuccessResponse;
import com.bsys.reservation.clients.payment.enums.PaymentStatus;

import com.bsys.reservation.webhook.WebhookService;
import com.bsys.reservation.clients.catalog.BatchClient;
import com.bsys.reservation.clients.catalog.DTO.BatchResDTO;
import com.bsys.reservation.clients.catalog.DTO.BatchReserveReqDTO;

import com.bsys.reservation.domain.entity.Reservation;
import com.bsys.reservation.infra.padronize.SuccessResponse;
import com.bsys.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
@Slf4j
public class ReservesPaidSchedule {

    private final ExternalPaymentClient externalPaymentClient;
    private final WebhookService webhookService;
    private final ReservationRepository reservationRepository;
    private final BatchClient batchClient;

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.HOURS)
    public void checkPaymentsAndUpdate() {

        log.info("Starting checkPaymentsAndUpdate cycle");
        try {
            BillingListResponse response = externalPaymentClient.listBillings();
            if (isResponseEmpty(response)) {
                log.info("No billings found from external payment client.");
                return;
            }

            log.info("Found {} billings to process.", response.data().size());

            List<UUID> reservationIds = new ArrayList<>();
            List<BillingSuccessResponse.Data> paidBillings = extractPaidBillings(response.data(), reservationIds);

            if (reservationIds.isEmpty()) {
                log.info("No valid reservation IDs found in paid billings.");
                return;
            }

            log.info("Collected {} reservation IDs: {}", reservationIds.size(), reservationIds);

            Map<UUID, Reservation> reservationMap = fetchReservations(reservationIds);
            if (reservationMap.isEmpty()) {
                return;
            }

            List<BatchResDTO> batchResponses = fetchBatchDetails(reservationMap.values());
            if (batchResponses == null) {
                return;
            }

            Map<UUID, List<BatchResDTO>> groupedDetails = groupDetailsByReservation(batchResponses);

            processAndPublishPayments(paidBillings, reservationMap, groupedDetails);

        } catch (Exception ex) {
            log.error("Unexpected error in checkPaymentsAndUpdate", ex);
        }
    }

    private boolean isResponseEmpty(BillingListResponse response) {

        return response == null || response.data() == null || response.data().isEmpty();
    }

    private List<BillingSuccessResponse.Data> extractPaidBillings(
            List<BillingSuccessResponse.Data> billings,
            List<UUID> reservationIds) {

        List<BillingSuccessResponse.Data> paidBillings = new ArrayList<>();

        for (BillingSuccessResponse.Data billing : billings) {
            if (billing == null) continue;

            if (billing.status() != PaymentStatus.PAID) {
                continue;
            }

            String externalIdStr = billing.externalId();
            if (externalIdStr == null || externalIdStr.isBlank()) {
                log.warn("Paid billing {} has no externalId.", billing.id());
                continue;
            }

            try {
                reservationIds.add(UUID.fromString(externalIdStr));
                paidBillings.add(billing);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid UUID for externalId: {}", externalIdStr);
            }
        }

        return paidBillings;
    }

    private Map<UUID, Reservation> fetchReservations(List<UUID> reservationIds) {

        List<Reservation> reservations = reservationRepository.findAllByIdWithSeats(reservationIds);

        if (reservations.isEmpty()) {
            log.warn("No reservations found in DB for the collected IDs.");
            return Collections.emptyMap();
        }

        log.info("Found {} reservations in DB.", reservations.size());

        return reservations.stream()
                .collect(Collectors.toMap(Reservation::getId, r -> r));
    }

    private List<BatchResDTO> fetchBatchDetails(Collection<Reservation> reservations) {
        List<BatchReserveReqDTO> batchRequests = buildBatchRequests(reservations);

        if (batchRequests.isEmpty()) {
            log.warn("No seats found for the retrieved reservations.");
            return null;
        }

        log.info("Prepared {} batch requests for seat details.", batchRequests.size());

        try {
            ResponseEntity<SuccessResponse<List<BatchResDTO>>> batchResponse =
                    batchClient.getReservesBatch(batchRequests);

            if (batchResponse.getBody() == null || batchResponse.getBody().data() == null) {
                log.warn("Batch client returned empty response or null body.");
                return null;
            }

            List<BatchResDTO> batchResponses = batchResponse.getBody().data();
            log.info("Batch client returned {} response items.", batchResponses.size());

            return batchResponses;

        } catch (Exception e) {
            log.error("Failed to fetch batch details from Catalog Service", e);
            return null;
        }
    }

    private Map<UUID, List<BatchResDTO>> groupDetailsByReservation(List<BatchResDTO> batchResponses) {
        return batchResponses.stream()
                .collect(Collectors.groupingBy(BatchResDTO::reservationId));
    }

    private void processAndPublishPayments(
            List<BillingSuccessResponse.Data> paidBillings,
            Map<UUID, Reservation> reservationMap,
            Map<UUID, List<BatchResDTO>> groupedDetails) {

        for (BillingSuccessResponse.Data billing : paidBillings) {
            UUID externalId = parseExternalId(billing.externalId());
            if (externalId == null) continue;

            Reservation reservation = reservationMap.get(externalId);
            if (reservation == null) {
                log.warn("Reserva não encontrada no mapa para o ID: {}", externalId);
                continue;
            }

            if (isReserveConsumed(reservation))  {
                log.info("Reservation {} already consumed, skipping payment processing.", externalId);
                continue;
            }

            List<BatchResDTO> details = groupedDetails.get(externalId);
            if (details == null || details.isEmpty()) {
                log.warn("Batch details missing or empty for reservation ID: {}", externalId);
                continue;
            }
            
            webhookService.processPayment(reservation, details, billing.externalId());
        }
    }

    private UUID parseExternalId(String externalIdStr) {

        if (externalIdStr == null) return null;

        try {
            return UUID.fromString(externalIdStr);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid UUID for externalId: {}", externalIdStr);
            return null;
        }
    }

    private List<BatchReserveReqDTO> buildBatchRequests(Collection<Reservation> reservations) {

        return WebhookService.getBatchReserveReqDTOS(reservations);
    }

    private boolean isReserveConsumed(Reservation reservation) {

        return reservation.isConsumed();
    }

}

