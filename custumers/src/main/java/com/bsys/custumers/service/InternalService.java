package com.bsys.custumers.service;

import com.bsys.custumers.domain.entity.Customer;
import com.bsys.custumers.infra.constants.ExceptionsConstants;
import com.bsys.custumers.infra.exceptions.NotFoundException;
import com.bsys.custumers.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InternalService {

    private final CustomerRepository customerRepository;

    public Customer findById(String customerId) {
        return customerRepository.findByKeycloakUserId(customerId).orElseThrow(
                () -> new NotFoundException(
                        String.format(
                                ExceptionsConstants.CUSTOMER_NOT_FOUND,
                                customerId
                        )
                )
        );
    }
}
