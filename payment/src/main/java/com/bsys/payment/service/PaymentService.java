package com.bsys.payment.service;

import com.bsys.payment.clients.CustomerClient;
import com.bsys.payment.clients.PaymentClient;
import com.bsys.payment.clients.dto.BillingRequestDTO;
import com.bsys.payment.clients.dto.Customer;
import com.bsys.payment.clients.dto.successResponse.BillingSuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentClient paymentClient;
    private final CustomerClient customerClient;

    public Customer createBilling(String userId) {
        return customerClient.getCustomerById(userId).getBody().data();
    }
}
