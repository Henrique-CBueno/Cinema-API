package com.henrique.catalog.controller;

import com.henrique.catalog.domain.dto.global.PaginationParams;
import com.henrique.catalog.domain.dto.res.seat.SeatResDTO;
import com.henrique.catalog.infra.padronize.SuccessListDataResponse;
import com.henrique.catalog.service.SeatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

        Page<SeatResDTO> seats = seatsService.getSeatsByCinemaRoom(UUID.fromString(roomId), paginationParams.toPageable());

        if (seats.getContent().isEmpty()) return ResponseEntity.noContent().build();

        return ResponseEntity.ok(new SuccessListDataResponse(seats.getContent(),
                seats.getNumber(),
                seats.getSize(),
                seats.getTotalElements()));
    }
}
