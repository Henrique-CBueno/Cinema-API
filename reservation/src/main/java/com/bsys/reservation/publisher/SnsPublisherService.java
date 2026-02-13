package com.bsys.reservation.publisher;

import com.bsys.reservation.publisher.dto.ReservationPaidConsumerDTO;
import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SnsPublisherService {

    private final SnsTemplate snsTemplate;

    public void sendMessage(String exchangeName, ReservationPaidConsumerDTO<?> reservationPaidConsumerDTO) {

        log.info("name exchange {}", exchangeName);
        snsTemplate.convertAndSend(exchangeName, reservationPaidConsumerDTO.data());
    }
}
