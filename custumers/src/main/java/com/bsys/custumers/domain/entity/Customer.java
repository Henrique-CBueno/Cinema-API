package com.bsys.custumers.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    private String id;

    @Indexed(unique = true)
    private String keycloakUserId;

    private String phone;

    @Indexed(unique = true)
    private String customerId;

    @Indexed(unique = true)
    private String taxId;

    private Instant createdAt;
}
