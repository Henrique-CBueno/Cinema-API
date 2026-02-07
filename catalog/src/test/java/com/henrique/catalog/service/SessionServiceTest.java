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
import com.henrique.catalog.infra.constants.ExceptionsConstants;
import com.henrique.catalog.infra.exceptions.DuplicateResourceException;
import com.henrique.catalog.infra.exceptions.NotFoundException;
import com.henrique.catalog.repository.SessionRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

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

        @Mock
        private MovieService movieService;

        @Mock
        private RoomsService roomsService;

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

    @Nested
    class GetSessionById {

        @Test
        void shouldReturnSessionWhenExists() {
            // Arrange
            UUID sessionId = UUID.randomUUID();

            MovieEntity movieEntity = MovieFactory.createMovieEntity();
            RoomEntity roomEntity = RoomFactory.createRoomEntity();

            SessionEntity sessionEntity = buildSession(sessionId, movieEntity, roomEntity,
                    LocalDateTime.of(2026, 2, 4, 14, 0),
                    LocalDateTime.of(2026, 2, 4, 16, 0),
                    new BigDecimal("30.00"),
                    SessionStatus.SCHEDULED);

            SessionResDTO dto = new SessionResDTO(sessionId, null, null,
                    sessionEntity.getStartTime(), sessionEntity.getEndTime(),
                    sessionEntity.getPrice(), sessionEntity.getStatus());

            when(sessionRepository.findById(sessionId))
                    .thenReturn(Optional.of(sessionEntity));
            when(sessionMapper.toDTO(sessionEntity))
                    .thenReturn(dto);

            // Act
            SessionResDTO result = sessionService.getSessionById(sessionId);

            // Assert
            assertNotNull(result);
            assertEquals(dto, result);
            verify(sessionRepository, times(1)).findById(sessionId);
            verify(sessionMapper, times(1)).toDTO(sessionEntity);
        }

        @Test
        void shouldThrowNotFoundExceptionWhenSessionDoesNotExist() {
            // Arrange
            UUID sessionId = UUID.randomUUID();

            when(sessionRepository.findById(sessionId))
                    .thenReturn(Optional.empty());

            // Act & Assert
            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> sessionService.getSessionById(sessionId));

            assertEquals(String.format(ExceptionsConstants.SESSION_DONT_EXISTS, sessionId),
                    exception.getMessage());
            verify(sessionRepository, times(1)).findById(sessionId);
            verify(sessionMapper, never()).toDTO(any());
        }
    }

    @Nested
    class CreateNewSession {

        @Test
        void shouldCreateSessionSuccessfully() {
            // Arrange
            UUID userId = UUID.randomUUID();
            UUID movieId = UUID.randomUUID();
            UUID cinemaId = UUID.randomUUID();
            UUID roomId = UUID.randomUUID();
            LocalDateTime startTime = LocalDateTime.of(2026, 2, 4, 14, 0);

            MovieEntity movieEntity = MovieFactory.createMovieEntity(movieId, "Matrix");
            movieEntity.setDurationMinutes(120);
            RoomEntity roomEntity = RoomFactory.createRoomEntity(roomId, "Sala 1");

            var dto = new com.henrique.catalog.domain.dto.req.sessions.CreateSessionReqDTO(
                    movieId,
                    roomId,
                    cinemaId,
                    startTime,
                    new BigDecimal("30.00")
            );

            SessionEntity sessionEntity = new SessionEntity();
            UUID createdId = UUID.randomUUID();
            sessionEntity.setId(createdId);

            when(movieService.getMovieByIdReturningEntity(movieId))
                    .thenReturn(movieEntity);
            when(roomsService.getRoomByCinemaIdAndRoomIdReturningEntity(cinemaId, roomId))
                    .thenReturn(roomEntity);
            when(sessionMapper.toEntity(dto, movieEntity, roomEntity, userId))
                    .thenReturn(sessionEntity);
            when(sessionRepository.saveAndFlush(sessionEntity))
                    .thenReturn(sessionEntity);

            // Act
            UUID result = sessionService.createNewSession(dto, userId);

            // Assert
            assertNotNull(result);
            assertEquals(createdId, result);
            verify(movieService, times(1)).getMovieByIdReturningEntity(movieId);
            verify(roomsService, times(1)).getRoomByCinemaIdAndRoomIdReturningEntity(cinemaId, roomId);
            verify(sessionMapper, times(1)).toEntity(dto, movieEntity, roomEntity, userId);
            verify(sessionRepository, times(1)).saveAndFlush(sessionEntity);
        }

        @Test
        void shouldThrowDuplicateResourceExceptionWhenSessionTimeConflicts() {
            // Arrange
            UUID userId = UUID.randomUUID();
            UUID movieId = UUID.randomUUID();
            UUID cinemaId = UUID.randomUUID();
            UUID roomId = UUID.randomUUID();

            MovieEntity movieEntity = MovieFactory.createMovieEntity(movieId, "Matrix");
            RoomEntity roomEntity = RoomFactory.createRoomEntity(roomId, "Sala 1");

            var dto = new com.henrique.catalog.domain.dto.req.sessions.CreateSessionReqDTO(
                    movieId,
                    roomId,
                    cinemaId,
                    LocalDateTime.of(2026, 2, 4, 14, 0),
                    new BigDecimal("30.00")
            );

            SessionEntity sessionEntity = new SessionEntity();

            when(movieService.getMovieByIdReturningEntity(movieId))
                    .thenReturn(movieEntity);
            when(roomsService.getRoomByCinemaIdAndRoomIdReturningEntity(cinemaId, roomId))
                    .thenReturn(roomEntity);
            when(sessionMapper.toEntity(dto, movieEntity, roomEntity, userId))
                    .thenReturn(sessionEntity);
            when(sessionRepository.saveAndFlush(sessionEntity))
                    .thenThrow(new DataIntegrityViolationException("Duplicate"));

            // Act & Assert
            DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
                    () -> sessionService.createNewSession(dto, userId));

            assertEquals(ExceptionsConstants.SESSION_IN_THIS_TIME, exception.getMessage());
            verify(sessionRepository, times(1)).saveAndFlush(sessionEntity);
        }
    }

    @Nested
    class SafeDeleteSession {

        @Test
        void shouldDeleteSessionSuccessfullyWhenAffectedRowsIsGreaterThanZero() {
            // Arrange
            UUID cinemaId = UUID.randomUUID();
            UUID roomId = UUID.randomUUID();
            UUID sessionId = UUID.randomUUID();

            when(sessionRepository.softDeleteById(sessionId, roomId, cinemaId))
                    .thenReturn(1);

            // Act
            sessionService.cancelSession(cinemaId, roomId, sessionId);

            // Assert
            verify(sessionRepository, times(1)).softDeleteById(sessionId, roomId, cinemaId);
        }

        @Test
        void shouldThrowNotFoundExceptionWhenAffectedRowsIsZero() {
            // Arrange
            UUID cinemaId = UUID.randomUUID();
            UUID roomId = UUID.randomUUID();
            UUID sessionId = UUID.randomUUID();

            when(sessionRepository.softDeleteById(sessionId, roomId, cinemaId))
                    .thenReturn(0);

            // Act & Assert
            assertThrows(NotFoundException.class,
                    () -> sessionService.cancelSession(cinemaId, roomId, sessionId));
            verify(sessionRepository, times(1)).softDeleteById(sessionId, roomId, cinemaId);
        }

        @Test
        void shouldIncludeSessionIdInExceptionMessage() {
            // Arrange
            UUID cinemaId = UUID.randomUUID();
            UUID roomId = UUID.randomUUID();
            UUID sessionId = UUID.randomUUID();

            when(sessionRepository.softDeleteById(sessionId, roomId, cinemaId))
                    .thenReturn(0);

            // Act & Assert
            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> sessionService.cancelSession(cinemaId, roomId, sessionId));
            assertTrue(exception.getMessage().contains(sessionId.toString()));
        }

        @Test
        void shouldThrowNotFoundExceptionWhenAffectedRowsIsNegative() {
            // Arrange
            UUID cinemaId = UUID.randomUUID();
            UUID roomId = UUID.randomUUID();
            UUID sessionId = UUID.randomUUID();

            when(sessionRepository.softDeleteById(sessionId, roomId, cinemaId))
                    .thenReturn(-1);

            // Act & Assert
            assertThrows(NotFoundException.class,
                    () -> sessionService.cancelSession(cinemaId, roomId, sessionId));
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
