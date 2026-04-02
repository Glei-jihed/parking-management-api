package edu.backend.bootstrap;

import edu.backend.application.service.AuthApplicationService;
import edu.backend.application.service.ManagementApplicationService;
import edu.backend.application.service.ReservationApplicationService;
import edu.backend.domain.port.out.ParkingSpotRepositoryPort;
import edu.backend.domain.port.out.PasswordHasherPort;
import edu.backend.domain.port.out.ReservationRepositoryPort;
import edu.backend.domain.port.out.TokenProviderPort;
import edu.backend.domain.port.out.UserRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    AuthApplicationService authApplicationService(
            UserRepositoryPort userRepositoryPort,
            PasswordHasherPort passwordHasherPort,
            TokenProviderPort tokenProviderPort
    ) {
        return new AuthApplicationService(userRepositoryPort, passwordHasherPort, tokenProviderPort);
    }

    @Bean
    ReservationApplicationService reservationApplicationService(
            ReservationRepositoryPort reservationRepositoryPort,
            ParkingSpotRepositoryPort parkingSpotRepositoryPort,
            UserRepositoryPort userRepositoryPort
    ) {
        return new ReservationApplicationService(
                reservationRepositoryPort,
                parkingSpotRepositoryPort,
                userRepositoryPort
        );
    }

    @Bean
    ManagementApplicationService managementApplicationService(
            UserRepositoryPort userRepositoryPort,
            ReservationRepositoryPort reservationRepositoryPort,
            ParkingSpotRepositoryPort parkingSpotRepositoryPort,
            PasswordHasherPort passwordHasherPort
    ) {
        return new ManagementApplicationService(
                userRepositoryPort,
                reservationRepositoryPort,
                parkingSpotRepositoryPort,
                passwordHasherPort
        );
    }
}