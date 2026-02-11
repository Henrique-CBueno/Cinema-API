package com.bsys.payment.controller;

import com.bsys.payment.clients.dto.BillingRequestDTO;
import com.bsys.payment.clients.dto.Customer;
import com.bsys.payment.clients.dto.successResponse.BillingSuccessResponse;
import com.bsys.payment.domain.dto.reservation.req.ReservationReqDTO;
import com.bsys.payment.infra.padronize.SuccessResponse;
import com.bsys.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("internal")
@RequiredArgsConstructor
public class PaymentsController {

    private final PaymentService paymentService;

    @PostMapping("create")
    public ResponseEntity<SuccessResponse<BillingSuccessResponse>> createBilling(@RequestBody @Valid ReservationReqDTO reservationDTO,
                                                         @RequestHeader(value = "X-User-Id", required = false) String userId) {

        BillingSuccessResponse billing = paymentService.createBilling(userId, reservationDTO);
        return ResponseEntity.ok(new SuccessResponse<>(billing));
    }
}
