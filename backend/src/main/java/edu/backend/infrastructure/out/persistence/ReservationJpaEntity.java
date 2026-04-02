package edu.backend.infrastructure.out.persistence;

import jakarta.persistence.*;
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

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate;

    @Column(nullable = false)
    private String slot;

    @Column(nullable = false)
    private String status;

    @Column(name = "parking_spot_code", nullable = false)
    private String parkingSpotCode;

    @Column(name = "electric_required", nullable = false)
    private boolean electricRequired;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;
}