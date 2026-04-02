package edu.backend.infrastructure.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationJpaRepository extends JpaRepository<ReservationJpaEntity, Long> {
    List<ReservationJpaEntity> findByUserId(Long userId);
    List<ReservationJpaEntity> findByReservationDate(LocalDate reservationDate);
    List<ReservationJpaEntity> findByReservationDateAndSlotIn(LocalDate reservationDate, List<String> slots);
    List<ReservationJpaEntity> findByReservationDateBetween(LocalDate startDate, LocalDate endDate);
}