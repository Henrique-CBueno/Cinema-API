package com.bsys.reservation.clients.customer;

import com.bsys.reservation.infra.padronize.SuccessResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer")
public interface CustomerClient {

    @GetMapping("customer/internal/customers/{customerId}")
    ResponseEntity<SuccessResponse<Customer>> getCustomerById(@PathVariable String customerId);
}
