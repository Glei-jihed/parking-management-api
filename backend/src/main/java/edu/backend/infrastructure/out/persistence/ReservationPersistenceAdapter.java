package edu.backend.infrastructure.out.persistence;

import edu.backend.domain.model.Reservation;
import edu.backend.domain.model.ReservationSlot;
import edu.backend.domain.port.out.LoadReservationsPort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class ReservationPersistenceAdapter implements LoadReservationsPort {

    private final ReservationJpaRepository reservationJpaRepository;

    public ReservationPersistenceAdapter(ReservationJpaRepository reservationJpaRepository) {
        this.reservationJpaRepository = reservationJpaRepository;
    }

    @Override
    public List<Reservation> findRelevantReservations(LocalDate date, ReservationSlot slot) {
        List<String> relevantSlots = switch (slot) {
            case MORNING -> List.of("MORNING", "FULL_DAY");
            case AFTERNOON -> List.of("AFTERNOON", "FULL_DAY");
            case FULL_DAY -> List.of("FULL_DAY");
        };

        return reservationJpaRepository.findByReservationDateAndSlotIn(date, relevantSlots)
                .stream()
                .map(PersistenceMapper::toDomain)
                .toList();
    }
}