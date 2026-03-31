package edu.backend.infrastructure.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingSpotJpaRepository extends JpaRepository<ParkingSpotJpaEntity, Long> {
}