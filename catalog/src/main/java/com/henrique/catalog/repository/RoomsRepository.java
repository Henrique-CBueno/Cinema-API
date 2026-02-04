package com.henrique.catalog.repository;

import com.henrique.catalog.domain.dto.req.rooms.UpdateRoomReqDTO;
import com.henrique.catalog.domain.entity.RoomEntity;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoomsRepository extends JpaRepository<RoomEntity, UUID> {

    Page<RoomEntity> findByCinemaId(UUID cinemaId, Pageable pageable);
    Optional<RoomEntity> findByCinemaIdAndId(UUID cinemaId, UUID roomId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("""
        UPDATE RoomEntity r SET
        r.name = COALESCE(:name, r.name),
        r.totalRows = COALESCE(:totalRows, r.totalRows),
        r.totalColumns = COALESCE(:totalColumns, r.totalColumns)
        WHERE r.id = :roomId AND r.cinema.id = :cinemaId
    """)
    int updatePartial(
            @Param("cinemaId") UUID cinemaId,
            @Param("roomId") UUID roomId,
            @Param("name") String name,
            @Param("totalRows") Integer totalRows,
            @Param("totalColumns") Integer totalColumns
    );

    @Modifying
    @Transactional
    @Query("UPDATE RoomEntity r SET r.active = false WHERE r.id = :roomId AND r.cinema.id = :cinemaId")
    int softDeleteById(@Param("roomId") UUID roomId,
                       @Param("cinemaId") UUID cinemaId);
}
