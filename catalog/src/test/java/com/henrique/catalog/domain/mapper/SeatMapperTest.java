package com.henrique.catalog.domain.mapper;

import com.henrique.catalog.domain.dto.req.seat.CreateSeatReqDTO;
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

    @Nested
    class ToEntity {

        @Test
        void shouldMapDTOToEntity() {
            // Arrange
            CreateSeatReqDTO dto = new CreateSeatReqDTO("A", 1);

            // Act
            SeatEntity response = seatMapper.toEntity(dto);

            // Assert
            assertNotNull(response);
            assertEquals(dto.rowLabel(), response.getRowLabel());
            assertEquals(dto.columnNumber(), response.getColumnNumber());
        }

        @Test
        void shouldReturnNullWhenDTOIsNull() {
            // Arrange
            CreateSeatReqDTO dto = null;

            // Act
            SeatEntity response = seatMapper.toEntity(dto);

            // Assert
            assertNull(response);
        }

        @Test
        void shouldMapDifferentRowLabels() {
            // Arrange
            CreateSeatReqDTO dto1 = new CreateSeatReqDTO("A", 1);
            CreateSeatReqDTO dto2 = new CreateSeatReqDTO("Z", 10);

            // Act
            SeatEntity response1 = seatMapper.toEntity(dto1);
            SeatEntity response2 = seatMapper.toEntity(dto2);

            // Assert
            assertNotNull(response1);
            assertNotNull(response2);
            assertEquals("A", response1.getRowLabel());
            assertEquals("Z", response2.getRowLabel());
        }

        @Test
        void shouldMapDifferentColumnNumbers() {
            // Arrange
            CreateSeatReqDTO dto1 = new CreateSeatReqDTO("A", 1);
            CreateSeatReqDTO dto2 = new CreateSeatReqDTO("A", 50);

            // Act
            SeatEntity response1 = seatMapper.toEntity(dto1);
            SeatEntity response2 = seatMapper.toEntity(dto2);

            // Assert
            assertNotNull(response1);
            assertNotNull(response2);
            assertEquals(1, response1.getColumnNumber());
            assertEquals(50, response2.getColumnNumber());
        }

        @Test
        void shouldNotMapIdWhenCreatingEntity() {
            // Arrange
            CreateSeatReqDTO dto = new CreateSeatReqDTO("B", 5);

            // Act
            SeatEntity response = seatMapper.toEntity(dto);

            // Assert
            assertNotNull(response);
            assertNull(response.getId());
        }

        @Test
        void shouldNotMapRoomWhenCreatingEntity() {
            // Arrange
            CreateSeatReqDTO dto = new CreateSeatReqDTO("C", 8);

            // Act
            SeatEntity response = seatMapper.toEntity(dto);

            // Assert
            assertNotNull(response);
            assertNull(response.getRoom());
        }

        @Test
        void shouldNotMapCreatedByUserIdWhenCreatingEntity() {
            // Arrange
            CreateSeatReqDTO dto = new CreateSeatReqDTO("D", 12);

            // Act
            SeatEntity response = seatMapper.toEntity(dto);

            // Assert
            assertNotNull(response);
            assertNull(response.getCreatedByUserId());
        }

        @Test
        void shouldMapAllFieldsWithoutDataLoss() {
            // Arrange
            String rowLabel = "F";
            Integer columnNumber = 15;
            CreateSeatReqDTO dto = new CreateSeatReqDTO(rowLabel, columnNumber);

            // Act
            SeatEntity response = seatMapper.toEntity(dto);

            // Assert
            assertNotNull(response);
            assertEquals(rowLabel, response.getRowLabel());
            assertEquals(columnNumber, response.getColumnNumber());
        }
    }
}
