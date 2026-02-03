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

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("""
        UPDATE CinemaEntity m SET
        m.name = COALESCE(:name, m.name),
        m.city = COALESCE(:city, m.city)
        WHERE m.id = :id
    """)
    int updatePartial(
            @Param("id") UUID id,
            @Param("name") String name,
            @Param("city") String city
    );
}
