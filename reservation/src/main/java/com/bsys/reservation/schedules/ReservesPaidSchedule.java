package com.bsys.reservation.schedules;

import com.bsys.reservation.clients.payment.ExternalPaymentClient;
import com.bsys.reservation.clients.payment.dto.BillingListResponse;
import com.bsys.reservation.clients.payment.dto.BillingSuccessResponse;
import com.bsys.reservation.clients.payment.enums.PaymentStatus;
import com.bsys.reservation.webhook.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Component
@RequiredArgsConstructor
@Slf4j
public class ReservesPaidSchedule {

    private final ExternalPaymentClient externalPaymentClient;
    private final WebhookService webhookService;

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.HOURS)
    public void checkPaymentsAndUpdate() {

        log.info("rodou");
        try {

            BillingListResponse response = externalPaymentClient.listBillings();
            if (response == null || response.data() == null || response.data().isEmpty()) {
                return;
            }

            for (BillingSuccessResponse.Data billing : response.data()) {
                if (billing == null || billing.status() != PaymentStatus.PAID) {
                    continue;
                }


                if (billing.products() == null || billing.products().isEmpty()) {
                    continue;
                }

                for (BillingSuccessResponse.ResponseProduct product : billing.products()) {
                    String externalId = (product.externalId());
                    if (externalId == null || externalId.isBlank()) {
                        continue;
                    }

                    try {
                        UUID.fromString(externalId);
                    } catch (IllegalArgumentException ex) {
                        continue;
                    }

                    webhookService.paid(externalId);
                }
            }
        } catch (Exception ex) {
            log.warn("Failed to sync paid reservations from external payment service", ex);
        }
    }
}
