package com.bsys.reservation.controller;

import com.bsys.reservation.domain.dto.req.CreateReservationDTO;
import com.bsys.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class reservationController {

    private final ReservationService reservationService;

    @PostMapping()
    public ResponseEntity<UUID> fazerRezerva(@RequestBody @Valid CreateReservationDTO dto,

            @RequestHeader(value = "X-User-Email", required = false) String email,
            @RequestHeader(value = "X-User-Name", required = false) String username,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {

        return ResponseEntity.ok(reservationService.createReservation(dto,
                email,
                username,
                userId));
    }
}
