package com.henrique.catalog.factory;

import com.henrique.catalog.domain.dto.res.seat.SeatResDTO;
import com.henrique.catalog.domain.entity.RoomEntity;
import com.henrique.catalog.domain.entity.SeatEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public class SeatFactory {

    private static final String DEFAULT_ROW_LABEL = "A";
    private static final Integer DEFAULT_COLUMN_NUMBER = 1;
    private static final UUID DEFAULT_USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final UUID DEFAULT_ROOM_ID = UUID.fromString("323e4567-e89b-12d3-a456-426614174000");

    public static SeatEntity createSeatEntity() {
        return createSeatEntity(UUID.randomUUID(), DEFAULT_ROW_LABEL, DEFAULT_COLUMN_NUMBER);
    }

    public static SeatEntity createSeatEntity(UUID id, String rowLabel, Integer columnNumber) {
        SeatEntity seat = new SeatEntity();
        seat.setId(id);
        seat.setRowLabel(rowLabel);
        seat.setColumnNumber(columnNumber);
        seat.setCreatedByUserId(DEFAULT_USER_ID);
        seat.setActive(true);
        seat.setCreatedAt(LocalDateTime.now());
        seat.setRoom(createRoomEntity());
        return seat;
    }

    public static SeatEntity createSeatEntity(UUID id, String rowLabel, Integer columnNumber, RoomEntity room) {
        SeatEntity seat = createSeatEntity(id, rowLabel, columnNumber);
        seat.setRoom(room);
        return seat;
    }

    public static SeatResDTO createSeatResponseDTO() {
        return createSeatResponseDTO(UUID.randomUUID(), DEFAULT_ROW_LABEL, DEFAULT_COLUMN_NUMBER);
    }

    public static SeatResDTO createSeatResponseDTO(UUID id, String rowLabel, Integer columnNumber) {
        return new SeatResDTO(
                id,
                DEFAULT_ROOM_ID,
                rowLabel,
                String.valueOf(columnNumber));
    }

    public static SeatResDTO createSeatResponseDTO(UUID id, UUID roomId, String rowLabel, Integer columnNumber) {
        return new SeatResDTO(
                id,
                roomId,
                rowLabel,
                String.valueOf(columnNumber));
    }

    public static RoomEntity createRoomEntity() {
        return RoomFactory.createRoomEntity();
    }
}
