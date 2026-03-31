package edu.backend.infrastructure.in.web.dto;

public record ParkingSpotStatusResponse(
        String code,
        String rowLabel,
        int spotNumber,
        boolean electric,
        String status
) {
}