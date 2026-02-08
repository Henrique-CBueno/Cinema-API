package com.henrique.catalog.domain.mapper;

import com.henrique.catalog.domain.dto.req.sessions.CreateSessionReqDTO;
import com.henrique.catalog.domain.dto.res.session.SessionResDTO;
import com.henrique.catalog.domain.entity.MovieEntity;
import com.henrique.catalog.domain.entity.RoomEntity;
import com.henrique.catalog.domain.entity.SessionEntity;
import com.henrique.catalog.domain.entity.enums.SessionStatus;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = { MovieMapper.class, RoomsMapper.class })
public interface SessionMapper {

    @Mapping(source = "room.cinema.id", target = "cinemaId")
    SessionResDTO toDTO(SessionEntity sessionEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "endTime", ignore = true)
    @Mapping(target = "createdByUserId", source = "createdByUserId")
    SessionEntity toEntity(CreateSessionReqDTO dto,
            MovieEntity movie,
            RoomEntity room,
            UUID createdByUserId);

    @AfterMapping
    default void calculateEndTimeAndStatus(@MappingTarget SessionEntity entity,
            CreateSessionReqDTO dto,
            MovieEntity movie) {

        entity.setEndTime(dto.startTime().plusMinutes(movie.getDurationMinutes()));
        entity.setStatus(SessionStatus.SCHEDULED);
    }

}
