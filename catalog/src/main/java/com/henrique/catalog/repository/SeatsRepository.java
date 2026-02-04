package com.henrique.catalog.repository;

import com.henrique.catalog.domain.entity.SeatEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.transaction.Transactional;

import java.util.UUID;

public interface SeatsRepository extends JpaRepository<SeatEntity, UUID> {

    Page<SeatEntity> findAllByRoomId(UUID roomId,
            Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE SeatEntity s SET s.active = false WHERE s.id = :seatId AND s.room.id = :roomId AND s.room.cinema.id = :cinemaId")
    int softDeleteById(@Param("seatId") UUID seatId,
            @Param("roomId") UUID roomId,
            @Param("cinemaId") UUID cinemaId);
}
