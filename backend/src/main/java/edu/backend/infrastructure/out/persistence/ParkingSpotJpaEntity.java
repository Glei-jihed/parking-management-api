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

@Entity
@Table(name = "parking_spots")
@Getter
@Setter
@NoArgsConstructor
public class ParkingSpotJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(name = "row_label", nullable = false)
    private String rowLabel;

    @Column(name = "spot_number", nullable = false)
    private int spotNumber;

    @Column(nullable = false)
    private boolean electric;
}