package edu.backend.infrastructure.out.persistence;

import edu.backend.infrastructure.out.security.security.BCryptPasswordHasherAdapter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ParkingSpotJpaRepository parkingSpotJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final BCryptPasswordHasherAdapter passwordHasher;

    public DataInitializer(
            ParkingSpotJpaRepository parkingSpotJpaRepository,
            UserJpaRepository userJpaRepository,
            BCryptPasswordHasherAdapter passwordHasher
    ) {
        this.parkingSpotJpaRepository = parkingSpotJpaRepository;
        this.userJpaRepository = userJpaRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public void run(String... args) {
        seedParkingSpots();
        seedUsers();
    }

    private void seedParkingSpots() {
        if (parkingSpotJpaRepository.count() > 0) {
            return;
        }

        for (char row = 'A'; row <= 'F'; row++) {
            for (int number = 1; number <= 10; number++) {
                ParkingSpotJpaEntity entity = new ParkingSpotJpaEntity();
                entity.setCode(row + String.format("%02d", number));
                entity.setRowLabel(String.valueOf(row));
                entity.setSpotNumber(number);
                entity.setElectric(row == 'A' || row == 'F');
                parkingSpotJpaRepository.save(entity);
            }
        }
    }

    private void seedUsers() {
        if (userJpaRepository.count() > 0) {
            return;
        }

        userJpaRepository.save(buildUser("employee@parking.local", "password", "EMPLOYEE"));
        userJpaRepository.save(buildUser("secretary@parking.local", "password", "SECRETARY"));
        userJpaRepository.save(buildUser("manager@parking.local", "password", "MANAGER"));
    }

    private UserJpaEntity buildUser(String email, String password, String role) {
        UserJpaEntity user = new UserJpaEntity();
        user.setEmail(email);
        user.setPassword(passwordHasher.hash(password));
        user.setRole(role);
        user.setActive(true);
        return user;
    }
}