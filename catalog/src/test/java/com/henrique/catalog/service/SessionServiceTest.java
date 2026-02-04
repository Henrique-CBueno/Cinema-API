package com.henrique.catalog.service;

import com.henrique.catalog.domain.dto.req.sessions.GetAllSessionParamsDTO;
import com.henrique.catalog.domain.dto.res.session.SessionResDTO;
import com.henrique.catalog.domain.entity.MovieEntity;
import com.henrique.catalog.domain.entity.RoomEntity;
import com.henrique.catalog.domain.entity.SessionEntity;
import com.henrique.catalog.domain.entity.enums.SessionStatus;
import com.henrique.catalog.domain.mapper.SessionMapper;
import com.henrique.catalog.factory.MovieFactory;
import com.henrique.catalog.factory.RoomFactory;
import com.henrique.catalog.repository.SessionRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private SessionMapper sessionMapper;

    @InjectMocks
    private SessionService sessionService;

    @Nested
    class GetSessions {

        @Test
        void shouldReturnPageOfSessionsWhenSessionsExist() {
            // Arrange
            Pageable pageable = Pageable.ofSize(10);
            GetAllSessionParamsDTO params = new GetAllSessionParamsDTO(null, null, null, null);

            MovieEntity movieEntity = MovieFactory.createMovieEntity();
            RoomEntity roomEntity = RoomFactory.createRoomEntity();

            SessionEntity session1 = buildSession(UUID.randomUUID(), movieEntity, roomEntity,
                    LocalDateTime.of(2026, 2, 4, 14, 0),
                    LocalDateTime.of(2026, 2, 4, 16, 0),
                    new BigDecimal("30.00"),
                    SessionStatus.SCHEDULED);

            SessionEntity session2 = buildSession(UUID.randomUUID(), movieEntity, roomEntity,
                    LocalDateTime.of(2026, 2, 4, 18, 0),
                    LocalDateTime.of(2026, 2, 4, 20, 0),
                    new BigDecimal("40.00"),
                    SessionStatus.SCHEDULED);

            Page<SessionEntity> entityPage = new PageImpl<>(List.of(session1, session2));

            SessionResDTO dto1 = new SessionResDTO(session1.getId(), null, null,
                    session1.getStartTime(), session1.getEndTime(), session1.getPrice(), session1.getStatus());
            SessionResDTO dto2 = new SessionResDTO(session2.getId(), null, null,
                    session2.getStartTime(), session2.getEndTime(), session2.getPrice(), session2.getStatus());

            when(sessionRepository.findSessionsWithFilters(any(), any(), any(), any(), any(), any(), any()))
                    .thenReturn(entityPage);
            when(sessionMapper.toDTO(session1)).thenReturn(dto1);
            when(sessionMapper.toDTO(session2)).thenReturn(dto2);

            // Act
            Page<SessionResDTO> result = sessionService.getSessions(pageable, params);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.getTotalElements());
            assertEquals(dto1, result.getContent().getFirst());
            verify(sessionRepository, times(1)).findSessionsWithFilters(
                    eq(null), eq(null), eq(null), eq(null), eq(null), eq(null), eq(pageable));
            verify(sessionMapper, times(2)).toDTO(any(SessionEntity.class));
        }

        @Test
        void shouldReturnEmptyPageWhenNoSessionsExist() {
            // Arrange
            Pageable pageable = Pageable.ofSize(10);
            GetAllSessionParamsDTO params = new GetAllSessionParamsDTO(null, null, null, null);

            Page<SessionEntity> emptyPage = new PageImpl<>(List.of());

            when(sessionRepository.findSessionsWithFilters(any(), any(), any(), any(), any(), any(), any()))
                    .thenReturn(emptyPage);

            // Act
            Page<SessionResDTO> result = sessionService.getSessions(pageable, params);

            // Assert
            assertNotNull(result);
            assertTrue(result.getContent().isEmpty());
            verify(sessionRepository, times(1)).findSessionsWithFilters(
                    eq(null), eq(null), eq(null), eq(null), eq(null), eq(null), eq(pageable));
            verify(sessionMapper, never()).toDTO(any());
        }

        @Test
        void shouldCalculateStartAndEndOfDayWhenDateProvided() {
            // Arrange
            Pageable pageable = Pageable.ofSize(10);
            UUID movieId = UUID.randomUUID();
            UUID cinemaId = UUID.randomUUID();
            UUID roomId = UUID.randomUUID();
            LocalDate date = LocalDate.of(2026, 2, 4);

            GetAllSessionParamsDTO params = new GetAllSessionParamsDTO(movieId, cinemaId, roomId, date);
            Page<SessionEntity> emptyPage = new PageImpl<>(List.of());

            when(sessionRepository.findSessionsWithFilters(any(), any(), any(), any(), any(), any(), any()))
                    .thenReturn(emptyPage);

            ArgumentCaptor<LocalDateTime> startCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
            ArgumentCaptor<LocalDateTime> endCaptor = ArgumentCaptor.forClass(LocalDateTime.class);

            // Act
            sessionService.getSessions(pageable, params);

            // Assert
            verify(sessionRepository, times(1)).findSessionsWithFilters(
                    eq(movieId), eq(cinemaId), eq(roomId), eq(date),
                    startCaptor.capture(), endCaptor.capture(), eq(pageable));

            assertEquals(date.atStartOfDay(), startCaptor.getValue());
            assertEquals(date.atTime(LocalTime.MAX), endCaptor.getValue());
        }

        @Test
        void shouldPassNullStartAndEndOfDayWhenDateIsNull() {
            // Arrange
            Pageable pageable = Pageable.ofSize(10);
            GetAllSessionParamsDTO params = new GetAllSessionParamsDTO(null, null, null, null);

            Page<SessionEntity> emptyPage = new PageImpl<>(List.of());

            when(sessionRepository.findSessionsWithFilters(any(), any(), any(), any(), any(), any(), any()))
                    .thenReturn(emptyPage);

            ArgumentCaptor<LocalDateTime> startCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
            ArgumentCaptor<LocalDateTime> endCaptor = ArgumentCaptor.forClass(LocalDateTime.class);

            // Act
            sessionService.getSessions(pageable, params);

            // Assert
            verify(sessionRepository, times(1)).findSessionsWithFilters(
                    eq(null), eq(null), eq(null), eq(null),
                    startCaptor.capture(), endCaptor.capture(), eq(pageable));

            assertNull(startCaptor.getValue());
            assertNull(endCaptor.getValue());
        }
    }

    private SessionEntity buildSession(UUID id,
            MovieEntity movie,
            RoomEntity room,
            LocalDateTime start,
            LocalDateTime end,
            BigDecimal price,
            SessionStatus status) {
        SessionEntity session = new SessionEntity();
        session.setId(id);
        session.setMovie(movie);
        session.setRoom(room);
        session.setStartTime(start);
        session.setEndTime(end);
        session.setPrice(price);
        session.setStatus(status);
        return session;
    }
}
