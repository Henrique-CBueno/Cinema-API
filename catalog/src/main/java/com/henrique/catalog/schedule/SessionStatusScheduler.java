package com.henrique.catalog.schedule;

import com.henrique.catalog.service.SessionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class SessionStatusScheduler {

    private final SessionService sessionService;

    @Scheduled(fixedDelay = 6000)
    @Transactional
    public void updateSessionsStatus() {

        log.info("rodou");

        LocalDateTime  now = LocalDateTime.now();

        int started = sessionService.updateScheduledToInProgress(now);
        int finished = sessionService.updateInProgressToFinished(now);

        if (started > 0 || finished > 0) {
            log.info("Sessões atualizadas — IN_PROGRESS: {}, FINISHED: {}", started, finished);
        }
    }
}
