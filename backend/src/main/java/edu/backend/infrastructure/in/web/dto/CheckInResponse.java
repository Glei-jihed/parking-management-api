package edu.backend.infrastructure.in.web.dto;

import java.time.LocalDateTime;

public record CheckInResponse(
        Long reservationId,
        String status,
        LocalDateTime checkInTime
) {
}