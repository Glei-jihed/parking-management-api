package edu.backend.domain.port.out;

import edu.backend.domain.model.Reservation;
import edu.backend.domain.model.ReservationSlot;

import java.time.LocalDate;
import java.util.List;

public interface LoadReservationsPort {
    List<Reservation> findRelevantReservations(LocalDate date, ReservationSlot slot);
}