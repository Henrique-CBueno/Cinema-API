package com.henrique.catalog.domain.mapper;

import com.henrique.catalog.domain.dto.res.session.SessionResDTO;
import com.henrique.catalog.domain.entity.SessionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {MovieMapper.class, RoomsMapper.class})
public interface SessionMapper {

    SessionResDTO toDTO(SessionEntity sessionEntity);
}
