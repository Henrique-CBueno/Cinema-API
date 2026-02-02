package com.henrique.catalog.repository;

import com.henrique.catalog.domain.entity.CinemaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CinemaRepository extends JpaRepository<CinemaEntity, UUID> {
}
