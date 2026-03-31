package edu.backend.application.service;

import edu.backend.domain.model.*;
import edu.backend.domain.port.out.LoadParkingSpotsPort;
import edu.backend.domain.port.out.LoadReservationsPort;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetParkingSpotStatusesServiceTest {

    @Test
    void should_return_available_when_no_reservation() {
        LoadParkingSpotsPort parkingPort = () -> List.of(
                new ParkingSpot(1L, "A01", "A", 1, true)
        );

        LoadReservationsPort reservationPort = (date, slot) -> List.of();

        GetParkingSpotStatusesService service =
                new GetParkingSpotStatusesService(parkingPort, reservationPort);

        var result = service.getParkingSpotsWithStatus(
                LocalDate.now(),
                ReservationSlot.AFTERNOON
        );

        assertEquals(1, result.size());
        assertEquals("A01", result.get(0).getCode());
        assertEquals(ParkingSpotStatus.AVAILABLE, result.get(0).getStatus());
    }

    @Test
    void should_return_reserved_when_reservation_exists() {
        LoadParkingSpotsPort parkingPort = () -> List.of(
                new ParkingSpot(1L, "A01", "A", 1, true)
        );

        LoadReservationsPort reservationPort = (date, slot) -> List.of(
                new Reservation(
                        1L,
                        "user@test.com",
                        date,
                        slot,
                        ReservationStatus.RESERVED,
                        "A01",
                        null
                )
        );

        GetParkingSpotStatusesService service =
                new GetParkingSpotStatusesService(parkingPort, reservationPort);

        var result = service.getParkingSpotsWithStatus(
                LocalDate.now(),
                ReservationSlot.AFTERNOON
        );

        assertEquals(ParkingSpotStatus.RESERVED, result.get(0).getStatus());
    }

    @Test
    void should_return_checked_in_when_checkin_done() {
        LoadParkingSpotsPort parkingPort = () -> List.of(
                new ParkingSpot(1L, "B02", "B", 2, false)
        );

        LoadReservationsPort reservationPort = (date, slot) -> List.of(
                new Reservation(
                        1L,
                        "user@test.com",
                        date,
                        slot,
                        ReservationStatus.CHECKED_IN,
                        "B02",
                        LocalDateTime.now()
                )
        );

        GetParkingSpotStatusesService service =
                new GetParkingSpotStatusesService(parkingPort, reservationPort);

        var result = service.getParkingSpotsWithStatus(
                LocalDate.now(),
                ReservationSlot.MORNING
        );

        assertEquals(ParkingSpotStatus.CHECKED_IN, result.get(0).getStatus());
    }

    @Test
    void should_sort_by_code() {
        LoadParkingSpotsPort parkingPort = () -> List.of(
                new ParkingSpot(2L, "B02", "B", 2, false),
                new ParkingSpot(1L, "A01", "A", 1, true)
        );

        LoadReservationsPort reservationPort = (date, slot) -> List.of();

        GetParkingSpotStatusesService service =
                new GetParkingSpotStatusesService(parkingPort, reservationPort);

        var result = service.getParkingSpotsWithStatus(
                LocalDate.now(),
                ReservationSlot.AFTERNOON
        );

        assertEquals("A01", result.get(0).getCode());
        assertEquals("B02", result.get(1).getCode());
    }
}