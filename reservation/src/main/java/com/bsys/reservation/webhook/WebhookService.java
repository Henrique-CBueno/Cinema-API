package com.bsys.reservation.webhook;

import com.bsys.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WebhookService {

    private final ReservationRepository reservationRepository;

    @Transactional
    public void paid(String reservationId) {

        reservationRepository.setReservePaid(UUID.fromString(reservationId));
        reservationRepository.setSeatPaid(UUID.fromString(reservationId));
    }
}
