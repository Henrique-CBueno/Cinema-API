package com.henrique.catalog.controller;

import com.henrique.catalog.domain.dto.global.PaginationParams;
import com.henrique.catalog.domain.dto.req.movie.CreateMovieReqDTO;
import com.henrique.catalog.domain.dto.req.movie.UpdateMovieReqDTO;
import com.henrique.catalog.domain.dto.res.movie.MovieResDTO;
import com.henrique.catalog.infra.padronize.SuccessListDataResponse;
import com.henrique.catalog.infra.padronize.SuccessResponse;
import com.henrique.catalog.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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

    @PostMapping
    public ResponseEntity<Void> createMovie(
            @RequestBody @Valid CreateMovieReqDTO dto,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {

        UUID createdMovieId = movieService.createMovie(dto, userId);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdMovieId)
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteMovieById(@PathVariable UUID id) {

        movieService.deleteMovieById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}")
    public ResponseEntity<SuccessResponse> partialUpdateMovie(@PathVariable UUID id,
                                                              @RequestBody UpdateMovieReqDTO dto) {

        MovieResDTO updatedMovie = movieService.updatePartialMovie(id, dto);
        return ResponseEntity.ok(new SuccessResponse(updatedMovie));
    }
}
