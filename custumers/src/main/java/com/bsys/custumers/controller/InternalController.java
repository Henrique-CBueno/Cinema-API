package com.bsys.custumers.controller;

import com.bsys.custumers.domain.entity.Customer;
import com.bsys.custumers.infra.padronize.SuccessResponse;
import com.bsys.custumers.service.InternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("internal/customers")
@RequiredArgsConstructor
public class InternalController {

    private final InternalService internalService;

    @GetMapping("{customerId}")
    public ResponseEntity<SuccessResponse<Customer>> getCustomerById(@PathVariable String customerId) {

        Customer customer = internalService.findById(customerId);
        return ResponseEntity.ok(new SuccessResponse<>(customer));
    }
}
