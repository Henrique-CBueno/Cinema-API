package com.henrique.catalog.factory;

import com.henrique.catalog.domain.dto.req.cinema.CreateCinemaReqDTO;
import com.henrique.catalog.domain.dto.req.cinema.UpdateCinemaReqDTO;
import com.henrique.catalog.domain.dto.res.cinema.CinemaResDTO;
import com.henrique.catalog.domain.entity.CinemaEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public class CinemaFactory {

    private static final String DEFAULT_NAME = "Cinemark";
    private static final String DEFAULT_CITY = "São Paulo";
    private static final UUID DEFAULT_USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    public static CinemaEntity createCinemaEntity() {
        return createCinemaEntity(UUID.randomUUID(), DEFAULT_NAME, DEFAULT_CITY);
    }

    public static CinemaEntity createCinemaEntity(UUID id, String name, String city) {
        CinemaEntity cinema = new CinemaEntity();
        cinema.setId(id);
        cinema.setName(name);
        cinema.setCity(city);
        cinema.setCreatedByUserId(DEFAULT_USER_ID);
        cinema.setActive(true);
        cinema.setCreatedAt(LocalDateTime.now());
        return cinema;
    }

    public static CinemaEntity createCinemaEntity(String name, String city) {
        return createCinemaEntity(UUID.randomUUID(), name, city);
    }

    public static CinemaEntity createInactiveCinemaEntity(UUID id) {
        CinemaEntity cinema = createCinemaEntity(id, DEFAULT_NAME, DEFAULT_CITY);
        cinema.setActive(false);
        return cinema;
    }

    public static CinemaResDTO createCinemaResponseDTO() {
        return new CinemaResDTO(UUID.randomUUID(), DEFAULT_NAME, DEFAULT_CITY);
    }

    public static CinemaResDTO createCinemaResponseDTO(UUID id, String name, String city) {
        return new CinemaResDTO(id, name, city);
    }

    public static CinemaResDTO createCinemaResponseDTO(String name, String city) {
        return new CinemaResDTO(UUID.randomUUID(), name, city);
    }

    public static CreateCinemaReqDTO createCinemaRequestDTO() {
        return new CreateCinemaReqDTO(DEFAULT_NAME, DEFAULT_CITY);
    }

    public static CreateCinemaReqDTO createCinemaRequestDTO(String name, String city) {
        return new CreateCinemaReqDTO(name, city);
    }

    public static UpdateCinemaReqDTO createUpdateCinemaRequestDTO() {
        return new UpdateCinemaReqDTO(DEFAULT_NAME, DEFAULT_CITY);
    }

    public static UpdateCinemaReqDTO createUpdateCinemaRequestDTO(String name, String city) {
        return new UpdateCinemaReqDTO(name, city);
    }
}
