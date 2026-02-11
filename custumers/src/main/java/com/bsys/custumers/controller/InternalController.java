package com.bsys.custumers.controller;

import com.bsys.custumers.domain.dto.req.UpdateCustomerIdDTO;
import com.bsys.custumers.domain.entity.Customer;
import com.bsys.custumers.infra.padronize.SuccessResponse;
import com.bsys.custumers.service.InternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("updateCustomerId/{userId}")
    public ResponseEntity<Void> updateCustomerId(@PathVariable String userId,
                                                 @RequestBody UpdateCustomerIdDTO dto) {

        internalService.updateCustomerId(dto, userId);
        return ResponseEntity.ok().build();
    }
}
