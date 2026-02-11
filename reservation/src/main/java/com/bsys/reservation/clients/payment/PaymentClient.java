package com.bsys.reservation.clients.payment;

import com.bsys.reservation.clients.payment.dto.BillingSuccessResponse;
import com.bsys.reservation.clients.payment.dto.ReservationReqDTO;
import com.bsys.reservation.infra.padronize.SuccessResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "payment")
public interface PaymentClient {

    @PostMapping("payment/internal/create")
    ResponseEntity<SuccessResponse<BillingSuccessResponse>> createBilling(@RequestBody @Valid ReservationReqDTO reservationDTO,
                                                                          @RequestHeader(value = "X-User-Id", required = false) String userId);
}
