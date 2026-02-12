package com.bsys.reservation.webhook.dto.req;

public record WebhookData(
        PaymentInfo payment,
        BillingInfo billing
) {}
