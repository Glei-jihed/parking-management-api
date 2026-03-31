package edu.backend.application.service;

import edu.backend.domain.model.ParkingSpot;
import edu.backend.domain.model.ParkingSpotStatus;
import edu.backend.domain.model.ParkingSpotView;
import edu.backend.domain.model.Reservation;
import edu.backend.domain.model.ReservationSlot;
import edu.backend.domain.model.ReservationStatus;
import edu.backend.domain.port.in.GetParkingSpotStatusesUseCase;
import edu.backend.domain.port.out.LoadParkingSpotsPort;
import edu.backend.domain.port.out.LoadReservationsPort;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GetParkingSpotStatusesService implements GetParkingSpotStatusesUseCase {

    private final LoadParkingSpotsPort loadParkingSpotsPort;
    private final LoadReservationsPort loadReservationsPort;

    public GetParkingSpotStatusesService(
            LoadParkingSpotsPort loadParkingSpotsPort,
            LoadReservationsPort loadReservationsPort
    ) {
        this.loadParkingSpotsPort = loadParkingSpotsPort;
        this.loadReservationsPort = loadReservationsPort;
    }

    @Override
    public List<ParkingSpotView> getParkingSpotsWithStatus(LocalDate date, ReservationSlot slot) {
        List<ParkingSpot> parkingSpots = loadParkingSpotsPort.findAll();
        List<Reservation> reservations = loadReservationsPort.findRelevantReservations(date, slot);

        Map<String, Reservation> reservationBySpotCode = reservations.stream()
                .collect(Collectors.toMap(
                        Reservation::getParkingSpotCode,
                        Function.identity(),
                        (first, second) -> first
                ));

        return parkingSpots.stream()
                .map(spot -> toView(spot, reservationBySpotCode.get(spot.getCode()), date, slot))
                .sorted((a, b) -> a.getCode().compareToIgnoreCase(b.getCode()))
                .toList();
    }

    private ParkingSpotView toView(
            ParkingSpot spot,
            Reservation reservation,
            LocalDate date,
            ReservationSlot slot
    ) {
        ParkingSpotStatus status = calculateStatus(reservation, date, slot);

        return new ParkingSpotView(
                spot.getCode(),
                spot.getRowLabel(),
                spot.getSpotNumber(),
                spot.isElectric(),
                status
        );
    }

    private ParkingSpotStatus calculateStatus(
            Reservation reservation,
            LocalDate date,
            ReservationSlot slot
    ) {
        if (reservation == null) {
            return ParkingSpotStatus.AVAILABLE;
        }

        if (reservation.getStatus() == ReservationStatus.CHECKED_IN) {
            return ParkingSpotStatus.CHECKED_IN;
        }

        if (reservation.getStatus() == ReservationStatus.RELEASED
                || reservation.getStatus() == ReservationStatus.CANCELLED) {
            return ParkingSpotStatus.AVAILABLE;
        }

        if (reservation.getStatus() == ReservationStatus.RESERVED) {
            boolean shouldBeReleasedForDisplay =
                    date.equals(LocalDate.now())
                            && LocalTime.now().isAfter(LocalTime.of(11, 0))
                            && (slot == ReservationSlot.MORNING || slot == ReservationSlot.FULL_DAY)
                            && reservation.getCheckInTime() == null;

            return shouldBeReleasedForDisplay
                    ? ParkingSpotStatus.AVAILABLE
                    : ParkingSpotStatus.RESERVED;
        }

        return ParkingSpotStatus.AVAILABLE;
    }
}