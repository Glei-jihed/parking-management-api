package edu.backend.application.service;

import edu.backend.domain.exception.BusinessException;
import edu.backend.domain.model.*;
import edu.backend.domain.port.out.ParkingSpotRepositoryPort;
import edu.backend.domain.port.out.ReservationRepositoryPort;
import edu.backend.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class ReservationApplicationServiceTest {

    @Test
    void should_create_reservation_for_employee_on_available_non_electric_spot() {

        InMemoryReservationRepository reservationRepository = new InMemoryReservationRepository();

        ParkingSpotRepositoryPort parkingSpotRepositoryPort =
                new InMemoryParkingSpotRepository(List.of(
                        new ParkingSpot(1L, "B01", "B", 1, false),
                        new ParkingSpot(2L, "A01", "A", 1, true)
                ));

        UserRepositoryPort userRepositoryPort = new InMemoryUserRepository();

        ReservationApplicationService service = new ReservationApplicationService(
                reservationRepository,
                parkingSpotRepositoryPort,
                userRepositoryPort
        );

        User employee = new User(1L, "employee@test.com", "x", UserRole.EMPLOYEE, true);

        LocalDate nextWorkingDay = nextWeekday();

        List<Reservation> created = service.createReservations(
                employee,
                nextWorkingDay,
                nextWorkingDay,
                ReservationSlot.MORNING,
                false
        );

        assertEquals(1, created.size());
        assertEquals("B01", created.get(0).getParkingSpotCode());
        assertEquals(ReservationStatus.RESERVED, created.get(0).getStatus());
    }

    @Test
    void should_fail_when_employee_requests_more_than_five_working_days() {

        InMemoryReservationRepository reservationRepository = new InMemoryReservationRepository();

        ParkingSpotRepositoryPort parkingSpotRepositoryPort =
                new InMemoryParkingSpotRepository(List.of(
                        new ParkingSpot(1L, "B01", "B", 1, false)
                ));

        UserRepositoryPort userRepositoryPort = new InMemoryUserRepository();

        ReservationApplicationService service = new ReservationApplicationService(
                reservationRepository,
                parkingSpotRepositoryPort,
                userRepositoryPort
        );

        User employee = new User(1L, "employee@test.com", "x", UserRole.EMPLOYEE, true);

        LocalDate start = nextMonday();
        LocalDate end = start.plusDays(6);

        assertThrows(BusinessException.class, () ->
                service.createReservations(employee, start, end, ReservationSlot.MORNING, false)
        );
    }

    // ==========================
    // Helpers
    // ==========================

    private static LocalDate nextWeekday() {
        LocalDate date = LocalDate.now().plusDays(1);
        while (date.getDayOfWeek().getValue() > 5) {
            date = date.plusDays(1);
        }
        return date;
    }

    private static LocalDate nextMonday() {
        LocalDate date = LocalDate.now().plusDays(1);
        while (date.getDayOfWeek() != java.time.DayOfWeek.MONDAY) {
            date = date.plusDays(1);
        }
        return date;
    }

    // ==========================
    // In-memory adapters
    // ==========================

    static class InMemoryParkingSpotRepository implements ParkingSpotRepositoryPort {

        private final List<ParkingSpot> spots;

        InMemoryParkingSpotRepository(List<ParkingSpot> spots) {
            this.spots = spots;
        }

        @Override
        public List<ParkingSpot> findAll() {
            return spots;
        }

        @Override
        public Optional<ParkingSpot> findByCode(String code) {
            return spots.stream()
                    .filter(s -> s.getCode().equals(code))
                    .findFirst();
        }
    }

    static class InMemoryReservationRepository implements ReservationRepositoryPort {

        private final List<Reservation> data = new ArrayList<>();
        private final AtomicLong seq = new AtomicLong(1);

        @Override
        public Reservation save(Reservation reservation) {
            if (reservation.getId() == null) {
                Reservation stored = new Reservation(
                        seq.getAndIncrement(),
                        reservation.getUserId(),
                        reservation.getUserEmail(),
                        reservation.getReservationDate(),
                        reservation.getSlot(),
                        reservation.getStatus(),
                        reservation.getParkingSpotCode(),
                        reservation.isElectricRequired(),
                        reservation.getCreatedAt() != null ? reservation.getCreatedAt() : LocalDateTime.now(),
                        reservation.getCheckInTime()
                );
                data.add(stored);
                return stored;
            }
            data.removeIf(r -> r.getId().equals(reservation.getId()));
            data.add(reservation);
            return reservation;
        }

        @Override
        public Optional<Reservation> findById(Long id) {
            return data.stream().filter(r -> r.getId().equals(id)).findFirst();
        }

        @Override
        public List<Reservation> findAll() {
            return new ArrayList<>(data);
        }

        @Override
        public List<Reservation> findByUserId(Long userId) {
            return data.stream().filter(r -> r.getUserId().equals(userId)).toList();
        }

        @Override
        public List<Reservation> findByDate(LocalDate date) {
            return data.stream().filter(r -> r.getReservationDate().equals(date)).toList();
        }

        @Override
        public List<Reservation> findRelevantReservations(LocalDate date, ReservationSlot slot) {
            return data.stream()
                    .filter(r -> r.getReservationDate().equals(date))
                    .filter(r -> switch (slot) {
                        case MORNING -> r.getSlot() == ReservationSlot.MORNING || r.getSlot() == ReservationSlot.FULL_DAY;
                        case AFTERNOON -> r.getSlot() == ReservationSlot.AFTERNOON || r.getSlot() == ReservationSlot.FULL_DAY;
                        case FULL_DAY -> r.getSlot() == ReservationSlot.FULL_DAY;
                    })
                    .toList();
        }

        @Override
        public List<Reservation> findBetween(LocalDate startDate, LocalDate endDate) {
            return data.stream()
                    .filter(r -> !r.getReservationDate().isBefore(startDate)
                            && !r.getReservationDate().isAfter(endDate))
                    .toList();
        }
    }

    static class InMemoryUserRepository implements UserRepositoryPort {

        @Override
        public Optional<User> findByEmail(String email) {
            return Optional.empty();
        }

        @Override
        public Optional<User> findById(Long id) {
            return Optional.empty();
        }

        @Override
        public List<User> findAll() {
            return List.of();
        }

        @Override
        public User save(User user) {
            return user;
        }

        @Override
        public boolean existsByEmail(String email) {
            return false;
        }
    }
}