package com.henrique.catalog.domain.mapper;

import com.henrique.catalog.domain.dto.res.movie.MovieResDTO;
import com.henrique.catalog.domain.dto.res.rooms.RoomsResDTO;
import com.henrique.catalog.domain.dto.res.session.SessionResDTO;
import com.henrique.catalog.domain.dto.req.sessions.CreateSessionReqDTO;
import com.henrique.catalog.domain.entity.MovieEntity;
import com.henrique.catalog.domain.entity.RoomEntity;
import com.henrique.catalog.domain.entity.SessionEntity;
import com.henrique.catalog.domain.entity.enums.SessionStatus;
import com.henrique.catalog.factory.MovieFactory;
import com.henrique.catalog.factory.RoomFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionMapperTest {

    private SessionMapper sessionMapper;

    @Mock
    private MovieMapper movieMapper;

    @Mock
    private RoomsMapper roomsMapper;

    @BeforeEach
    void setUp() {
        sessionMapper = org.mapstruct.factory.Mappers.getMapper(SessionMapper.class);
        ReflectionTestUtils.setField(sessionMapper, "movieMapper", movieMapper);
        ReflectionTestUtils.setField(sessionMapper, "roomsMapper", roomsMapper);
    }

    @Nested
    class ToDTO {

        @Test
        void shouldMapEntityToDTO() {
            // Arrange
            UUID sessionId = UUID.randomUUID();
            LocalDateTime startTime = LocalDateTime.of(2026, 2, 4, 14, 0);
            LocalDateTime endTime = LocalDateTime.of(2026, 2, 4, 16, 0);
            BigDecimal price = new BigDecimal("35.50");

            MovieEntity movieEntity = MovieFactory.createMovieEntity(UUID.randomUUID(), "Matrix");
            RoomEntity roomEntity = RoomFactory.createRoomEntity(UUID.randomUUID(), "Sala 1");

            SessionEntity sessionEntity = new SessionEntity();
            sessionEntity.setId(sessionId);
            sessionEntity.setMovie(movieEntity);
            sessionEntity.setRoom(roomEntity);
            sessionEntity.setStartTime(startTime);
            sessionEntity.setEndTime(endTime);
            sessionEntity.setPrice(price);
            sessionEntity.setStatus(SessionStatus.SCHEDULED);

            MovieResDTO movieResDTO = MovieFactory.createMovieResponseDTO(movieEntity.getId(), movieEntity.getTitle());
            RoomsResDTO roomsResDTO = RoomFactory.createRoomsResponseDTO(roomEntity.getId(), roomEntity.getName());

            when(movieMapper.toResponse(movieEntity)).thenReturn(movieResDTO);
            when(roomsMapper.toDTO(roomEntity)).thenReturn(roomsResDTO);

            // Act
            SessionResDTO response = sessionMapper.toDTO(sessionEntity);

            // Assert
            assertNotNull(response);
            assertEquals(sessionId, response.id());
            assertEquals(movieResDTO, response.movie());
            assertEquals(roomsResDTO, response.room());
            assertEquals(roomEntity.getCinema().getId(), response.cinemaId());
            assertEquals(startTime, response.startTime());
            assertEquals(endTime, response.endTime());
            assertEquals(price, response.price());
            assertEquals(SessionStatus.SCHEDULED, response.status());
        }

        @Test
        void shouldReturnNullWhenEntityIsNull() {
            // Arrange
            SessionEntity sessionEntity = null;

            // Act
            SessionResDTO response = sessionMapper.toDTO(sessionEntity);

            // Assert
            assertNull(response);
        }

        @Test
        void shouldMapDifferentStatusesCorrectly() {
            // Arrange
            MovieEntity movieEntity = MovieFactory.createMovieEntity();
            RoomEntity roomEntity = RoomFactory.createRoomEntity();

            SessionEntity sessionEntity = new SessionEntity();
            sessionEntity.setId(UUID.randomUUID());
            sessionEntity.setMovie(movieEntity);
            sessionEntity.setRoom(roomEntity);
            sessionEntity.setStartTime(LocalDateTime.of(2026, 2, 4, 18, 0));
            sessionEntity.setEndTime(LocalDateTime.of(2026, 2, 4, 20, 0));
            sessionEntity.setPrice(new BigDecimal("50.00"));
            sessionEntity.setStatus(SessionStatus.FINISHED);

            MovieResDTO movieResDTO = MovieFactory.createMovieResponseDTO(movieEntity.getId(), movieEntity.getTitle());
            RoomsResDTO roomsResDTO = RoomFactory.createRoomsResponseDTO(roomEntity.getId(), roomEntity.getName());

            when(movieMapper.toResponse(movieEntity)).thenReturn(movieResDTO);
            when(roomsMapper.toDTO(roomEntity)).thenReturn(roomsResDTO);

            // Act
            SessionResDTO response = sessionMapper.toDTO(sessionEntity);

            // Assert
            assertNotNull(response);
            assertEquals(SessionStatus.FINISHED, response.status());
        }
    }

    @Nested
    class ToEntity {

        @Test
        void shouldMapCreateRequestToEntityWithCalculatedFields() {
            // Arrange
            UUID movieId = UUID.randomUUID();
            UUID roomId = UUID.randomUUID();
            UUID cinemaId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            LocalDateTime startTime = LocalDateTime.of(2026, 2, 4, 14, 0);
            BigDecimal price = new BigDecimal("30.00");

            MovieEntity movie = MovieFactory.createMovieEntity(movieId, "Matrix");
            movie.setDurationMinutes(120);
            RoomEntity room = RoomFactory.createRoomEntity(roomId, "Sala 1");

            CreateSessionReqDTO dto = new CreateSessionReqDTO(
                    movieId,
                    roomId,
                    cinemaId,
                    startTime,
                    price
            );

            // Act
            SessionEntity entity = sessionMapper.toEntity(dto, movie, room, userId);

            // Assert
            assertNotNull(entity);
            assertNull(entity.getId());
            assertEquals(movie, entity.getMovie());
            assertEquals(room, entity.getRoom());
            assertEquals(startTime, entity.getStartTime());
            assertEquals(startTime.plusMinutes(movie.getDurationMinutes()), entity.getEndTime());
            assertEquals(price, entity.getPrice());
            assertEquals(SessionStatus.SCHEDULED, entity.getStatus());
            assertEquals(userId, entity.getCreatedByUserId());
        }
    }
}
