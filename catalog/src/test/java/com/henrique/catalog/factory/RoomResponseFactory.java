package com.henrique.catalog.factory;

import com.henrique.catalog.domain.dto.res.rooms.RoomsResDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.UUID;

public class RoomResponseFactory {

    public static Page<RoomsResDTO> buildWithOneItem() {
        RoomsResDTO room = RoomFactory.createRoomsResponseDTO();
        return new PageImpl<>(List.of(room));
    }

    public static Page<RoomsResDTO> buildWithMultipleItems() {
        List<RoomsResDTO> rooms = List.of(
                RoomFactory.createRoomsResponseDTO(UUID.randomUUID(), "Sala 1"),
                RoomFactory.createRoomsResponseDTO(UUID.randomUUID(), "Sala 2"),
                RoomFactory.createRoomsResponseDTO(UUID.randomUUID(), "Sala 3")
        );
        return new PageImpl<>(rooms);
    }

    public static Page<RoomsResDTO> buildEmpty() {
        return new PageImpl<>(List.of());
    }

    public static Page<RoomsResDTO> buildWithCustomItems(List<RoomsResDTO> rooms) {
        return new PageImpl<>(rooms);
    }
}
