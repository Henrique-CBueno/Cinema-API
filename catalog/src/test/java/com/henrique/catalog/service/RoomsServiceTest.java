package com.henrique.catalog.service;

import com.henrique.catalog.domain.dto.res.rooms.RoomsResDTO;
import com.henrique.catalog.domain.entity.CinemaEntity;
import com.henrique.catalog.domain.entity.RoomEntity;
import com.henrique.catalog.domain.mapper.CinemaMapper;
import com.henrique.catalog.domain.mapper.RoomsMapper;
import com.henrique.catalog.factory.RoomFactory;
import com.henrique.catalog.repository.RoomsRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomsServiceTest {

    @Mock
    private RoomsRepository roomsRepository;

    @Mock
    private CinemaMapper cinemaMapper;

    @Mock
    private RoomsMapper roomsMapper;

    @InjectMocks
    private RoomsService roomsService;

    @Nested
    class GetAllRooms {

        @Test
        void shouldReturnPageOfRoomsWhenRoomsExist() {
            // Arrange
            UUID cinemaId = UUID.randomUUID();
            Pageable pageable = Pageable.ofSize(10);
            
            CinemaEntity cinemaEntity = RoomFactory.createCinemaEntity();
            RoomEntity room1 = RoomFactory.createRoomEntity(UUID.randomUUID(), "Sala 1", cinemaEntity);
            RoomEntity room2 = RoomFactory.createRoomEntity(UUID.randomUUID(), "Sala 2", cinemaEntity);
            
            Page<RoomEntity> entityPage = new PageImpl<>(List.of(room1, room2));
            
            RoomsResDTO dto1 = RoomFactory.createRoomsResponseDTO(room1.getId(), "Sala 1");
            RoomsResDTO dto2 = RoomFactory.createRoomsResponseDTO(room2.getId(), "Sala 2");
            
            when(roomsRepository.findByCinemaId(cinemaId, pageable))
                    .thenReturn(entityPage);
            when(roomsMapper.toDTO(room1))
                    .thenReturn(dto1);
            when(roomsMapper.toDTO(room2))
                    .thenReturn(dto2);

            // Act
            Page<RoomsResDTO> result = roomsService.getAllRooms(pageable, cinemaId);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.getTotalElements());
            assertEquals("Sala 1", result.getContent().get(0).name());
            assertEquals("Sala 2", result.getContent().get(1).name());
            verify(roomsRepository, times(1)).findByCinemaId(cinemaId, pageable);
            verify(roomsMapper, times(2)).toDTO(any(RoomEntity.class));
        }

        @Test
        void shouldReturnEmptyPageWhenNoRoomsExist() {
            // Arrange
            UUID cinemaId = UUID.randomUUID();
            Pageable pageable = Pageable.ofSize(10);
            Page<RoomEntity> emptyPage = new PageImpl<>(List.of());
            
            when(roomsRepository.findByCinemaId(cinemaId, pageable))
                    .thenReturn(emptyPage);

            // Act
            Page<RoomsResDTO> result = roomsService.getAllRooms(pageable, cinemaId);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getTotalElements());
            assertTrue(result.getContent().isEmpty());
            verify(roomsRepository, times(1)).findByCinemaId(cinemaId, pageable);
            verify(roomsMapper, never()).toDTO(any());
        }

        @Test
        void shouldUseCorrectPaginationParameters() {
            // Arrange
            UUID cinemaId = UUID.randomUUID();
            Pageable pageable = Pageable.ofSize(20).withPage(1);
            Page<RoomEntity> emptyPage = new PageImpl<>(List.of());
            
            when(roomsRepository.findByCinemaId(cinemaId, pageable))
                    .thenReturn(emptyPage);

            // Act
            roomsService.getAllRooms(pageable, cinemaId);

            // Assert
            verify(roomsRepository, times(1)).findByCinemaId(eq(cinemaId), eq(pageable));
        }

        @Test
        void shouldReturnSingleRoomCorrectly() {
            // Arrange
            UUID cinemaId = UUID.randomUUID();
            Pageable pageable = Pageable.ofSize(10);
            
            CinemaEntity cinemaEntity = RoomFactory.createCinemaEntity();
            RoomEntity room = RoomFactory.createRoomEntity(UUID.randomUUID(), "Sala Premium", cinemaEntity);
            
            Page<RoomEntity> entityPage = new PageImpl<>(List.of(room));
            
            RoomsResDTO dto = RoomFactory.createRoomsResponseDTO(room.getId(), "Sala Premium");
            
            when(roomsRepository.findByCinemaId(cinemaId, pageable))
                    .thenReturn(entityPage);
            when(roomsMapper.toDTO(room))
                    .thenReturn(dto);

            // Act
            Page<RoomsResDTO> result = roomsService.getAllRooms(pageable, cinemaId);

            // Assert
            assertEquals(1, result.getTotalElements());
            assertEquals("Sala Premium", result.getContent().get(0).name());
            assertEquals(10, result.getContent().get(0).totalRows());
            assertEquals(15, result.getContent().get(0).totalColumns());
        }

        @Test
        void shouldCallRepositoryWithCorrectCinemaId() {
            // Arrange
            UUID cinemaId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
            Pageable pageable = Pageable.ofSize(10);
            Page<RoomEntity> emptyPage = new PageImpl<>(List.of());
            
            when(roomsRepository.findByCinemaId(cinemaId, pageable))
                    .thenReturn(emptyPage);

            // Act
            roomsService.getAllRooms(pageable, cinemaId);

            // Assert
            verify(roomsRepository, times(1)).findByCinemaId(
                    eq(UUID.fromString("550e8400-e29b-41d4-a716-446655440000")),
                    any()
            );
        }
    }

    @Nested
    class GetRoomByCinemaIdAndRoomId {

        @Test
        void shouldReturnRoomWhenExists() {
            // Arrange
            UUID cinemaId = UUID.randomUUID();
            UUID roomId = UUID.randomUUID();
            
            CinemaEntity cinemaEntity = RoomFactory.createCinemaEntity();
            RoomEntity roomEntity = RoomFactory.createRoomEntity(roomId, "Sala VIP", cinemaEntity);
            
            when(roomsRepository.findByCinemaIdAndId(cinemaId, roomId))
                    .thenReturn(java.util.Optional.of(roomEntity));
            when(roomsMapper.toDTO(roomEntity))
                    .thenReturn(RoomFactory.createRoomsResponseDTO(roomId, "Sala VIP"));

            // Act
            RoomsResDTO result = roomsService.getRoomByCinemaIdAndRoomId(cinemaId, roomId);

            // Assert
            assertNotNull(result);
            assertEquals(roomId, result.id());
            assertEquals("Sala VIP", result.name());
            verify(roomsRepository, times(1)).findByCinemaIdAndId(cinemaId, roomId);
            verify(roomsMapper, times(1)).toDTO(roomEntity);
        }

        @Test
        void shouldThrowNotFoundExceptionWhenRoomDoesNotExist() {
            // Arrange
            UUID cinemaId = UUID.randomUUID();
            UUID roomId = UUID.randomUUID();
            
            when(roomsRepository.findByCinemaIdAndId(cinemaId, roomId))
                    .thenReturn(java.util.Optional.empty());

            // Act & Assert
            com.henrique.catalog.infra.exceptions.NotFoundException exception = 
                    assertThrows(com.henrique.catalog.infra.exceptions.NotFoundException.class,
                            () -> roomsService.getRoomByCinemaIdAndRoomId(cinemaId, roomId));
            
            assertTrue(exception.getMessage().contains(roomId.toString()));
            assertTrue(exception.getMessage().contains(cinemaId.toString()));
            verify(roomsRepository, times(1)).findByCinemaIdAndId(cinemaId, roomId);
            verify(roomsMapper, never()).toDTO(any());
        }

        @Test
        void shouldCallRepositoryWithCorrectParameters() {
            // Arrange
            UUID cinemaId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
            UUID roomId = UUID.fromString("660e8400-e29b-41d4-a716-446655440000");
            
            CinemaEntity cinemaEntity = RoomFactory.createCinemaEntity();
            RoomEntity roomEntity = RoomFactory.createRoomEntity(roomId, "Sala 1", cinemaEntity);
            
            when(roomsRepository.findByCinemaIdAndId(cinemaId, roomId))
                    .thenReturn(java.util.Optional.of(roomEntity));
            when(roomsMapper.toDTO(roomEntity))
                    .thenReturn(RoomFactory.createRoomsResponseDTO(roomId, "Sala 1"));

            // Act
            roomsService.getRoomByCinemaIdAndRoomId(cinemaId, roomId);

            // Assert
            verify(roomsRepository, times(1)).findByCinemaIdAndId(
                    eq(UUID.fromString("550e8400-e29b-41d4-a716-446655440000")),
                    eq(UUID.fromString("660e8400-e29b-41d4-a716-446655440000"))
            );
        }

        @Test
        void shouldMapRoomEntityToDTOCorrectly() {
            // Arrange
            UUID cinemaId = UUID.randomUUID();
            UUID roomId = UUID.randomUUID();
            
            CinemaEntity cinemaEntity = RoomFactory.createCinemaEntity();
            RoomEntity roomEntity = RoomFactory.createRoomEntity(roomId, "Sala IMAX", cinemaEntity);
            RoomsResDTO expectedDTO = RoomFactory.createRoomsResponseDTO(roomId, "Sala IMAX");
            
            when(roomsRepository.findByCinemaIdAndId(cinemaId, roomId))
                    .thenReturn(java.util.Optional.of(roomEntity));
            when(roomsMapper.toDTO(roomEntity))
                    .thenReturn(expectedDTO);

            // Act
            RoomsResDTO result = roomsService.getRoomByCinemaIdAndRoomId(cinemaId, roomId);

            // Assert
            assertEquals(expectedDTO, result);
            assertEquals("Sala IMAX", result.name());
        }

        @Test
        void shouldThrowExceptionWithCorrectMessageFormat() {
            // Arrange
            UUID cinemaId = UUID.randomUUID();
            UUID roomId = UUID.randomUUID();
            
            when(roomsRepository.findByCinemaIdAndId(cinemaId, roomId))
                    .thenReturn(java.util.Optional.empty());

            // Act & Assert
            com.henrique.catalog.infra.exceptions.NotFoundException exception = 
                    assertThrows(com.henrique.catalog.infra.exceptions.NotFoundException.class,
                            () -> roomsService.getRoomByCinemaIdAndRoomId(cinemaId, roomId));
            
            String expectedMessage = String.format("Não existe uma sala com id %s no cinema com id %s", 
                    roomId, cinemaId);
            assertEquals(expectedMessage, exception.getMessage());
        }
    }
}
