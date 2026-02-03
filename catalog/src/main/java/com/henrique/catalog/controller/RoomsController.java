package com.henrique.catalog.controller;

import com.henrique.catalog.domain.dto.global.PaginationParams;
import com.henrique.catalog.domain.dto.res.rooms.RoomsResDTO;
import com.henrique.catalog.infra.padronize.SuccessListDataResponse;
import com.henrique.catalog.infra.padronize.SuccessResponse;
import com.henrique.catalog.service.RoomsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("cinemas/{cinemaId}/rooms")
@RequiredArgsConstructor
public class RoomsController {

    private final RoomsService roomsService;

    @GetMapping
    public ResponseEntity<SuccessListDataResponse> getAllRoomsFromCinemaId(@PathVariable UUID cinemaId,
                                                                           PaginationParams paginationParams) {

        Page<RoomsResDTO> allRooms = roomsService.getAllRooms(paginationParams.toPageable(),
                cinemaId);

        if (allRooms.getContent().isEmpty()) return ResponseEntity.noContent().build();

        return ResponseEntity.ok(new SuccessListDataResponse(allRooms.getContent(),
                allRooms.getNumber(),
                allRooms.getSize(),
                allRooms.getTotalElements()));
    }

    @GetMapping("{roomId}")
    public ResponseEntity<SuccessResponse> getRoomFromCinemaByRoomId(@PathVariable UUID cinemaId,
                                                                     @PathVariable UUID roomId) {

        RoomsResDTO returnedRoom = roomsService.getRoomByCinemaIdAndRoomId(cinemaId, roomId);
        return ResponseEntity.ok(new SuccessResponse(returnedRoom));
    }
}
