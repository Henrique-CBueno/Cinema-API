package com.henrique.catalog.controller;

import com.henrique.catalog.domain.dto.global.PaginationParams;
import com.henrique.catalog.domain.dto.req.seat.CreateSeatReqDTO;
import com.henrique.catalog.domain.dto.res.seat.SeatsExistenceResDTO;
import com.henrique.catalog.domain.dto.res.seat.SeatResDTO;
import com.henrique.catalog.infra.padronize.SuccessListDataResponse;
import com.henrique.catalog.service.SeatsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("cinemas/{cinemaId}/rooms/{roomId}/seats")
@RequiredArgsConstructor
public class SeatsController {

    private final SeatsService seatsService;

    @GetMapping
    public ResponseEntity<SuccessListDataResponse> getAllSeatsByCinemaRoom(@PathVariable String cinemaId,
            @PathVariable String roomId,
            PaginationParams paginationParams) {

        Page<SeatResDTO> seats = seatsService.getSeatsByCinemaRoom(UUID.fromString(roomId),
                paginationParams.toPageable());

        if (seats.getContent().isEmpty())
            return ResponseEntity.noContent().build();

        return ResponseEntity.ok(new SuccessListDataResponse(seats.getContent(),
                seats.getNumber(),
                seats.getSize(),
                seats.getTotalElements()));
    }

    @PostMapping
    public ResponseEntity<Void> createSeatsInCinemaRoom(@PathVariable String cinemaId,
            @PathVariable String roomId,
            @RequestBody @Valid List<CreateSeatReqDTO> seats,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {

        seatsService.createSeatsInCinemaRoom(UUID.fromString(cinemaId),
                UUID.fromString(roomId),
                seats,
                UUID.fromString(userId));

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build()
                .toUri();

        return ResponseEntity.created(uri).build();
    }

        @PostMapping("exists")
        public ResponseEntity<SeatsExistenceResDTO> checkSeatsExistence(@PathVariable String cinemaId,
                        @PathVariable String roomId,
                        @RequestBody List<UUID> seatIds) {

                SeatsExistenceResDTO result = seatsService.validateSeatsInRoom(UUID.fromString(cinemaId),
                                UUID.fromString(roomId),
                                seatIds);

                return ResponseEntity.ok(result);
        }

        @DeleteMapping("{seatId}")
        public ResponseEntity<Void> deleteSeat(@PathVariable String cinemaId,
                        @PathVariable String roomId,
                        @PathVariable String seatId) {

        seatsService.deleteSeatFromRoom(UUID.fromString(cinemaId),
                UUID.fromString(roomId),
                UUID.fromString(seatId));

        return ResponseEntity.noContent().build();
    }
}
