package com.henrique.catalog.service;

import com.henrique.catalog.domain.dto.req.sessions.CreateSessionReqDTO;
import com.henrique.catalog.domain.dto.req.sessions.GetAllSessionParamsDTO;
import com.henrique.catalog.domain.dto.res.session.SessionResDTO;
import com.henrique.catalog.domain.entity.MovieEntity;
import com.henrique.catalog.domain.entity.RoomEntity;
import com.henrique.catalog.domain.entity.SessionEntity;
import com.henrique.catalog.domain.mapper.SessionMapper;
import com.henrique.catalog.infra.constants.ExceptionsConstants;
import com.henrique.catalog.infra.exceptions.DuplicateResourceException;
import com.henrique.catalog.infra.exceptions.NotFoundException;
import com.henrique.catalog.repository.SessionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;
    private final MovieService movieService;
    private final RoomsService roomsService;

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

    public SessionResDTO getSessionById(UUID sessionId) {
        return sessionRepository.findById(sessionId)
                .map(sessionMapper::toDTO)
                .orElseThrow(() -> new NotFoundException(String.format(
                        ExceptionsConstants.SESSION_DONT_EXISTS,
                        sessionId
                )));
    }

    @Transactional
    public UUID createNewSession(CreateSessionReqDTO dto,
                                 UUID userId) {

        try {
            MovieEntity movie = movieService.getMovieByIdReturningEntity(dto.movieId());
            RoomEntity room = roomsService.getRoomByCinemaIdAndRoomIdReturningEntity(dto.cinemaId(), dto.roomId());

            SessionEntity sessionEntity = sessionMapper.toEntity(dto,
                                                                movie,
                                                                room,
                                                                userId);

            return sessionRepository.saveAndFlush(sessionEntity).getId();
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException(ExceptionsConstants.SESSION_IN_THIS_TIME);
        }
    }

    public void cancelSession(UUID cinemaId,
                              UUID roomId,
                              UUID sessionId) {

        int affectedRows = sessionRepository.softDeleteById(sessionId, roomId, cinemaId);

        if (affectedRows < 1)
            throw new NotFoundException(String.format(
                    ExceptionsConstants.SESSION_DONT_EXISTS,
                    sessionId
            ));
    }


    public int updateScheduledToInProgress(LocalDateTime now) {
        return sessionRepository.updateScheduledToInProgress(now);
    }

    public int updateInProgressToFinished(LocalDateTime now) {
        return sessionRepository.updateInProgressToFinished(now);
    }
}
