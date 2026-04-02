package edu.backend.infrastructure.in.web.dto;

import edu.backend.domain.model.Reservation;
import edu.backend.domain.model.ReservationSlot;
import edu.backend.domain.model.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReservationResponse(
        Long id,
        Long userId,
        String userEmail,
        LocalDate reservationDate,
        ReservationSlot slot,
        ReservationStatus status,
        String parkingSpotCode,
        boolean electricRequired,
        LocalDateTime createdAt,
        LocalDateTime checkInTime
) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getUserId(),
                reservation.getUserEmail(),
                reservation.getReservationDate(),
                reservation.getSlot(),
                reservation.getStatus(),
                reservation.getParkingSpotCode(),
                reservation.isElectricRequired(),
                reservation.getCreatedAt(),
                reservation.getCheckInTime()
        );
    }
}