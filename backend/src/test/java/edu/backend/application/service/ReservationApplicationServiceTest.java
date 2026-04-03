package edu.backend.application.service;

import edu.backend.domain.exception.BusinessException;
import edu.backend.domain.exception.ForbiddenException;
import edu.backend.domain.model.ParkingSpot;
import edu.backend.domain.model.ParkingSpotStatus;
import edu.backend.domain.model.ParkingSpotView;
import edu.backend.domain.model.Reservation;
import edu.backend.domain.model.ReservationSlot;
import edu.backend.domain.model.ReservationStatus;
import edu.backend.domain.model.User;
import edu.backend.domain.model.UserRole;
import edu.backend.domain.port.out.ParkingSpotRepositoryPort;
import edu.backend.domain.port.out.ReservationRepositoryPort;
import edu.backend.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
        assertFalse(created.get(0).isElectricRequired());
    }


    @Test
    void should_fail_when_same_user_has_overlapping_reservation_on_same_day() {
        InMemoryReservationRepository reservationRepository = new InMemoryReservationRepository();

        LocalDate targetDate = nextWeekday();

        reservationRepository.save(new Reservation(
                null,
                1L,
                "employee@test.com",
                targetDate,
                ReservationSlot.MORNING,
                ReservationStatus.RESERVED,
                "B01",
                false,
                LocalDateTime.now(),
                null
        ));

        ParkingSpotRepositoryPort parkingSpotRepositoryPort =
                new InMemoryParkingSpotRepository(List.of(
                        new ParkingSpot(1L, "B01", "B", 1, false),
                        new ParkingSpot(2L, "B02", "B", 2, false)
                ));

        UserRepositoryPort userRepositoryPort = new InMemoryUserRepository();

        ReservationApplicationService service = new ReservationApplicationService(
                reservationRepository,
                parkingSpotRepositoryPort,
                userRepositoryPort
        );

        User employee = new User(1L, "employee@test.com", "x", UserRole.EMPLOYEE, true);

        assertThrows(BusinessException.class, () ->
                service.createReservations(employee, targetDate, targetDate, ReservationSlot.MORNING, false)
        );
    }

    @Test
    void should_check_in_reserved_reservation_for_owner() {
        InMemoryReservationRepository reservationRepository = new InMemoryReservationRepository();

        LocalDate today = LocalDate.now();

        Reservation saved = reservationRepository.save(new Reservation(
                null,
                1L,
                "employee@test.com",
                today,
                ReservationSlot.MORNING,
                ReservationStatus.RESERVED,
                "B01",
                false,
                LocalDateTime.now(),
                null
        ));

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

        Reservation checkedIn = service.checkIn(employee, saved.getId());

        assertEquals(ReservationStatus.CHECKED_IN, checkedIn.getStatus());
        assertNotNull(checkedIn.getCheckInTime());
    }

    @Test
    void should_cancel_reservation_for_owner() {
        InMemoryReservationRepository reservationRepository = new InMemoryReservationRepository();

        LocalDate targetDate = nextWeekday();

        Reservation saved = reservationRepository.save(new Reservation(
                null,
                1L,
                "employee@test.com",
                targetDate,
                ReservationSlot.AFTERNOON,
                ReservationStatus.RESERVED,
                "B01",
                false,
                LocalDateTime.now(),
                null
        ));

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

        Reservation cancelled = service.cancelReservation(employee, saved.getId());

        assertEquals(ReservationStatus.CANCELLED, cancelled.getStatus());
    }

    @Test
    void should_forbid_cancel_when_user_is_not_owner_and_not_secretary() {
        InMemoryReservationRepository reservationRepository = new InMemoryReservationRepository();

        LocalDate targetDate = nextWeekday();

        Reservation saved = reservationRepository.save(new Reservation(
                null,
                1L,
                "employee@test.com",
                targetDate,
                ReservationSlot.AFTERNOON,
                ReservationStatus.RESERVED,
                "B01",
                false,
                LocalDateTime.now(),
                null
        ));

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

        User otherEmployee = new User(2L, "other@test.com", "x", UserRole.EMPLOYEE, true);

        assertThrows(ForbiddenException.class, () ->
                service.cancelReservation(otherEmployee, saved.getId())
        );
    }

    @Test
    void should_return_spot_statuses_sorted_and_with_correct_status() {
        InMemoryReservationRepository reservationRepository = new InMemoryReservationRepository();
        LocalDate targetDate = nextWeekday();

        reservationRepository.save(new Reservation(
                null,
                1L,
                "employee@test.com",
                targetDate,
                ReservationSlot.MORNING,
                ReservationStatus.RESERVED,
                "B02",
                false,
                LocalDateTime.now(),
                null
        ));

        reservationRepository.save(new Reservation(
                null,
                2L,
                "employee2@test.com",
                targetDate,
                ReservationSlot.MORNING,
                ReservationStatus.CHECKED_IN,
                "A01",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        ));

        ParkingSpotRepositoryPort parkingSpotRepositoryPort =
                new InMemoryParkingSpotRepository(List.of(
                        new ParkingSpot(2L, "B02", "B", 2, false),
                        new ParkingSpot(1L, "A01", "A", 1, true),
                        new ParkingSpot(3L, "C01", "C", 1, false)
                ));

        UserRepositoryPort userRepositoryPort = new InMemoryUserRepository();

        ReservationApplicationService service = new ReservationApplicationService(
                reservationRepository,
                parkingSpotRepositoryPort,
                userRepositoryPort
        );

        List<ParkingSpotView> result = service.getParkingSpotsWithStatus(targetDate, ReservationSlot.MORNING);

        assertEquals(3, result.size());

        assertEquals("A01", result.get(0).getCode());
        assertEquals(ParkingSpotStatus.CHECKED_IN, result.get(0).getStatus());

        assertEquals("B02", result.get(1).getCode());
        assertEquals(ParkingSpotStatus.RESERVED, result.get(1).getStatus());

        assertEquals("C01", result.get(2).getCode());
        assertEquals(ParkingSpotStatus.AVAILABLE, result.get(2).getStatus());
    }


    private static LocalDate nextWeekday() {
        LocalDate date = LocalDate.now().plusDays(1);
        while (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            date = date.plusDays(1);
        }
        return date;
    }

    private static LocalDate nextMonday() {
        LocalDate date = LocalDate.now().plusDays(1);
        while (date.getDayOfWeek() != DayOfWeek.MONDAY) {
            date = date.plusDays(1);
        }
        return date;
    }


    static class InMemoryParkingSpotRepository implements ParkingSpotRepositoryPort {

        private final List<ParkingSpot> spots;

        InMemoryParkingSpotRepository(List<ParkingSpot> spots) {
            this.spots = new ArrayList<>(spots);
        }

        @Override
        public List<ParkingSpot> findAll() {
            return spots.stream()
                    .sorted(Comparator.comparing(ParkingSpot::getCode))
                    .toList();
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

            Reservation toStore;

            if (reservation.getId() == null) {
                toStore = new Reservation(
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
            } else {
                toStore = reservation;
            }

            final Long idToKeep = toStore.getId(); // ✅ FIX ICI

            data.removeIf(r -> r.getId().equals(idToKeep));
            data.add(toStore);

            return toStore;
        }

        @Override
        public Optional<Reservation> findById(Long id) {
            return data.stream()
                    .filter(r -> r.getId().equals(id))
                    .findFirst();
        }

        @Override
        public List<Reservation> findAll() {
            return new ArrayList<>(data);
        }

        @Override
        public List<Reservation> findByUserId(Long userId) {
            return data.stream()
                    .filter(r -> r.getUserId().equals(userId))
                    .toList();
        }

        @Override
        public List<Reservation> findByDate(LocalDate date) {
            return data.stream()
                    .filter(r -> r.getReservationDate().equals(date))
                    .toList();
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