package edu.backend.infrastructure.out.persistence;

import edu.backend.domain.model.ParkingSpot;
import edu.backend.domain.port.out.ParkingSpotRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ParkingSpotPersistenceAdapter implements ParkingSpotRepositoryPort {

    private final ParkingSpotJpaRepository parkingSpotJpaRepository;

    public ParkingSpotPersistenceAdapter(ParkingSpotJpaRepository parkingSpotJpaRepository) {
        this.parkingSpotJpaRepository = parkingSpotJpaRepository;
    }

    @Override
    public List<ParkingSpot> findAll() {
        return parkingSpotJpaRepository.findAll().stream()
                .map(PersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<ParkingSpot> findByCode(String code) {
        return parkingSpotJpaRepository.findByCode(code).map(PersistenceMapper::toDomain);
    }
}