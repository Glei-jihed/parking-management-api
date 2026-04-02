package edu.backend.infrastructure.out.persistence;

import edu.backend.domain.model.Reservation;
import edu.backend.domain.model.ReservationSlot;
import edu.backend.domain.port.out.ReservationRepositoryPort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class ReservationPersistenceAdapter implements ReservationRepositoryPort {

    private final ReservationJpaRepository reservationJpaRepository;

    public ReservationPersistenceAdapter(ReservationJpaRepository reservationJpaRepository) {
        this.reservationJpaRepository = reservationJpaRepository;
    }

    @Override
    public Reservation save(Reservation reservation) {
        return PersistenceMapper.toDomain(
                reservationJpaRepository.save(PersistenceMapper.toJpa(reservation))
        );
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        return reservationJpaRepository.findById(id).map(PersistenceMapper::toDomain);
    }

    @Override
    public List<Reservation> findAll() {
        return reservationJpaRepository.findAll().stream().map(PersistenceMapper::toDomain).toList();
    }

    @Override
    public List<Reservation> findByUserId(Long userId) {
        return reservationJpaRepository.findByUserId(userId).stream().map(PersistenceMapper::toDomain).toList();
    }

    @Override
    public List<Reservation> findByDate(LocalDate date) {
        return reservationJpaRepository.findByReservationDate(date).stream().map(PersistenceMapper::toDomain).toList();
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

    @Override
    public List<Reservation> findBetween(LocalDate startDate, LocalDate endDate) {
        return reservationJpaRepository.findByReservationDateBetween(startDate, endDate)
                .stream()
                .map(PersistenceMapper::toDomain)
                .toList();
    }
}