package com.henrique.catalog.repository;

import com.henrique.catalog.domain.entity.MovieEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<MovieEntity, UUID> {

    @Modifying
    @Transactional
    @Query("UPDATE MovieEntity m SET m.active = false WHERE m.id = :id")
    int softDeleteById(@Param("id") UUID id);
}
