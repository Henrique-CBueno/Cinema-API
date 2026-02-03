package com.henrique.catalog.factory;

import com.henrique.catalog.domain.dto.res.cinema.CinemaResDTO;
import com.henrique.catalog.domain.dto.res.rooms.RoomsResDTO;
import com.henrique.catalog.domain.entity.CinemaEntity;
import com.henrique.catalog.domain.entity.RoomEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public class RoomFactory {

    private static final String DEFAULT_ROOM_NAME = "Sala 1";
    private static final Integer DEFAULT_TOTAL_ROWS = 10;
    private static final Integer DEFAULT_TOTAL_COLUMNS = 15;
    private static final UUID DEFAULT_USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final UUID DEFAULT_CINEMA_ID = UUID.fromString("223e4567-e89b-12d3-a456-426614174000");
    private static final String DEFAULT_CINEMA_NAME = "Cinemark";
    private static final String DEFAULT_CINEMA_CITY = "São Paulo";

    public static RoomEntity createRoomEntity() {
        return createRoomEntity(UUID.randomUUID(), DEFAULT_ROOM_NAME);
    }

    public static RoomEntity createRoomEntity(UUID id, String roomName) {
        RoomEntity room = new RoomEntity();
        room.setId(id);
        room.setName(roomName);
        room.setTotalRows(DEFAULT_TOTAL_ROWS);
        room.setTotalColumns(DEFAULT_TOTAL_COLUMNS);
        room.setCreatedByUserId(DEFAULT_USER_ID);
        room.setActive(true);
        room.setCreatedAt(LocalDateTime.now());
        room.setCinema(createCinemaEntity());
        return room;
    }

    public static RoomEntity createRoomEntity(UUID id, String roomName, CinemaEntity cinema) {
        RoomEntity room = createRoomEntity(id, roomName);
        room.setCinema(cinema);
        return room;
    }

    public static RoomsResDTO createRoomsResponseDTO() {
        return createRoomsResponseDTO(UUID.randomUUID(), DEFAULT_ROOM_NAME);
    }

    public static RoomsResDTO createRoomsResponseDTO(UUID id, String roomName) {
        CinemaResDTO cinemaResDTO = new CinemaResDTO(
                DEFAULT_CINEMA_ID,
                DEFAULT_CINEMA_NAME,
                DEFAULT_CINEMA_CITY
        );
        return new RoomsResDTO(
                id,
                cinemaResDTO,
                roomName,
                DEFAULT_TOTAL_ROWS,
                DEFAULT_TOTAL_COLUMNS
        );
    }

    public static CinemaEntity createCinemaEntity() {
        CinemaEntity cinema = new CinemaEntity();
        cinema.setId(DEFAULT_CINEMA_ID);
        cinema.setName(DEFAULT_CINEMA_NAME);
        cinema.setCity(DEFAULT_CINEMA_CITY);
        cinema.setActive(true);
        cinema.setCreatedAt(LocalDateTime.now());
        return cinema;
    }
}
