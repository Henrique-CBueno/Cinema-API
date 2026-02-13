package com.bsys.payment.service;

import com.bsys.payment.clients.CustomerClient;
import com.bsys.payment.clients.PaymentClient;
import com.bsys.payment.clients.dto.BillingRequestDTO;
import com.bsys.payment.clients.dto.Customer;
import com.bsys.payment.clients.dto.CustomerClientDTO;
import com.bsys.payment.clients.dto.UpdateCustomerIdDTO;
import com.bsys.payment.clients.dto.enums.PaymentsMethods;
import com.bsys.payment.clients.dto.successResponse.BillingSuccessResponse;
import com.bsys.payment.clients.dto.successResponse.CustomerSuccessData;
import com.bsys.payment.domain.dto.reservation.req.ReservationReqDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentClient paymentClient;
    private final CustomerClient customerClient;

    public BillingSuccessResponse createBilling(String userId, ReservationReqDTO reservationDTO) {

        Customer currentCustomer = customerClient.getCustomerById(userId).getBody().data();

        if (currentCustomer.getCustomerId() == null) {

            CustomerSuccessData paymentCustomer = paymentClient.createCustomer(new CustomerClientDTO(
                    currentCustomer.getEmail(),
                    currentCustomer.getPhone(),
                    currentCustomer.getEmail(),
                    currentCustomer.getTaxId()));

            if (paymentCustomer.data() == null) throw new RuntimeException(paymentCustomer.toString());

            customerClient.updateCustomerId(userId , new UpdateCustomerIdDTO(
                    paymentCustomer.data().id()
            ));

            currentCustomer.setCustomerId(paymentCustomer.data().id());
        }

        BillingRequestDTO cobranca = getBillingRequestDTO(currentCustomer, reservationDTO);

        return paymentClient.createBilling(cobranca);
    }


    private static @NonNull BillingRequestDTO getBillingRequestDTO(Customer currentCustomer, ReservationReqDTO reservationDTO) {
        return new BillingRequestDTO(
                "ONE_TIME",
                List.of(PaymentsMethods.PIX),
                List.of(getProduct(reservationDTO)),
                60,
                "https://henriquebueno.com",
                "https://henriquebueno.com",
                currentCustomer.getCustomerId(),
                reservationDTO.reservationId()
        );
    }

    private static BillingRequestDTO.@NonNull Product getProduct(ReservationReqDTO reservationDTO) {
        return new BillingRequestDTO.Product(
                reservationDTO.reservationId().toString(),
                "reserva do filme" + reservationDTO.movieTitle(),
                "Filme começa as " + reservationDTO.startTime().toString() + " e termina as" + reservationDTO.endTime().toString(),
                1,
                getIntPriceInCents(reservationDTO)
        );
    }


    private static int getIntPriceInCents(ReservationReqDTO reservationDTO) {
        return reservationDTO.price().multiply(new BigDecimal("100"))
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();
    }
}
