package com.henrique.catalog.repository;

import com.henrique.catalog.domain.entity.SessionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<SessionEntity, UUID> {

    @Query("""
        SELECT s FROM SessionEntity s
        WHERE (:movieId IS NULL OR s.movie.id = :movieId)
        AND (:cinemaId IS NULL OR s.room.cinema.id = :cinemaId)
        AND (:roomId IS NULL OR s.room.id = :roomId)
        AND (:date IS NULL OR
        (s.startTime >= :startOfDay AND s.startTime <= :endOfDay))
    """)
    Page<SessionEntity> findSessionsWithFilters(UUID movieId,
                                                UUID cinemaId,
                                                UUID roomId,
                                                LocalDate date, // Usado só para checar null
                                                LocalDateTime startOfDay,
                                                LocalDateTime endOfDay,
                                                Pageable pageable);
}
