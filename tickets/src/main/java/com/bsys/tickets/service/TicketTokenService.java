package com.bsys.tickets.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.bsys.tickets.config.TicketJwtProperties;
import com.bsys.tickets.domain.dto.ReservationPaidConsumerDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TicketTokenService {

    private final TicketJwtProperties properties;

    public String generateToken(ReservationPaidConsumerDTO ticket) {

        Algorithm algorithm = Algorithm.HMAC256(properties.getJwtSecret());

        return JWT.create()
                .withIssuer("ticket-service")
                .withSubject(ticket.reservationId())
                .withClaim("eventId", ticket.reservationId())
                .withIssuedAt(new Date())
                .withExpiresAt(Date.from(
                        Instant.now().plusSeconds(properties.getJwtExpiration())
                ))
                .sign(algorithm);
    }
}
