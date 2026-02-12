package com.bsys.reservation.publisher;

import com.bsys.reservation.publisher.dto.ReservationPaidConsumerDTO;
import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SnsPublisherService {

    @Value("${sns.exchanges.reservation_paid}")
    private String exchangeName;

    private final SnsTemplate snsTemplate;

    public void sendMessage() {

        snsTemplate.convertAndSend(exchangeName, new ReservationPaidConsumerDTO("teste"));
    }
}
