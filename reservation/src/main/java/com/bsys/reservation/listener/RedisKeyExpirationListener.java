package com.bsys.reservation.listener;

import com.bsys.reservation.service.ReservationService;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisKeyExpirationListener implements MessageListener {

    private final ReservationService reservationService;

    @Override
    public void onMessage(Message message, byte[] pattern) {

        String expiredKey = message.toString();

        // cinema:session:{sessionId}:seat:{seatId}
        if (!expiredKey.startsWith("cinema:session:")) {
            return;
        }

        log.warn("Lock expirou: {}", expiredKey);

        handleSeatLockExpiration(expiredKey);
    }

    private void handleSeatLockExpiration(String key) {
        try {
            String[] parts = key.split(":");
            if (parts.length < 5) {
                log.warn("Formato de chave invalido: {}", key);
                return;
            }

            UUID sessionId = UUID.fromString(parts[2]);
            UUID seatId = UUID.fromString(parts[4]);

            boolean updated = reservationService.expirePendingReservation(sessionId, seatId);
            if (updated) {
                log.info("Reserva expirada: sessão={}, assento={}", sessionId, seatId);
            } else {
                log.info("Nenhuma reserva pendente encontrada: sessão={}, assento={}", sessionId, seatId);
            }

        } catch (Exception e) {
            log.error("Erro ao processar expiração do lock: {}", key, e);
        }
    }
}
