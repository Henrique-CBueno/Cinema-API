package com.bsys.reservation.clients.payment.dto;

import com.bsys.reservation.clients.payment.enums.PaymentStatus;

import java.util.List;

public record BillingSuccessResponse(Data data,
                                     String error
) {

    public record Data(
            String id,
            String url,
            PaymentStatus status, // Ex: "PENDING", "PAID"
            boolean devMode,
            List<String> methods,
            List<ResponseProduct> products,
            String frequency,
            int amount, // Valor total em centavos
            String nextBilling, // Pode ser uma data ISO ou "null"
            ResponseCustomer customer,
            boolean allowCoupons,
            List<String> coupons
    ) {}

    public record ResponseProduct(
            String id,
            String externalId,
            int quantity
    ) {}

    public record ResponseCustomer(
            String id,
            CustomerMetadata metadata
    ) {}

    public record CustomerMetadata(
            String name,
            String cellphone,
            String email,
            String taxId
    ) {}
}