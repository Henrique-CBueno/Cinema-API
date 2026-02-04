package com.henrique.catalog.factory;

import com.henrique.catalog.domain.dto.res.seat.SeatResDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.UUID;

public class SeatResponseFactory {

    public static Page<SeatResDTO> buildWithOneItem() {
        SeatResDTO seat = SeatFactory.createSeatResponseDTO();
        return new PageImpl<>(List.of(seat));
    }

    public static Page<SeatResDTO> buildWithMultipleItems() {
        List<SeatResDTO> seats = List.of(
                SeatFactory.createSeatResponseDTO(UUID.randomUUID(), "A", 1),
                SeatFactory.createSeatResponseDTO(UUID.randomUUID(), "A", 2),
                SeatFactory.createSeatResponseDTO(UUID.randomUUID(), "A", 3),
                SeatFactory.createSeatResponseDTO(UUID.randomUUID(), "B", 1),
                SeatFactory.createSeatResponseDTO(UUID.randomUUID(), "B", 2));
        return new PageImpl<>(seats);
    }

    public static Page<SeatResDTO> buildEmpty() {
        return new PageImpl<>(List.of());
    }

    public static Page<SeatResDTO> buildWithCustomItems(List<SeatResDTO> seats) {
        return new PageImpl<>(seats);
    }
}
