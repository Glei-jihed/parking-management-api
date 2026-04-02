package edu.backend.application.service;

import edu.backend.domain.exception.BusinessException;
import edu.backend.domain.exception.ForbiddenException;
import edu.backend.domain.exception.NotFoundException;
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
import org.springframework.scheduling.annotation.Scheduled;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReservationApplicationService {

    private final ReservationRepositoryPort reservationRepositoryPort;
    private final ParkingSpotRepositoryPort parkingSpotRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;

    public ReservationApplicationService(
            ReservationRepositoryPort reservationRepositoryPort,
            ParkingSpotRepositoryPort parkingSpotRepositoryPort,
            UserRepositoryPort userRepositoryPort
    ) {
        this.reservationRepositoryPort = reservationRepositoryPort;
        this.parkingSpotRepositoryPort = parkingSpotRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
    }

    public List<ParkingSpotView> getParkingSpotsWithStatus(LocalDate date, ReservationSlot slot) {
        List<ParkingSpot> parkingSpots = parkingSpotRepositoryPort.findAll();
        List<Reservation> reservations = reservationRepositoryPort.findRelevantReservations(date, slot);

        Map<String, Reservation> reservationBySpotCode = reservations.stream()
                .filter(this::blocksSpot)
                .collect(Collectors.toMap(
                        Reservation::getParkingSpotCode,
                        Function.identity(),
                        (first, second) -> first
                ));

        return parkingSpots.stream()
                .map(spot -> new ParkingSpotView(
                        spot.getCode(),
                        spot.getRowLabel(),
                        spot.getSpotNumber(),
                        spot.isElectric(),
                        toSpotStatus(reservationBySpotCode.get(spot.getCode()))
                ))
                .sorted(Comparator.comparing(ParkingSpotView::getCode))
                .toList();
    }

    public List<Reservation> createReservations(
            User currentUser,
            LocalDate startDate,
            LocalDate endDate,
            ReservationSlot slot,
            boolean needsElectric
    ) {
        requireActiveUser(currentUser);

        if (startDate == null || endDate == null) {
            throw new BusinessException("Start date and end date are required");
        }
        if (endDate.isBefore(startDate)) {
            throw new BusinessException("End date cannot be before start date");
        }
        if (startDate.isBefore(LocalDate.now())) {
            throw new BusinessException("Reservation cannot start in the past");
        }

        List<LocalDate> requestedWorkingDays = collectWorkingDays(startDate, endDate);

        if (requestedWorkingDays.isEmpty()) {
            throw new BusinessException("No working day selected");
        }

        int maxDays = currentUser.getRole() == UserRole.MANAGER ? 30 : 5;
        if (requestedWorkingDays.size() > maxDays) {
            throw new BusinessException("Reservation limit exceeded for your role");
        }

        List<Reservation> created = new ArrayList<>();
        for (LocalDate day : requestedWorkingDays) {
            ensureUserHasNoOverlap(currentUser, day, slot);

            ParkingSpot assignedSpot = findAvailableSpot(day, slot, needsElectric)
                    .orElseThrow(() -> new BusinessException("No compatible parking spot available for " + day));

            Reservation reservation = new Reservation(
                    null,
                    currentUser.getId(),
                    currentUser.getEmail(),
                    day,
                    slot,
                    ReservationStatus.RESERVED,
                    assignedSpot.getCode(),
                    needsElectric,
                    LocalDateTime.now(),
                    null
            );

            created.add(reservationRepositoryPort.save(reservation));
        }

        return created;
    }

    public List<Reservation> getMyReservations(User currentUser) {
        requireActiveUser(currentUser);
        return reservationRepositoryPort.findByUserId(currentUser.getId()).stream()
                .sorted(Comparator.comparing(Reservation::getReservationDate).thenComparing(Reservation::getSlot))
                .toList();
    }

    public Reservation checkIn(User currentUser, Long reservationId) {
        requireActiveUser(currentUser);

        Reservation reservation = reservationRepositoryPort.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Reservation not found"));

        if (!reservation.getUserId().equals(currentUser.getId())
                && currentUser.getRole() != UserRole.SECRETARY) {
            throw new ForbiddenException("You cannot check in this reservation");
        }

        if (reservation.getStatus() == ReservationStatus.CANCELLED
                || reservation.getStatus() == ReservationStatus.RELEASED) {
            throw new BusinessException("Reservation is no longer valid");
        }

        if (!reservation.getReservationDate().equals(LocalDate.now())) {
            throw new BusinessException("Check-in is only allowed on reservation day");
        }

        reservation.setStatus(ReservationStatus.CHECKED_IN);
        reservation.setCheckInTime(LocalDateTime.now());

        return reservationRepositoryPort.save(reservation);
    }

    public Reservation cancelReservation(User currentUser, Long reservationId) {
        requireActiveUser(currentUser);

        Reservation reservation = reservationRepositoryPort.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Reservation not found"));

        boolean isOwner = reservation.getUserId().equals(currentUser.getId());
        boolean isSecretary = currentUser.getRole() == UserRole.SECRETARY;

        if (!isOwner && !isSecretary) {
            throw new ForbiddenException("You cannot cancel this reservation");
        }

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            return reservation;
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        return reservationRepositoryPort.save(reservation);
    }

    public List<Reservation> getAllReservationsForAdmin(User currentUser) {
        requireSecretary(currentUser);
        return reservationRepositoryPort.findAll().stream()
                .sorted(Comparator.comparing(Reservation::getReservationDate).thenComparing(Reservation::getSlot))
                .toList();
    }

    public Reservation createReservationAsAdmin(
            User currentUser,
            Long userId,
            LocalDate date,
            ReservationSlot slot,
            boolean needsElectric
    ) {
        requireSecretary(currentUser);

        User targetUser = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new NotFoundException("Target user not found"));

        ParkingSpot assignedSpot = findAvailableSpot(date, slot, needsElectric)
                .orElseThrow(() -> new BusinessException("No compatible parking spot available"));

        Reservation reservation = new Reservation(
                null,
                targetUser.getId(),
                targetUser.getEmail(),
                date,
                slot,
                ReservationStatus.RESERVED,
                assignedSpot.getCode(),
                needsElectric,
                LocalDateTime.now(),
                null
        );

        return reservationRepositoryPort.save(reservation);
    }

    public Reservation updateReservationAsAdmin(
            User currentUser,
            Long reservationId,
            LocalDate date,
            ReservationSlot slot,
            ReservationStatus status,
            Boolean electricRequired
    ) {
        requireSecretary(currentUser);

        Reservation reservation = reservationRepositoryPort.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Reservation not found"));

        boolean finalElectric = electricRequired != null ? electricRequired : reservation.isElectricRequired();
        LocalDate finalDate = date != null ? date : reservation.getReservationDate();
        ReservationSlot finalSlot = slot != null ? slot : reservation.getSlot();
        ReservationStatus finalStatus = status != null ? status : reservation.getStatus();

        if (!reservation.getReservationDate().equals(finalDate) || reservation.getSlot() != finalSlot
                || reservation.isElectricRequired() != finalElectric) {
            ParkingSpot newSpot = findAvailableSpotExcludingReservation(finalDate, finalSlot, finalElectric, reservation.getId())
                    .orElseThrow(() -> new BusinessException("No compatible parking spot available for update"));
            reservation.setParkingSpotCode(newSpot.getCode());
            reservation.setReservationDate(finalDate);
            reservation.setSlot(finalSlot);
            reservation.setElectricRequired(finalElectric);
        }

        reservation.setStatus(finalStatus);

        if (finalStatus != ReservationStatus.CHECKED_IN) {
            reservation.setCheckInTime(null);
        } else if (reservation.getCheckInTime() == null) {
            reservation.setCheckInTime(LocalDateTime.now());
        }

        return reservationRepositoryPort.save(reservation);
    }

    @Scheduled(cron = "0 */10 * * * *")
    public void releaseExpiredMorningReservations() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        if (now.isBefore(LocalTime.of(11, 0))) {
            return;
        }

        List<Reservation> todayReservations = reservationRepositoryPort.findByDate(today);

        for (Reservation reservation : todayReservations) {
            boolean shouldRelease =
                    reservation.getStatus() == ReservationStatus.RESERVED
                            && reservation.getCheckInTime() == null
                            && (reservation.getSlot() == ReservationSlot.MORNING
                            || reservation.getSlot() == ReservationSlot.FULL_DAY);

            if (shouldRelease) {
                reservation.setStatus(ReservationStatus.RELEASED);
                reservationRepositoryPort.save(reservation);
            }
        }
    }

    private void ensureUserHasNoOverlap(User user, LocalDate date, ReservationSlot requestedSlot) {
        List<Reservation> sameDayReservations = reservationRepositoryPort.findByUserId(user.getId()).stream()
                .filter(r -> r.getReservationDate().equals(date))
                .filter(r -> r.getStatus() != ReservationStatus.CANCELLED)
                .filter(r -> r.getStatus() != ReservationStatus.RELEASED)
                .toList();

        boolean overlap = sameDayReservations.stream().anyMatch(existing -> overlaps(existing.getSlot(), requestedSlot));

        if (overlap) {
            throw new BusinessException("User already has a reservation on " + date + " for the selected slot");
        }
    }

    private java.util.Optional<ParkingSpot> findAvailableSpot(LocalDate date, ReservationSlot slot, boolean needsElectric) {
        return findAvailableSpotExcludingReservation(date, slot, needsElectric, null);
    }

    private java.util.Optional<ParkingSpot> findAvailableSpotExcludingReservation(
            LocalDate date,
            ReservationSlot slot,
            boolean needsElectric,
            Long excludedReservationId
    ) {
        List<ParkingSpot> allSpots = parkingSpotRepositoryPort.findAll().stream()
                .filter(spot -> needsElectric == spot.isElectric())
                .sorted(Comparator.comparing(ParkingSpot::getCode))
                .toList();

        List<Reservation> reservations = reservationRepositoryPort.findRelevantReservations(date, slot);

        List<String> blockedSpotCodes = reservations.stream()
                .filter(this::blocksSpot)
                .filter(r -> excludedReservationId == null || !r.getId().equals(excludedReservationId))
                .map(Reservation::getParkingSpotCode)
                .toList();

        return allSpots.stream()
                .filter(spot -> !blockedSpotCodes.contains(spot.getCode()))
                .findFirst();
    }

    private boolean blocksSpot(Reservation reservation) {
        return reservation.getStatus() == ReservationStatus.RESERVED
                || reservation.getStatus() == ReservationStatus.CHECKED_IN;
    }

    private ParkingSpotStatus toSpotStatus(Reservation reservation) {
        if (reservation == null) {
            return ParkingSpotStatus.AVAILABLE;
        }
        if (reservation.getStatus() == ReservationStatus.CHECKED_IN) {
            return ParkingSpotStatus.CHECKED_IN;
        }
        if (reservation.getStatus() == ReservationStatus.RESERVED) {
            return ParkingSpotStatus.RESERVED;
        }
        return ParkingSpotStatus.AVAILABLE;
    }

    private boolean overlaps(ReservationSlot a, ReservationSlot b) {
        if (a == ReservationSlot.FULL_DAY || b == ReservationSlot.FULL_DAY) {
            return true;
        }
        return a == b;
    }

    private List<LocalDate> collectWorkingDays(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> days = new ArrayList<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            if (isWorkingDay(current)) {
                days.add(current);
            }
            current = current.plusDays(1);
        }
        return days;
    }

    private boolean isWorkingDay(LocalDate date) {
        return date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY;
    }

    private void requireSecretary(User user) {
        requireActiveUser(user);
        if (user.getRole() != UserRole.SECRETARY) {
            throw new ForbiddenException("Secretary role required");
        }
    }

    private void requireActiveUser(User user) {
        if (user == null || !user.isActive()) {
            throw new ForbiddenException("Active user required");
        }
    }
}