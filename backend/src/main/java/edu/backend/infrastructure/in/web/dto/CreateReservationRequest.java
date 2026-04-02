package edu.backend.infrastructure.in.web.dto;

import edu.backend.domain.model.ReservationSlot;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateReservationRequest(
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @NotNull ReservationSlot slot,
        boolean needsElectric
) {
}