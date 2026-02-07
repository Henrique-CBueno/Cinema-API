package com.bsys.reservation.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RedisKeyExpirationListener implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {

        String expiredKey = message.toString();

        // cinema:session:12:seat:A5
        if (!expiredKey.startsWith("cinema:session:")) {
            return;
        }

        log.warn("Lock expirou: {}", expiredKey);

        handleSeatLockExpiration(expiredKey);
    }

    private void handleSeatLockExpiration(String key) {
        try {
            String[] parts = key.split(":");
            Long sessionId = Long.valueOf(parts[2]);
            String seatNumber = parts[4];

            // AQUI EU TENHO QUE COLOCAR QUE O PAGAMENTO FALHOU

            log.info("Assento liberado automaticamente: sessão={}, assento={}",
                    sessionId, seatNumber);

        } catch (Exception e) {
            log.error("Erro ao processar expiração do lock: {}", key, e);
        }
    }
}
