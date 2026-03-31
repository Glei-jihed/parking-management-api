package edu.backend.infrastructure.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
public class ReservationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_email", nullable = false)
    private String employeeEmail;

    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate;

    @Column(nullable = false)
    private String slot;

    @Column(nullable = false)
    private String status;

    @Column(name = "parking_spot_code", nullable = false)
    private String parkingSpotCode;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;
}