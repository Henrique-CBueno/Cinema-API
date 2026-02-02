package com.henrique.catalog.controller;

import com.henrique.catalog.domain.dto.global.PaginationParams;
import com.henrique.catalog.domain.dto.res.cinema.CinemaResDTO;
import com.henrique.catalog.infra.padronize.SuccessListDataResponse;
import com.henrique.catalog.infra.padronize.SuccessResponse;
import com.henrique.catalog.service.CinemaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("cinemas")
@RequiredArgsConstructor
public class CinemaController {

    private final CinemaService cinemaService;

    @GetMapping
    public ResponseEntity<SuccessListDataResponse> getAllCinemas(PaginationParams paginationParams) {

        Page<CinemaResDTO> allCinemas = cinemaService.getAllCinemas(paginationParams.toPageable());

        if (allCinemas.isEmpty()) return ResponseEntity.noContent().build();

        return ResponseEntity.ok(new SuccessListDataResponse(allCinemas.getContent(),
                allCinemas.getNumber(),
                allCinemas.getSize(),
                allCinemas.getTotalElements()));
    }

    @GetMapping("{id}")
    public ResponseEntity<SuccessResponse> getCinemaById(@PathVariable UUID id) {

        CinemaResDTO cinema = cinemaService.getCinemaById(id);
        return ResponseEntity.ok(new SuccessResponse(cinema));
    }
}
