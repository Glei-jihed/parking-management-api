package edu.backend.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Reservation {

    private final Long id;
    private final String employeeEmail;
    private final LocalDate reservationDate;
    private final ReservationSlot slot;
    private final ReservationStatus status;
    private final String parkingSpotCode;
    private final LocalDateTime checkInTime;

    public Reservation(
            Long id,
            String employeeEmail,
            LocalDate reservationDate,
            ReservationSlot slot,
            ReservationStatus status,
            String parkingSpotCode,
            LocalDateTime checkInTime
    ) {
        this.id = id;
        this.employeeEmail = employeeEmail;
        this.reservationDate = reservationDate;
        this.slot = slot;
        this.status = status;
        this.parkingSpotCode = parkingSpotCode;
        this.checkInTime = checkInTime;
    }

    public Long getId() {
        return id;
    }

    public String getEmployeeEmail() {
        return employeeEmail;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public ReservationSlot getSlot() {
        return slot;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public String getParkingSpotCode() {
        return parkingSpotCode;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }
}