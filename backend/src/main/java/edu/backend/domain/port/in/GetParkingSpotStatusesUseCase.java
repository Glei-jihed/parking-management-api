package edu.backend.domain.port.in;

import edu.backend.domain.model.ParkingSpotView;
import edu.backend.domain.model.ReservationSlot;

import java.time.LocalDate;
import java.util.List;

public interface GetParkingSpotStatusesUseCase {
    List<ParkingSpotView> getParkingSpotsWithStatus(LocalDate date, ReservationSlot slot);
}