package edu.backend.infrastructure.out.persistence;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ParkingSpotJpaRepository repository;

    public DataInitializer(ParkingSpotJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) return;

        ParkingSpotJpaEntity spot = new ParkingSpotJpaEntity();
        spot.setCode("A01");
        spot.setElectric(true);

        repository.save(spot);
    }
}