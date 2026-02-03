package com.henrique.catalog.domain.mapper;

import com.henrique.catalog.domain.dto.res.cinema.CinemaResDTO;
import com.henrique.catalog.domain.dto.res.rooms.RoomsResDTO;
import com.henrique.catalog.domain.entity.CinemaEntity;
import com.henrique.catalog.domain.entity.RoomEntity;
import com.henrique.catalog.factory.RoomFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomsMapperTest {

    private RoomsMapper roomsMapper;
    
    @Mock
    private CinemaMapper cinemaMapper;

    @BeforeEach
    void setUp() {
        roomsMapper = org.mapstruct.factory.Mappers.getMapper(RoomsMapper.class);
        ReflectionTestUtils.setField(roomsMapper, "cinemaMapper", cinemaMapper);
    }

    @Nested
    class ToDTO {

        @Test
        void shouldMapEntityToDTO() {
            // Arrange
            CinemaEntity cinemaEntity = RoomFactory.createCinemaEntity();
            RoomEntity roomEntity = RoomFactory.createRoomEntity(UUID.randomUUID(), "Sala Premium", cinemaEntity);
            
            CinemaResDTO cinemaDTO = new CinemaResDTO(
                    cinemaEntity.getId(),
                    cinemaEntity.getName(),
                    cinemaEntity.getCity()
            );
            
            when(cinemaMapper.toDTO(any(CinemaEntity.class)))
                    .thenReturn(cinemaDTO);

            // Act
            RoomsResDTO response = roomsMapper.toDTO(roomEntity);

            // Assert
            assertNotNull(response);
            assertEquals(roomEntity.getId(), response.id());
            assertEquals(roomEntity.getName(), response.name());
            assertEquals(roomEntity.getTotalRows(), response.totalRows());
            assertEquals(roomEntity.getTotalColumns(), response.totalColumns());
        }

        @Test
        void shouldReturnNullWhenEntityIsNull() {
            // Arrange
            RoomEntity entity = null;

            // Act
            RoomsResDTO response = roomsMapper.toDTO(entity);

            // Assert
            assertNull(response);
        }

        @Test
        void shouldMapRoomWithSpecificId() {
            // Arrange
            UUID roomId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
            CinemaEntity cinemaEntity = RoomFactory.createCinemaEntity();
            RoomEntity roomEntity = RoomFactory.createRoomEntity(roomId, "Sala IMAX", cinemaEntity);
            
            CinemaResDTO cinemaDTO = new CinemaResDTO(
                    cinemaEntity.getId(),
                    cinemaEntity.getName(),
                    cinemaEntity.getCity()
            );
            
            when(cinemaMapper.toDTO(any(CinemaEntity.class)))
                    .thenReturn(cinemaDTO);

            // Act
            RoomsResDTO response = roomsMapper.toDTO(roomEntity);

            // Assert
            assertNotNull(response);
            assertEquals(roomId, response.id());
            assertEquals("Sala IMAX", response.name());
        }

        @Test
        void shouldMapCinemaDataCorrectly() {
            // Arrange
            CinemaEntity cinemaEntity = RoomFactory.createCinemaEntity();
            RoomEntity roomEntity = RoomFactory.createRoomEntity(UUID.randomUUID(), "Sala VIP", cinemaEntity);
            
            CinemaResDTO cinemaDTO = new CinemaResDTO(
                    cinemaEntity.getId(),
                    cinemaEntity.getName(),
                    cinemaEntity.getCity()
            );
            
            when(cinemaMapper.toDTO(cinemaEntity))
                    .thenReturn(cinemaDTO);

            // Act
            RoomsResDTO response = roomsMapper.toDTO(roomEntity);

            // Assert
            assertNotNull(response);
            assertNotNull(response.cinema());
            assertEquals(cinemaEntity.getId(), response.cinema().id());
            assertEquals(cinemaEntity.getName(), response.cinema().name());
            assertEquals(cinemaEntity.getCity(), response.cinema().city());
        }

        @Test
        void shouldMapAllFieldsCorrectly() {
            // Arrange
            CinemaEntity cinemaEntity = RoomFactory.createCinemaEntity();
            UUID roomId = UUID.randomUUID();
            RoomEntity roomEntity = new RoomEntity();
            roomEntity.setId(roomId);
            roomEntity.setName("Sala 3D");
            roomEntity.setTotalRows(20);
            roomEntity.setTotalColumns(25);
            roomEntity.setCinema(cinemaEntity);
            
            CinemaResDTO cinemaDTO = new CinemaResDTO(
                    cinemaEntity.getId(),
                    cinemaEntity.getName(),
                    cinemaEntity.getCity()
            );
            
            when(cinemaMapper.toDTO(any(CinemaEntity.class)))
                    .thenReturn(cinemaDTO);

            // Act
            RoomsResDTO response = roomsMapper.toDTO(roomEntity);

            // Assert
            assertNotNull(response);
            assertEquals(roomId, response.id());
            assertEquals("Sala 3D", response.name());
            assertEquals(20, response.totalRows());
            assertEquals(25, response.totalColumns());
            assertNotNull(response.cinema());
        }

        @Test
        void shouldMapDifferentRoomNames() {
            // Arrange
            CinemaEntity cinemaEntity = RoomFactory.createCinemaEntity();
            RoomEntity room1 = RoomFactory.createRoomEntity(UUID.randomUUID(), "Sala 1", cinemaEntity);
            RoomEntity room2 = RoomFactory.createRoomEntity(UUID.randomUUID(), "Sala Premium", cinemaEntity);
            
            CinemaResDTO cinemaDTO = new CinemaResDTO(
                    cinemaEntity.getId(),
                    cinemaEntity.getName(),
                    cinemaEntity.getCity()
            );
            
            when(cinemaMapper.toDTO(any(CinemaEntity.class)))
                    .thenReturn(cinemaDTO);

            // Act
            RoomsResDTO response1 = roomsMapper.toDTO(room1);
            RoomsResDTO response2 = roomsMapper.toDTO(room2);

            // Assert
            assertNotNull(response1);
            assertNotNull(response2);
            assertEquals("Sala 1", response1.name());
            assertEquals("Sala Premium", response2.name());
            assertNotEquals(response1.id(), response2.id());
        }

        @Test
        void shouldMapRoomCapacityCorrectly() {
            // Arrange
            CinemaEntity cinemaEntity = RoomFactory.createCinemaEntity();
            RoomEntity roomEntity = new RoomEntity();
            roomEntity.setId(UUID.randomUUID());
            roomEntity.setName("Sala Grande");
            roomEntity.setTotalRows(30);
            roomEntity.setTotalColumns(40);
            roomEntity.setCinema(cinemaEntity);
            
            CinemaResDTO cinemaDTO = new CinemaResDTO(
                    cinemaEntity.getId(),
                    cinemaEntity.getName(),
                    cinemaEntity.getCity()
            );
            
            when(cinemaMapper.toDTO(any(CinemaEntity.class)))
                    .thenReturn(cinemaDTO);

            // Act
            RoomsResDTO response = roomsMapper.toDTO(roomEntity);

            // Assert
            assertNotNull(response);
            assertEquals(30, response.totalRows());
            assertEquals(40, response.totalColumns());
        }

        @Test
        void shouldPreserveDataIntegrity() {
            // Arrange
            CinemaEntity cinemaEntity = RoomFactory.createCinemaEntity();
            UUID roomId = UUID.randomUUID();
            String roomName = "Sala Teste";
            Integer totalRows = 15;
            Integer totalColumns = 20;

            RoomEntity roomEntity = new RoomEntity();
            roomEntity.setId(roomId);
            roomEntity.setName(roomName);
            roomEntity.setTotalRows(totalRows);
            roomEntity.setTotalColumns(totalColumns);
            roomEntity.setCinema(cinemaEntity);
            
            CinemaResDTO cinemaDTO = new CinemaResDTO(
                    cinemaEntity.getId(),
                    cinemaEntity.getName(),
                    cinemaEntity.getCity()
            );
            
            when(cinemaMapper.toDTO(any(CinemaEntity.class)))
                    .thenReturn(cinemaDTO);

            // Act
            RoomsResDTO response = roomsMapper.toDTO(roomEntity);

            // Assert
            assertNotNull(response);
            assertEquals(roomId, response.id());
            assertEquals(roomName, response.name());
            assertEquals(totalRows, response.totalRows());
            assertEquals(totalColumns, response.totalColumns());
        }
    }
}
