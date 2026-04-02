package edu.backend.infrastructure.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParkingSpotJpaRepository extends JpaRepository<ParkingSpotJpaEntity, Long> {
    Optional<ParkingSpotJpaEntity> findByCode(String code);
}