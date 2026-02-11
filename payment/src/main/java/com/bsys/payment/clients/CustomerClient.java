package com.bsys.payment.clients;

import com.bsys.payment.clients.dto.Customer;
import com.bsys.payment.clients.dto.UpdateCustomerIdDTO;
import com.bsys.payment.infra.padronize.SuccessResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "customer")
public interface CustomerClient {

    @GetMapping("/customer/internal/customers/{customerId}")
    ResponseEntity<SuccessResponse<Customer>> getCustomerById(@PathVariable String customerId);

    @PostMapping("/customer/internal/customers/updateCustomerId/{userId}")
    ResponseEntity<Void> updateCustomerId(@PathVariable String userId,
            @RequestBody UpdateCustomerIdDTO dto);
}
