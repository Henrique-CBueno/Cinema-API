package com.bsys.payment.clients.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.time.Instant;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    private String id;

    private String keycloakUserId;

    private String name;

    private String email;

    private String phone;

    private String customerId;

    private String taxId;

    private Instant createdAt;
}
