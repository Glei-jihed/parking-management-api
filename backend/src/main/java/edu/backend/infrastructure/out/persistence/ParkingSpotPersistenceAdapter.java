package edu.backend.infrastructure.out.persistence;

import edu.backend.domain.model.ParkingSpot;
import edu.backend.domain.port.out.LoadParkingSpotsPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ParkingSpotPersistenceAdapter implements LoadParkingSpotsPort {

    private final ParkingSpotJpaRepository parkingSpotJpaRepository;

    public ParkingSpotPersistenceAdapter(ParkingSpotJpaRepository parkingSpotJpaRepository) {
        this.parkingSpotJpaRepository = parkingSpotJpaRepository;
    }

    @Override
    public List<ParkingSpot> findAll() {
        return parkingSpotJpaRepository.findAll()
                .stream()
                .map(PersistenceMapper::toDomain)
                .toList();
    }
}