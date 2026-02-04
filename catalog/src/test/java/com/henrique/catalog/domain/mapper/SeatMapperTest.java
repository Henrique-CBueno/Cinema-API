package com.henrique.catalog.domain.mapper;

import com.henrique.catalog.domain.dto.res.seat.SeatResDTO;
import com.henrique.catalog.domain.entity.RoomEntity;
import com.henrique.catalog.domain.entity.SeatEntity;
import com.henrique.catalog.factory.SeatFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SeatMapperTest {

    private SeatMapper seatMapper;

    @BeforeEach
    void setUp() {
        seatMapper = org.mapstruct.factory.Mappers.getMapper(SeatMapper.class);
    }

    @Nested
    class ToDTO {

        @Test
        void shouldMapEntityToDTO() {
            // Arrange
            RoomEntity roomEntity = SeatFactory.createRoomEntity();
            SeatEntity seatEntity = SeatFactory.createSeatEntity(UUID.randomUUID(), "A", 1, roomEntity);

            // Act
            SeatResDTO response = seatMapper.toDTO(seatEntity);

            // Assert
            assertNotNull(response);
            assertEquals(seatEntity.getId(), response.id());
            assertEquals(seatEntity.getRoom().getId(), response.roomId());
            assertEquals(seatEntity.getRowLabel(), response.rowLabel());
            assertEquals(String.valueOf(seatEntity.getColumnNumber()), response.columnNumber());
        }

        @Test
        void shouldReturnNullWhenEntityIsNull() {
            // Arrange
            SeatEntity entity = null;

            // Act
            SeatResDTO response = seatMapper.toDTO(entity);

            // Assert
            assertNull(response);
        }

        @Test
        void shouldMapSeatWithSpecificId() {
            // Arrange
            UUID seatId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
            RoomEntity roomEntity = SeatFactory.createRoomEntity();
            SeatEntity seatEntity = SeatFactory.createSeatEntity(seatId, "B", 5, roomEntity);

            // Act
            SeatResDTO response = seatMapper.toDTO(seatEntity);

            // Assert
            assertNotNull(response);
            assertEquals(seatId, response.id());
            assertEquals("B", response.rowLabel());
            assertEquals("5", response.columnNumber());
        }

        @Test
        void shouldMapRoomIdCorrectly() {
            // Arrange
            UUID roomId = UUID.fromString("660e8400-e29b-41d4-a716-446655440000");
            RoomEntity roomEntity = SeatFactory.createRoomEntity();
            roomEntity.setId(roomId);
            SeatEntity seatEntity = SeatFactory.createSeatEntity(UUID.randomUUID(), "C", 10, roomEntity);

            // Act
            SeatResDTO response = seatMapper.toDTO(seatEntity);

            // Assert
            assertNotNull(response);
            assertEquals(roomId, response.roomId());
        }

        @Test
        void shouldMapAllFieldsWithoutDataLoss() {
            // Arrange
            UUID seatId = UUID.randomUUID();
            UUID roomId = UUID.randomUUID();
            RoomEntity roomEntity = SeatFactory.createRoomEntity();
            roomEntity.setId(roomId);
            SeatEntity seatEntity = SeatFactory.createSeatEntity(seatId, "D", 8, roomEntity);

            // Act
            SeatResDTO response = seatMapper.toDTO(seatEntity);

            // Assert
            assertNotNull(response);
            assertEquals(seatId, response.id());
            assertEquals(roomId, response.roomId());
            assertEquals("D", response.rowLabel());
            assertEquals("8", response.columnNumber());
        }

        @Test
        void shouldMapDifferentRowLabels() {
            // Arrange
            RoomEntity roomEntity = SeatFactory.createRoomEntity();
            SeatEntity seatEntity1 = SeatFactory.createSeatEntity(UUID.randomUUID(), "A", 1, roomEntity);
            SeatEntity seatEntity2 = SeatFactory.createSeatEntity(UUID.randomUUID(), "Z", 1, roomEntity);

            // Act
            SeatResDTO response1 = seatMapper.toDTO(seatEntity1);
            SeatResDTO response2 = seatMapper.toDTO(seatEntity2);

            // Assert
            assertNotNull(response1);
            assertNotNull(response2);
            assertEquals("A", response1.rowLabel());
            assertEquals("Z", response2.rowLabel());
        }

        @Test
        void shouldMapColumnNumberAsString() {
            // Arrange
            RoomEntity roomEntity = SeatFactory.createRoomEntity();
            SeatEntity seatEntity = SeatFactory.createSeatEntity(UUID.randomUUID(), "A", 15, roomEntity);

            // Act
            SeatResDTO response = seatMapper.toDTO(seatEntity);

            // Assert
            assertNotNull(response);
            assertEquals("15", response.columnNumber());
            assertTrue(response.columnNumber() instanceof String);
        }

        @Test
        void shouldPreserveDataIntegrity() {
            // Arrange
            UUID seatId = UUID.randomUUID();
            UUID roomId = UUID.randomUUID();
            String rowLabel = "E";
            Integer columnNumber = 20;

            RoomEntity roomEntity = SeatFactory.createRoomEntity();
            roomEntity.setId(roomId);
            SeatEntity seatEntity = SeatFactory.createSeatEntity(seatId, rowLabel, columnNumber, roomEntity);

            // Act
            SeatResDTO response = seatMapper.toDTO(seatEntity);

            // Assert
            assertNotNull(response);
            assertEquals(seatId, response.id());
            assertEquals(roomId, response.roomId());
            assertEquals(rowLabel, response.rowLabel());
            assertEquals(String.valueOf(columnNumber), response.columnNumber());
        }
    }
}
