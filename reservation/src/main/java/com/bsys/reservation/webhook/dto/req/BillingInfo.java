package com.bsys.reservation.webhook.dto.req;

import java.util.List;

public record BillingInfo(
        Integer amount,
        List<String> couponsUsed,
        CustomerInfo customer,
        String frequency,
        String id,
        List<String> kind,
        Integer paidAmount,
        List<ProductInfo> products,
        String status
) {}
