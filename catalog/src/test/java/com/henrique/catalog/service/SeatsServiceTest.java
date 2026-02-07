package com.henrique.catalog.service;

import com.henrique.catalog.domain.dto.req.seat.CreateSeatReqDTO;
import com.henrique.catalog.domain.dto.res.seat.SeatResDTO;
import com.henrique.catalog.domain.dto.res.seat.SeatsExistenceResDTO;
import com.henrique.catalog.domain.entity.RoomEntity;
import com.henrique.catalog.domain.entity.SeatEntity;
import com.henrique.catalog.domain.mapper.SeatMapper;
import com.henrique.catalog.factory.SeatFactory;
import com.henrique.catalog.infra.exceptions.DuplicateResourceException;
import com.henrique.catalog.infra.exceptions.NotFoundException;
import com.henrique.catalog.infra.exceptions.UnprocessableEntityException;
import com.henrique.catalog.repository.SeatsRepository;
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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeatsServiceTest {

        @Mock
        private SeatsRepository seatsRepository;

        @Mock
        private SeatMapper seatMapper;

        @Mock
        private RoomsService roomsService;

        @InjectMocks
        private SeatsService seatsService;

        @Nested
        class GetSeatsByCinemaRoom {

                @Test
                void shouldReturnPageOfSeatsWhenSeatsExist() {
                        // Arrange
                        UUID roomId = UUID.randomUUID();
                        Pageable pageable = Pageable.ofSize(10);

                        RoomEntity roomEntity = SeatFactory.createRoomEntity();
                        SeatEntity seat1 = SeatFactory.createSeatEntity(UUID.randomUUID(), "A", 1, roomEntity);
                        SeatEntity seat2 = SeatFactory.createSeatEntity(UUID.randomUUID(), "A", 2, roomEntity);

                        Page<SeatEntity> entityPage = new PageImpl<>(List.of(seat1, seat2));

                        SeatResDTO dto1 = SeatFactory.createSeatResponseDTO(seat1.getId(), "A", 1);
                        SeatResDTO dto2 = SeatFactory.createSeatResponseDTO(seat2.getId(), "A", 2);

                        when(seatsRepository.findAllByRoomId(roomId, pageable))
                                        .thenReturn(entityPage);
                        when(seatMapper.toDTO(seat1))
                                        .thenReturn(dto1);
                        when(seatMapper.toDTO(seat2))
                                        .thenReturn(dto2);

                        // Act
                        Page<SeatResDTO> result = seatsService.getSeatsByCinemaRoom(roomId, pageable);

                        // Assert
                        assertNotNull(result);
                        assertEquals(2, result.getTotalElements());
                        assertEquals("A", result.getContent().get(0).rowLabel());
                        assertEquals("1", result.getContent().get(0).columnNumber());
                        assertEquals("A", result.getContent().get(1).rowLabel());
                        assertEquals("2", result.getContent().get(1).columnNumber());
                        verify(seatsRepository, times(1)).findAllByRoomId(roomId, pageable);
                        verify(seatMapper, times(2)).toDTO(any(SeatEntity.class));
                }

                @Test
                void shouldReturnEmptyPageWhenNoSeatsExist() {
                        // Arrange
                        UUID roomId = UUID.randomUUID();
                        Pageable pageable = Pageable.ofSize(10);
                        Page<SeatEntity> emptyPage = new PageImpl<>(List.of());

                        when(seatsRepository.findAllByRoomId(roomId, pageable))
                                        .thenReturn(emptyPage);

                        // Act
                        Page<SeatResDTO> result = seatsService.getSeatsByCinemaRoom(roomId, pageable);

                        // Assert
                        assertNotNull(result);
                        assertTrue(result.getContent().isEmpty());
                        assertEquals(0, result.getTotalElements());
                        verify(seatsRepository, times(1)).findAllByRoomId(roomId, pageable);
                        verify(seatMapper, never()).toDTO(any(SeatEntity.class));
                }

                @Test
                void shouldRespectPageableParameters() {
                        // Arrange
                        UUID roomId = UUID.randomUUID();
                        Pageable pageable = Pageable.ofSize(5).withPage(0);

                        RoomEntity roomEntity = SeatFactory.createRoomEntity();
                        List<SeatEntity> seats = List.of(
                                        SeatFactory.createSeatEntity(UUID.randomUUID(), "A", 1, roomEntity),
                                        SeatFactory.createSeatEntity(UUID.randomUUID(), "A", 2, roomEntity),
                                        SeatFactory.createSeatEntity(UUID.randomUUID(), "A", 3, roomEntity),
                                        SeatFactory.createSeatEntity(UUID.randomUUID(), "A", 4, roomEntity),
                                        SeatFactory.createSeatEntity(UUID.randomUUID(), "A", 5, roomEntity));

                        Page<SeatEntity> entityPage = new PageImpl<>(seats, pageable, 10);

                        when(seatsRepository.findAllByRoomId(roomId, pageable))
                                        .thenReturn(entityPage);
                        when(seatMapper.toDTO(any(SeatEntity.class)))
                                        .thenAnswer(invocation -> {
                                                SeatEntity seat = invocation.getArgument(0);
                                                return SeatFactory.createSeatResponseDTO(seat.getId(),
                                                                seat.getRowLabel(),
                                                                seat.getColumnNumber());
                                        });

                        // Act
                        Page<SeatResDTO> result = seatsService.getSeatsByCinemaRoom(roomId, pageable);

                        // Assert
                        assertNotNull(result);
                        assertEquals(5, result.getNumberOfElements());
                        assertEquals(10, result.getTotalElements());
                        assertEquals(0, result.getNumber());
                        verify(seatsRepository, times(1)).findAllByRoomId(roomId, pageable);
                }

                @Test
                void shouldMapAllEntitiesToDTOs() {
                        // Arrange
                        UUID roomId = UUID.randomUUID();
                        Pageable pageable = Pageable.ofSize(10);

                        RoomEntity roomEntity = SeatFactory.createRoomEntity();
                        SeatEntity seat1 = SeatFactory.createSeatEntity(UUID.randomUUID(), "A", 1, roomEntity);
                        SeatEntity seat2 = SeatFactory.createSeatEntity(UUID.randomUUID(), "B", 1, roomEntity);
                        SeatEntity seat3 = SeatFactory.createSeatEntity(UUID.randomUUID(), "C", 1, roomEntity);

                        Page<SeatEntity> entityPage = new PageImpl<>(List.of(seat1, seat2, seat3));

                        when(seatsRepository.findAllByRoomId(roomId, pageable))
                                        .thenReturn(entityPage);
                        when(seatMapper.toDTO(any(SeatEntity.class)))
                                        .thenAnswer(invocation -> {
                                                SeatEntity seat = invocation.getArgument(0);
                                                return SeatFactory.createSeatResponseDTO(seat.getId(),
                                                                seat.getRowLabel(),
                                                                seat.getColumnNumber());
                                        });

                        // Act
                        Page<SeatResDTO> result = seatsService.getSeatsByCinemaRoom(roomId, pageable);

                        // Assert
                        assertNotNull(result);
                        assertEquals(3, result.getTotalElements());
                        verify(seatMapper, times(3)).toDTO(any(SeatEntity.class));
                }

                @Test
                void shouldCallRepositoryWithCorrectParameters() {
                        // Arrange
                        UUID roomId = UUID.randomUUID();
                        Pageable pageable = Pageable.ofSize(20).withPage(1);
                        Page<SeatEntity> emptyPage = new PageImpl<>(List.of());

                        when(seatsRepository.findAllByRoomId(roomId, pageable))
                                        .thenReturn(emptyPage);

                        // Act
                        seatsService.getSeatsByCinemaRoom(roomId, pageable);

                        // Assert
                        verify(seatsRepository, times(1)).findAllByRoomId(roomId, pageable);
                }

                @Test
                void shouldReturnSingleSeatWhenOnlyOneSeatExists() {
                        // Arrange
                        UUID roomId = UUID.randomUUID();
                        Pageable pageable = Pageable.ofSize(10);

                        RoomEntity roomEntity = SeatFactory.createRoomEntity();
                        SeatEntity seat = SeatFactory.createSeatEntity(UUID.randomUUID(), "A", 1, roomEntity);

                        Page<SeatEntity> entityPage = new PageImpl<>(List.of(seat));
                        SeatResDTO dto = SeatFactory.createSeatResponseDTO(seat.getId(), "A", 1);

                        when(seatsRepository.findAllByRoomId(roomId, pageable))
                                        .thenReturn(entityPage);
                        when(seatMapper.toDTO(seat))
                                        .thenReturn(dto);

                        // Act
                        Page<SeatResDTO> result = seatsService.getSeatsByCinemaRoom(roomId, pageable);

                        // Assert
                        assertNotNull(result);
                        assertEquals(1, result.getTotalElements());
                        assertEquals("A", result.getContent().get(0).rowLabel());
                        assertEquals("1", result.getContent().get(0).columnNumber());
                }
        }

        @Nested
        class CreateSeatsInCinemaRoom {

                @Test
                void shouldCreateSeatsSuccessfully() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();
                        UUID userId = UUID.randomUUID();

                        RoomEntity room = SeatFactory.createRoomEntity();
                        room.setId(roomId);
                        room.setTotalRows(10);
                        room.setTotalColumns(15);

                        List<CreateSeatReqDTO> seatsToCreate = List.of(
                                        new CreateSeatReqDTO("A", 1),
                                        new CreateSeatReqDTO("A", 2));

                        when(roomsService.getRoomByCinemaIdAndRoomIdReturningEntity(cinemaId, roomId))
                                        .thenReturn(room);
                        when(seatMapper.toEntity(any(CreateSeatReqDTO.class)))
                                        .thenAnswer(invocation -> {
                                                CreateSeatReqDTO dto = invocation.getArgument(0);
                                                SeatEntity seat = new SeatEntity();
                                                seat.setRowLabel(dto.rowLabel());
                                                seat.setColumnNumber(dto.columnNumber());
                                                return seat;
                                        });

                        // Act
                        seatsService.createSeatsInCinemaRoom(cinemaId, roomId, seatsToCreate, userId);

                        // Assert
                        verify(roomsService, times(1)).getRoomByCinemaIdAndRoomIdReturningEntity(cinemaId, roomId);
                        verify(seatMapper, times(2)).toEntity(any(CreateSeatReqDTO.class));
                        verify(seatsRepository, times(1)).saveAllAndFlush(any());
                }

                @Test
                void shouldThrowUnprocessableEntityExceptionWhenSeatPositionIsInvalid() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();
                        UUID userId = UUID.randomUUID();

                        RoomEntity room = SeatFactory.createRoomEntity();
                        room.setId(roomId);
                        room.setTotalRows(10);
                        room.setTotalColumns(5);

                        List<CreateSeatReqDTO> invalidSeats = List.of(
                                        new CreateSeatReqDTO("A", 20));

                        when(roomsService.getRoomByCinemaIdAndRoomIdReturningEntity(cinemaId, roomId))
                                        .thenReturn(room);

                        // Act & Assert
                        assertThrows(UnprocessableEntityException.class,
                                        () -> seatsService.createSeatsInCinemaRoom(cinemaId, roomId, invalidSeats,
                                                        userId));
                        verify(seatsRepository, never()).saveAllAndFlush(any());
                }

                @Test
                void shouldThrowUnprocessableEntityExceptionWhenRowPositionIsInvalid() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();
                        UUID userId = UUID.randomUUID();

                        RoomEntity room = SeatFactory.createRoomEntity();
                        room.setId(roomId);
                        room.setTotalRows(5);
                        room.setTotalColumns(15);

                        List<CreateSeatReqDTO> invalidSeats = List.of(
                                        new CreateSeatReqDTO("Z", 1));

                        when(roomsService.getRoomByCinemaIdAndRoomIdReturningEntity(cinemaId, roomId))
                                        .thenReturn(room);

                        // Act & Assert
                        assertThrows(UnprocessableEntityException.class,
                                        () -> seatsService.createSeatsInCinemaRoom(cinemaId, roomId, invalidSeats,
                                                        userId));
                }

                @Test
                void shouldThrowDuplicateResourceExceptionWhenSeatAlreadyExists() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();
                        UUID userId = UUID.randomUUID();

                        RoomEntity room = SeatFactory.createRoomEntity();
                        room.setId(roomId);
                        room.setTotalRows(10);
                        room.setTotalColumns(15);

                        List<CreateSeatReqDTO> seats = List.of(
                                        new CreateSeatReqDTO("A", 1));

                        when(roomsService.getRoomByCinemaIdAndRoomIdReturningEntity(cinemaId, roomId))
                                        .thenReturn(room);
                        when(seatMapper.toEntity(any(CreateSeatReqDTO.class)))
                                        .thenAnswer(invocation -> {
                                                CreateSeatReqDTO dto = invocation.getArgument(0);
                                                SeatEntity seat = new SeatEntity();
                                                seat.setRowLabel(dto.rowLabel());
                                                seat.setColumnNumber(dto.columnNumber());
                                                return seat;
                                        });
                        when(seatsRepository.saveAllAndFlush(any()))
                                        .thenThrow(new DataIntegrityViolationException("Duplicate key"));

                        // Act & Assert
                        assertThrows(DuplicateResourceException.class,
                                        () -> seatsService.createSeatsInCinemaRoom(cinemaId, roomId, seats, userId));
                }

                @Test
                void shouldSetUserIdOnCreatedSeats() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();
                        UUID userId = UUID.randomUUID();

                        RoomEntity room = SeatFactory.createRoomEntity();
                        room.setId(roomId);
                        room.setTotalRows(10);
                        room.setTotalColumns(15);

                        List<CreateSeatReqDTO> seats = List.of(
                                        new CreateSeatReqDTO("A", 1));

                        when(roomsService.getRoomByCinemaIdAndRoomIdReturningEntity(cinemaId, roomId))
                                        .thenReturn(room);
                        when(seatMapper.toEntity(any(CreateSeatReqDTO.class)))
                                        .thenAnswer(invocation -> {
                                                CreateSeatReqDTO dto = invocation.getArgument(0);
                                                SeatEntity seat = new SeatEntity();
                                                seat.setRowLabel(dto.rowLabel());
                                                seat.setColumnNumber(dto.columnNumber());
                                                return seat;
                                        });

                        ArgumentCaptor<List<SeatEntity>> seatsCaptor = ArgumentCaptor.forClass(List.class);

                        // Act
                        seatsService.createSeatsInCinemaRoom(cinemaId, roomId, seats, userId);

                        // Assert
                        verify(seatsRepository).saveAllAndFlush(seatsCaptor.capture());
                        List<SeatEntity> capturedSeats = seatsCaptor.getValue();
                        assertTrue(capturedSeats.stream().allMatch(seat -> seat.getCreatedByUserId().equals(userId)));
                }

                @Test
                void shouldSetRoomOnCreatedSeats() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();
                        UUID userId = UUID.randomUUID();

                        RoomEntity room = SeatFactory.createRoomEntity();
                        room.setId(roomId);
                        room.setTotalRows(10);
                        room.setTotalColumns(15);

                        List<CreateSeatReqDTO> seats = List.of(
                                        new CreateSeatReqDTO("A", 1),
                                        new CreateSeatReqDTO("A", 2));

                        when(roomsService.getRoomByCinemaIdAndRoomIdReturningEntity(cinemaId, roomId))
                                        .thenReturn(room);
                        when(seatMapper.toEntity(any(CreateSeatReqDTO.class)))
                                        .thenAnswer(invocation -> {
                                                CreateSeatReqDTO dto = invocation.getArgument(0);
                                                SeatEntity seat = new SeatEntity();
                                                seat.setRowLabel(dto.rowLabel());
                                                seat.setColumnNumber(dto.columnNumber());
                                                return seat;
                                        });

                        ArgumentCaptor<List<SeatEntity>> seatsCaptor = ArgumentCaptor.forClass(List.class);

                        // Act
                        seatsService.createSeatsInCinemaRoom(cinemaId, roomId, seats, userId);

                        // Assert
                        verify(seatsRepository).saveAllAndFlush(seatsCaptor.capture());
                        List<SeatEntity> capturedSeats = seatsCaptor.getValue();
                        assertTrue(capturedSeats.stream().allMatch(seat -> seat.getRoom().equals(room)));
                }
        }

        @Nested
        class SafeDeleteSeat {

                @Test
                void shouldDeleteSeatSuccessfullyWhenAffectedRowsIsGreaterThanZero() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();
                        UUID seatId = UUID.randomUUID();

                        when(seatsRepository.softDeleteById(seatId, roomId, cinemaId))
                                        .thenReturn(1);

                        // Act
                        seatsService.deleteSeatFromRoom(cinemaId, roomId, seatId);

                        // Assert
                        verify(seatsRepository, times(1)).softDeleteById(seatId, roomId, cinemaId);
                }

                @Test
                void shouldThrowNotFoundExceptionWhenAffectedRowsIsZero() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();
                        UUID seatId = UUID.randomUUID();

                        when(seatsRepository.softDeleteById(seatId, roomId, cinemaId))
                                        .thenReturn(0);

                        // Act & Assert
                        assertThrows(NotFoundException.class,
                                        () -> seatsService.deleteSeatFromRoom(cinemaId, roomId, seatId));
                        verify(seatsRepository, times(1)).softDeleteById(seatId, roomId, cinemaId);
                }

                @Test
                void shouldIncludeSeatIdRoomIdAndCinemaIdInExceptionMessage() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();
                        UUID seatId = UUID.randomUUID();

                        when(seatsRepository.softDeleteById(seatId, roomId, cinemaId))
                                        .thenReturn(0);

                        // Act & Assert
                        NotFoundException exception = assertThrows(NotFoundException.class,
                                        () -> seatsService.deleteSeatFromRoom(cinemaId, roomId, seatId));
                        assertTrue(exception.getMessage().contains(seatId.toString()));
                        assertTrue(exception.getMessage().contains(roomId.toString()));
                        assertTrue(exception.getMessage().contains(cinemaId.toString()));
                }

                @Test
                void shouldThrowNotFoundExceptionWhenAffectedRowsIsNegative() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();
                        UUID seatId = UUID.randomUUID();

                        when(seatsRepository.softDeleteById(seatId, roomId, cinemaId))
                                        .thenReturn(-1);

                        // Act & Assert
                        assertThrows(NotFoundException.class,
                                        () -> seatsService.deleteSeatFromRoom(cinemaId, roomId, seatId));
                }
        }

        @Nested
        class ValidateSeatsInRoom {

                @Test
                void shouldReturnAllExistsTrueWhenSeatIdsIsNull() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();

                        // Act
                        SeatsExistenceResDTO result = seatsService.validateSeatsInRoom(cinemaId, roomId, null);

                        // Assert
                        assertTrue(result.allExists());
                        assertNotNull(result.missingSeatIds());
                        assertTrue(result.missingSeatIds().isEmpty());
                        verify(seatsRepository, never()).findExistingSeatIdsInRoom(any(), any(), any());
                }

                @Test
                void shouldReturnAllExistsTrueWhenSeatIdsIsEmpty() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();

                        // Act
                        SeatsExistenceResDTO result = seatsService.validateSeatsInRoom(cinemaId, roomId, List.of());

                        // Assert
                        assertTrue(result.allExists());
                        assertNotNull(result.missingSeatIds());
                        assertTrue(result.missingSeatIds().isEmpty());
                        verify(seatsRepository, never()).findExistingSeatIdsInRoom(any(), any(), any());
                }

                @Test
                void shouldReturnMissingIdsWhenAnySeatDoesNotExist() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();
                        UUID seat1 = UUID.randomUUID();
                        UUID seat2 = UUID.randomUUID();
                        UUID seat3 = UUID.randomUUID();

                        List<UUID> requested = List.of(seat1, seat2, seat3);
                        List<UUID> existing = List.of(seat1, seat3);

                        when(seatsRepository.findExistingSeatIdsInRoom(cinemaId, roomId, requested))
                                        .thenReturn(existing);

                        // Act
                        SeatsExistenceResDTO result = seatsService.validateSeatsInRoom(cinemaId, roomId, requested);

                        // Assert
                        assertFalse(result.allExists());
                        assertEquals(List.of(seat2), result.missingSeatIds());
                }

                @Test
                void shouldReturnAllExistsTrueWhenAllSeatsExist() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();
                        UUID seat1 = UUID.randomUUID();
                        UUID seat2 = UUID.randomUUID();

                        List<UUID> requested = List.of(seat1, seat2);

                        when(seatsRepository.findExistingSeatIdsInRoom(cinemaId, roomId, requested))
                                        .thenReturn(requested);

                        // Act
                        SeatsExistenceResDTO result = seatsService.validateSeatsInRoom(cinemaId, roomId, requested);

                        // Assert
                        assertTrue(result.allExists());
                        assertTrue(result.missingSeatIds().isEmpty());
                }

                @Test
                void shouldReturnDistinctMissingIdsWhenInputHasDuplicates() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();
                        UUID seat1 = UUID.randomUUID();
                        UUID seat2 = UUID.randomUUID();

                        List<UUID> requested = List.of(seat1, seat2, seat2);
                        List<UUID> existing = List.of(seat1);

                        when(seatsRepository.findExistingSeatIdsInRoom(cinemaId, roomId, requested))
                                        .thenReturn(existing);

                        // Act
                        SeatsExistenceResDTO result = seatsService.validateSeatsInRoom(cinemaId, roomId, requested);

                        // Assert
                        assertFalse(result.allExists());
                        assertEquals(List.of(seat2), result.missingSeatIds());
                }
        }
}
