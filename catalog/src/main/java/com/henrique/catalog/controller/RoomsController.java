package com.henrique.catalog.controller;

import com.henrique.catalog.domain.dto.global.PaginationParams;
import com.henrique.catalog.domain.dto.req.cinema.UpdateCinemaReqDTO;
import com.henrique.catalog.domain.dto.req.rooms.CreateRoomReqDTO;
import com.henrique.catalog.domain.dto.req.rooms.UpdateRoomReqDTO;
import com.henrique.catalog.domain.dto.res.rooms.RoomsResDTO;
import com.henrique.catalog.infra.padronize.SuccessListDataResponse;
import com.henrique.catalog.infra.padronize.SuccessResponse;
import com.henrique.catalog.service.RoomsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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

    @PostMapping
    public ResponseEntity<Void> createRoomForCinemaId(@PathVariable UUID cinemaId,
                                                      @RequestBody @Valid CreateRoomReqDTO dto,
                                                      @RequestHeader(value = "X-User-Id", required = false) String userId) {

        UUID createdRoomId = roomsService.createRoomForCinemaId(cinemaId,
                dto,
                UUID.fromString(userId));

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdRoomId)
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    @PatchMapping("{roomId}")
    public ResponseEntity<SuccessResponse> partialUpdateRoom(@PathVariable UUID cinemaId,
                                                             @PathVariable UUID roomId,
                                                             @RequestBody UpdateRoomReqDTO dto) {

        RoomsResDTO updatedRoom = roomsService.updateRoom(cinemaId,
                roomId,
                dto);

        return ResponseEntity.ok(new SuccessResponse(updatedRoom));
    }

    @DeleteMapping("{roomId}")
    public ResponseEntity<Void> deleteRoomFromCinema(@PathVariable UUID cinemaId,
                                                     @PathVariable UUID roomId) {

        roomsService.deleteRoomFromCinema(roomId, cinemaId);

        return ResponseEntity.noContent().build();
    }
}
