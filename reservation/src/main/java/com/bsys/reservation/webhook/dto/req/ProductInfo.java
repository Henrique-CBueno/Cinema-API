package com.bsys.reservation.webhook.dto.req;

public record ProductInfo(
        String externalId,
        String id,
        Integer quantity
) {
}
