package com.henrique.catalog.repository;

import com.henrique.catalog.domain.entity.SeatEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SeatsRepository extends JpaRepository<SeatEntity,UUID> {

    Page<SeatEntity> findAllByRoomId(UUID roomId,
                                     Pageable pageable);
}
