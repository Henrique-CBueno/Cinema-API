package com.bsys.tickets.consumer;

import com.bsys.tickets.domain.dto.ReservationPaidConsumerDTO;
import com.bsys.tickets.service.TicketService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class ReservationConsumer {

    private final TicketService ticketService;

    @SqsListener("${sqs.queue.reservation_paid}")
    public void listen(ReservationPaidConsumerDTO message) {

        log.info("message received: {}", message.toString());
        ticketService.generateTicket(message);
    }
}
