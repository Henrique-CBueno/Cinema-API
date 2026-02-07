package com.bsys.reservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${cinema.reservation.lock-timeout:300}")
    private long lockTimeoutSeconds;

    public String lockSeat(UUID sessionId, String seatNumber, String userId) {

        String key = buildSeatKey(sessionId, seatNumber);
        String lockToken = UUID.randomUUID().toString();

        Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(key, lockToken, Duration.ofSeconds(lockTimeoutSeconds));

        if (Boolean.TRUE.equals(locked)) {
            log.info("Assento bloqueado: sessão={}, assento={}, usuário={}, token={}",
                    sessionId, seatNumber, userId, lockToken);
            return lockToken;
        }

        log.warn("Assento já bloqueado: sessão={}, assento={}", sessionId, seatNumber);
        return null;
    }

    /**
     * Tenta fazer lock de múltiplos assentos
     */
    public boolean lockSeats(UUID sessionId, List<String> seatNumbers, String userId) {
        String lockToken = UUID.randomUUID().toString();

        for (String seatNumber : seatNumbers) {
            String key = buildSeatKey(sessionId, seatNumber);
            Boolean locked = redisTemplate.opsForValue()
                    .setIfAbsent(key, lockToken, Duration.ofSeconds(lockTimeoutSeconds));

            if (!Boolean.TRUE.equals(locked)) {
                unlockSeats(sessionId, seatNumbers.subList(0, seatNumbers.indexOf(seatNumber)), lockToken);
                return false;
            }
        }

        log.info("Assentos bloqueados: sessão={}, assentos={}, token={}",
                sessionId, seatNumbers, lockToken);
        return true;
    }

    /**
     * Libera o lock de um assento (após pagamento confirmado ou cancelamento)
     */
    public void unlockSeat(UUID sessionId, String seatNumber, String lockToken) {
        String key = buildSeatKey(sessionId, seatNumber);

        // Verifica se o token bate antes de deletar (segurança)
        String currentToken = (String) redisTemplate.opsForValue().get(key);
        if (lockToken.equals(currentToken)) {
            redisTemplate.delete(key);
            log.info("Assento liberado: sessão={}, assento={}", sessionId, seatNumber);
        }
    }

    /**
     * Libera múltiplos assentos
     */
    public void unlockSeats(UUID sessionId, List<String> seatNumbers, String lockToken) {
        seatNumbers.forEach(seatNumber -> unlockSeat(sessionId, seatNumber, lockToken));
    }

    /**
     * Verifica se um assento está bloqueado
     */
    public boolean isSeatLocked(UUID sessionId, String seatNumber) {
        String key = buildSeatKey(sessionId, seatNumber);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * Renova o tempo de expiração do lock (útil para processos longos)
     */
    public boolean renewLock(UUID sessionId, String seatNumber, String lockToken) {
        String key = buildSeatKey(sessionId, seatNumber);
        String currentToken = (String) redisTemplate.opsForValue().get(key);

        if (lockToken.equals(currentToken)) {
            redisTemplate.expire(key, lockTimeoutSeconds, TimeUnit.SECONDS);
            log.info("Lock renovado: sessão={}, assento={}", sessionId, seatNumber);
            return true;
        }
        return false;
    }

    /**
     * Obtém o tempo restante do lock em segundos
     */
    public Long getLockRemainingTime(UUID sessionId, String seatNumber) {
        String key = buildSeatKey(sessionId, seatNumber);
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    private String buildSeatKey(UUID sessionId, String seatNumber) {
        return String.format("cinema:session:%s:seat:%s", sessionId, seatNumber);
    }
}
