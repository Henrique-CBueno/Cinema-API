package com.bsys.payment.clients;

import com.bsys.payment.clients.dto.BillingRequestDTO;
import com.bsys.payment.clients.dto.CustomerClientDTO;
import com.bsys.payment.clients.dto.successResponse.BillingSuccessResponse;
import com.bsys.payment.clients.dto.successResponse.CustomerSuccessData;
import com.bsys.payment.config.PaymentClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "Payment-client",
        url = "${integration.payment.url}",
        configuration = PaymentClientConfiguration.class)
public interface PaymentClient {

    @PostMapping("/billing/create")
    Object createBilling(BillingRequestDTO dto);

    @PostMapping("/customer/create")
    CustomerSuccessData createCustomer(CustomerClientDTO dto);
}
