package edu.backend.bootstrap;

import edu.backend.application.service.GetParkingSpotStatusesService;
import edu.backend.domain.port.out.LoadParkingSpotsPort;
import edu.backend.domain.port.out.LoadReservationsPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    GetParkingSpotStatusesService getParkingSpotStatusesService(
            LoadParkingSpotsPort loadParkingSpotsPort,
            LoadReservationsPort loadReservationsPort
    ) {
        return new GetParkingSpotStatusesService(loadParkingSpotsPort, loadReservationsPort);
    }
}