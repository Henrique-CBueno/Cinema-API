package com.bsys.reservation.webhook.dto.req;

import java.util.Map;

public record CustomerInfo(
        String id,
        Map<String, String> metadata // Usando Map para flexibilidade no metadata
) {}
