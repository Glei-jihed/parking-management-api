package edu.backend.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Reservation {

    private Long id;
    private Long userId;
    private String userEmail;
    private LocalDate reservationDate;
    private ReservationSlot slot;
    private ReservationStatus status;
    private String parkingSpotCode;
    private boolean electricRequired;
    private LocalDateTime createdAt;
    private LocalDateTime checkInTime;

    public Reservation(
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
        this.id = id;
        this.userId = userId;
        this.userEmail = userEmail;
        this.reservationDate = reservationDate;
        this.slot = slot;
        this.status = status;
        this.parkingSpotCode = parkingSpotCode;
        this.electricRequired = electricRequired;
        this.createdAt = createdAt;
        this.checkInTime = checkInTime;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserEmail() {
        return userEmail;
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

    public boolean isElectricRequired() {
        return electricRequired;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public void setParkingSpotCode(String parkingSpotCode) {
        this.parkingSpotCode = parkingSpotCode;
    }

    public void setSlot(ReservationSlot slot) {
        this.slot = slot;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public void setElectricRequired(boolean electricRequired) {
        this.electricRequired = electricRequired;
    }
}