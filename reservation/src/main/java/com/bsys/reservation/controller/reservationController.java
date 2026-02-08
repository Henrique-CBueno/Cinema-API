package com.bsys.reservation.controller;

import com.bsys.reservation.domain.dto.global.PaginationParams;
import com.bsys.reservation.domain.dto.req.CreateReservationDTO;
import com.bsys.reservation.domain.dto.res.ReservationResDTO;
import com.bsys.reservation.infra.padronize.SuccessListDataResponse;
import com.bsys.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<UUID> makeReservation(@RequestBody @Valid CreateReservationDTO dto,

            @RequestHeader(value = "X-User-Email", required = false) String email,
            @RequestHeader(value = "X-User-Name", required = false) String username,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {

        return ResponseEntity.ok(reservationService.createReservation(dto,
                email,
                username,
                userId));
    }

    @GetMapping()
    public ResponseEntity<SuccessListDataResponse<ReservationResDTO>> getReservations(@RequestHeader(value = "X-User-Id", required = false) String userId,
                                                                                      PaginationParams paginationParams) {

        return getSuccessListDataResponseResponseEntity(userId, paginationParams);
    }

    @GetMapping("{userId}")
    public ResponseEntity<SuccessListDataResponse<ReservationResDTO>> getReservationsByUserId(@PathVariable String userId,
                                                                                      PaginationParams paginationParams) {

        return getSuccessListDataResponseResponseEntity(userId, paginationParams);
    }

    @GetMapping("all")
    public ResponseEntity<SuccessListDataResponse<ReservationResDTO>> getAllReservations(PaginationParams paginationParams) {

        Page<ReservationResDTO> allReservations = reservationService.getAllReservation(paginationParams.toPageable());

        if (allReservations.getContent().isEmpty()) return ResponseEntity.noContent().build();

        return ResponseEntity.ok(new SuccessListDataResponse<ReservationResDTO>(allReservations.getContent(),
                allReservations.getNumber(),
                allReservations.getSize(),
                allReservations.getTotalElements()));
    }





    @NonNull
    private ResponseEntity<SuccessListDataResponse<ReservationResDTO>> getSuccessListDataResponseResponseEntity(@PathVariable String userId, PaginationParams paginationParams) {

        Page<ReservationResDTO> userReservations = reservationService.getUserReservation(UUID.fromString(userId), paginationParams.toPageable());

        if (userReservations.getContent().isEmpty()) return ResponseEntity.noContent().build();

        return ResponseEntity.ok(new SuccessListDataResponse<ReservationResDTO>(userReservations.getContent(),
                userReservations.getNumber(),
                userReservations.getSize(),
                userReservations.getTotalElements()));
    }
}
