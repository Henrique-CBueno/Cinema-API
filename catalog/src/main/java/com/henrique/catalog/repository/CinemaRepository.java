package com.henrique.catalog.repository;

import com.henrique.catalog.domain.entity.CinemaEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CinemaRepository extends JpaRepository<CinemaEntity, UUID> {

    @Modifying
    @Transactional
    @Query("UPDATE CinemaEntity c SET c.active = false WHERE c.id = :id")
    int softDeleteById(@Param("id") UUID id);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("""
        UPDATE CinemaEntity c SET
        c.name = COALESCE(:name, c.name),
        c.city = COALESCE(:city, c.city)
        WHERE c.id = :id
    """)
    int updatePartial(
            @Param("id") UUID id,
            @Param("name") String name,
            @Param("city") String city
    );
}
