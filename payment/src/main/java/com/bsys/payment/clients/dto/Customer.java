package com.bsys.payment.clients.dto;

import org.springframework.data.annotation.Id;

import java.time.Instant;

public record Customer(String id,

                        String keycloakUserId,

                        String phone,

                        String customerId,

                        String taxId,

                        Instant createdAt) {
}
