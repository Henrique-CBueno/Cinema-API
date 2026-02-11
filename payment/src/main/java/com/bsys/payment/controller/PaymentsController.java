package com.bsys.payment.controller;

import com.bsys.payment.clients.dto.BillingRequestDTO;
import com.bsys.payment.clients.dto.Customer;
import com.bsys.payment.clients.dto.successResponse.BillingSuccessResponse;
import com.bsys.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class PaymentsController {

    private final PaymentService paymentService;

    @PostMapping("create")
//    public ResponseEntity<Object> createBilling(@RequestBody @Valid BillingRequestDTO dto) {
    public ResponseEntity<Customer> createBilling(@RequestHeader(value = "X-User-Id", required = false) String userId) {

        return ResponseEntity.ok(paymentService.createBilling(userId));
    }
}
