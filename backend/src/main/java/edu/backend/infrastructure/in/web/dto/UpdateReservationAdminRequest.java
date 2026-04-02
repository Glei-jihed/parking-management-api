package edu.backend.infrastructure.in.web.dto;

import edu.backend.domain.model.ReservationSlot;
import edu.backend.domain.model.ReservationStatus;

import java.time.LocalDate;

public record UpdateReservationAdminRequest(
        LocalDate reservationDate,
        ReservationSlot slot,
        ReservationStatus status,
        Boolean electricRequired
) {
}