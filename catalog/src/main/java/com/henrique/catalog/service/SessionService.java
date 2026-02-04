package com.henrique.catalog.service;

import com.henrique.catalog.domain.dto.req.sessions.GetAllSessionParamsDTO;
import com.henrique.catalog.domain.dto.res.session.SessionResDTO;
import com.henrique.catalog.domain.mapper.SessionMapper;
import com.henrique.catalog.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;

    public Page<SessionResDTO> getSessions(Pageable pageable,
                                           GetAllSessionParamsDTO getAllSessionParamsDTO) {

        LocalDateTime startOfDay = null;
        LocalDateTime endOfDay = null;

        if (getAllSessionParamsDTO.date() != null) {
            startOfDay = getAllSessionParamsDTO.date().atStartOfDay(); // 00:00:00
            endOfDay = getAllSessionParamsDTO.date().atTime(LocalTime.MAX); // 23:59:59.999
        }

        return sessionRepository.findSessionsWithFilters(
                getAllSessionParamsDTO.movieId(),
                getAllSessionParamsDTO.cinemaId(),
                getAllSessionParamsDTO.roomId(),
                getAllSessionParamsDTO.date(),
                startOfDay,
                endOfDay,
                pageable)
                .map(sessionMapper::toDTO);
    }
}
