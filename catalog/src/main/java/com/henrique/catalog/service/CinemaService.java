package com.henrique.catalog.service;

import com.henrique.catalog.domain.dto.res.cinema.CinemaResDTO;
import com.henrique.catalog.domain.entity.CinemaEntity;
import com.henrique.catalog.domain.mapper.CinemaMapper;
import com.henrique.catalog.infra.constants.ExceptionsConstants;
import com.henrique.catalog.infra.exceptions.NotFoundException;
import com.henrique.catalog.repository.CinemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CinemaService {

    private final CinemaRepository cinemaRepository;
    private final CinemaMapper cinemaMapper;

    public Page<CinemaResDTO> getAllCinemas(Pageable pageable) {
        Page<CinemaEntity> allCinemas = cinemaRepository.findAll(pageable);

        return allCinemas.map(cinemaMapper::toDTO);
    }

    public CinemaResDTO getCinemaById(UUID id) {
        return cinemaRepository.findById(id).map(cinemaMapper::toDTO)
                .orElseThrow(() ->
                        new NotFoundException(String.format(
                                ExceptionsConstants.CINEMA_DONT_EXISTS,
                                id
                        )
                ));
    }
}
