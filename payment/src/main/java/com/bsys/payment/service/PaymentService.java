package com.bsys.payment.service;

import com.bsys.payment.clients.CustomerClient;
import com.bsys.payment.clients.PaymentClient;
import com.bsys.payment.clients.dto.BillingRequestDTO;
import com.bsys.payment.clients.dto.Customer;
import com.bsys.payment.clients.dto.CustomerClientDTO;
import com.bsys.payment.clients.dto.UpdateCustomerIdDTO;
import com.bsys.payment.clients.dto.successResponse.BillingSuccessResponse;
import com.bsys.payment.clients.dto.successResponse.CustomerSuccessData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentClient paymentClient;
    private final CustomerClient customerClient;

    public Customer createBilling(String userId) {

        Customer currentCustomer = customerClient.getCustomerById(userId).getBody().data();

        if (currentCustomer.customerId() == null) {

            CustomerSuccessData paymentCustomer = paymentClient.createCustomer(new CustomerClientDTO(
                    currentCustomer.email(),
                    currentCustomer.phone(),
                    currentCustomer.email(),
                    currentCustomer.taxId()));

            customerClient.updateCustomerId(userId , new UpdateCustomerIdDTO(
                    paymentCustomer.data().id()
            ));
        }


        return currentCustomer;
    }
}
