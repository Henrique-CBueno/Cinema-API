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
            
            when(roomsRepository.findByCinemaId(cinemaId, pageable))
                    .thenReturn(entityPage);
            when(cinemaMapper.toDTO(cinemaEntity))
                    .thenReturn(new com.henrique.catalog.domain.dto.res.cinema.CinemaResDTO(
                            cinemaEntity.getId(),
                            cinemaEntity.getName(),
                            cinemaEntity.getCity()
                    ));

            // Act
            Page<RoomsResDTO> result = roomsService.getAllRooms(pageable, cinemaId);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.getTotalElements());
            assertEquals("Sala 1", result.getContent().get(0).name());
            assertEquals("Sala 2", result.getContent().get(1).name());
            verify(roomsRepository, times(1)).findByCinemaId(cinemaId, pageable);
            verify(cinemaMapper, times(2)).toDTO(cinemaEntity);
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
            verify(cinemaMapper, never()).toDTO(any());
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
            
            when(roomsRepository.findByCinemaId(cinemaId, pageable))
                    .thenReturn(entityPage);
            when(cinemaMapper.toDTO(cinemaEntity))
                    .thenReturn(new com.henrique.catalog.domain.dto.res.cinema.CinemaResDTO(
                            cinemaEntity.getId(),
                            cinemaEntity.getName(),
                            cinemaEntity.getCity()
                    ));

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
}
