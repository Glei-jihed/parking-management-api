package edu.backend.domain.port.out;

import edu.backend.domain.model.ParkingSpot;

import java.util.List;
import java.util.Optional;

public interface ParkingSpotRepositoryPort {
    List<ParkingSpot> findAll();
    Optional<ParkingSpot> findByCode(String code);
}