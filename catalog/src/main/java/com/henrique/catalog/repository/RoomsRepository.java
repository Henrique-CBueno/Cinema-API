package com.henrique.catalog.repository;

import com.henrique.catalog.domain.entity.RoomEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoomsRepository extends JpaRepository<RoomEntity, UUID> {

    Page<RoomEntity> findByCinemaId(UUID cinemaId, Pageable pageable);
    Optional<RoomEntity> findByCinemaIdAndId(UUID cinemaId, UUID roomId);
}
