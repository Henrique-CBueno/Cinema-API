package com.bsys.reservation.webhook.dto.req;

public record PaymentInfo(
        Integer amount,
        Integer fee,
        String method
) {}
