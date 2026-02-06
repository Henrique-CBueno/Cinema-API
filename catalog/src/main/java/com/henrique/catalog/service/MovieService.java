package com.henrique.catalog.service;

import com.henrique.catalog.domain.dto.req.movie.CreateMovieReqDTO;
import com.henrique.catalog.domain.dto.req.movie.UpdateMovieReqDTO;
import com.henrique.catalog.domain.dto.res.movie.MovieResDTO;
import com.henrique.catalog.domain.entity.MovieEntity;
import com.henrique.catalog.domain.mapper.MovieMapper;
import com.henrique.catalog.infra.constants.ExceptionsConstants;
import com.henrique.catalog.infra.exceptions.DuplicateResourceException;
import com.henrique.catalog.infra.exceptions.NotFoundException;
import com.henrique.catalog.repository.MovieRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;


    public Page<MovieResDTO> getAllMovies(Pageable pageable) {
        Page<MovieEntity> allMovies = movieRepository.findAll(pageable);

        return allMovies.map(movieMapper::toResponse);
    }

    public MovieResDTO getMovieById(UUID id) {
        return movieRepository.findById(id)
                .map(movieMapper::toResponse)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionsConstants.MOVIE_DONT_EXISTS, id)
                ));
    }

    public MovieEntity getMovieByIdReturningEntity(UUID id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionsConstants.MOVIE_DONT_EXISTS, id)
                ));
    }

    @Transactional
    public UUID createMovie(CreateMovieReqDTO dto, String userId) {
        try {
            MovieEntity newMovie = movieMapper.toEntity(dto);
            newMovie.setCreatedByUserId(UUID.fromString(userId));

            movieRepository.saveAndFlush(newMovie);

            return newMovie.getId();
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException(ExceptionsConstants.DUPLICATE_RESOURCE, "Titulo");
        }
    }

    public void deleteMovieById(UUID id) {
        int affectedRows = movieRepository.softDeleteById(id);

        if (affectedRows < 1) throw new NotFoundException(String.format(
                ExceptionsConstants.MOVIE_DONT_EXISTS,
                id
        ));
    }

    @Transactional
    public MovieResDTO updatePartialMovie(UUID id,
                                          UpdateMovieReqDTO dto) {

        try {
            int affectedRows = movieRepository.updatePartial(id,
                    dto.title(),
                    dto.description(),
                    dto.durationMinutes(),
                    dto.rating());

            if (affectedRows < 1) throw new NotFoundException(String.format(
                    ExceptionsConstants.MOVIE_DONT_EXISTS,
                    id
            ));

            return movieRepository.findById(id)
                    .map(movieMapper::toResponse)
                    .orElseThrow(() -> new NotFoundException(
                            String.format(ExceptionsConstants.MOVIE_DONT_EXISTS, id)
                    ));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException(ExceptionsConstants.DUPLICATE_RESOURCE, "Titulo");
        }
    }
}
