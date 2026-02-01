package com.henrique.catalog.controller;

import com.henrique.catalog.domain.dto.global.PaginationParams;
import com.henrique.catalog.domain.dto.res.movie.MovieResDTO;
import com.henrique.catalog.infra.padronize.SuccessListDataResponse;
import com.henrique.catalog.infra.padronize.SuccessResponse;
import com.henrique.catalog.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public ResponseEntity<SuccessListDataResponse> getAllMovies(
//            ELE JA PEGA DO PATH AUTOMATICO POIS É GET
            PaginationParams paginationParams
    ) {
        Page<MovieResDTO> allMovies = movieService.getAllMovies(paginationParams.toPageable());

        if (allMovies.getContent().isEmpty()) return ResponseEntity.noContent().build();

        return ResponseEntity.ok(new SuccessListDataResponse(allMovies.getContent(),
                allMovies.getNumber(),
                allMovies.getSize(),
                (long) allMovies.getNumberOfElements()));
    }

    @GetMapping("{id}")
    public ResponseEntity<SuccessResponse> getMovieById(@PathVariable UUID id) {

        MovieResDTO movie = movieService.getMovieById(id);
        return ResponseEntity.ok(new SuccessResponse(movie));
    }
}
