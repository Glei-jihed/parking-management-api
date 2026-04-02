package edu.backend.domain.port.out;

import edu.backend.domain.model.Reservation;
import edu.backend.domain.model.ReservationSlot;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepositoryPort {
    Reservation save(Reservation reservation);
    Optional<Reservation> findById(Long id);
    List<Reservation> findAll();
    List<Reservation> findByUserId(Long userId);
    List<Reservation> findByDate(LocalDate date);
    List<Reservation> findRelevantReservations(LocalDate date, ReservationSlot slot);
    List<Reservation> findBetween(LocalDate startDate, LocalDate endDate);
}