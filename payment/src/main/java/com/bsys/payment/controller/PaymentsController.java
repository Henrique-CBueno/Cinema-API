package com.bsys.payment.controller;

import com.bsys.payment.clients.dto.BillingRequestDTO;
import com.bsys.payment.clients.dto.successResponse.BillingSuccessResponse;
import com.bsys.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class PaymentsController {

    private final PaymentService paymentService;

    @PostMapping("create")
    public ResponseEntity<Object> createBilling(@RequestBody @Valid BillingRequestDTO dto) {

        return ResponseEntity.ok(paymentService.createBilling(dto));
    }
}
