package edu.backend.application.service;

import edu.backend.domain.exception.BusinessException;
import edu.backend.domain.exception.ForbiddenException;
import edu.backend.domain.exception.NotFoundException;
import edu.backend.domain.model.DashboardMetrics;
import edu.backend.domain.model.ParkingSpot;
import edu.backend.domain.model.Reservation;
import edu.backend.domain.model.ReservationStatus;
import edu.backend.domain.model.User;
import edu.backend.domain.model.UserRole;
import edu.backend.domain.port.out.ParkingSpotRepositoryPort;
import edu.backend.domain.port.out.PasswordHasherPort;
import edu.backend.domain.port.out.ReservationRepositoryPort;
import edu.backend.domain.port.out.UserRepositoryPort;

import java.time.LocalDate;
import java.util.List;

public class ManagementApplicationService {

    private final UserRepositoryPort userRepositoryPort;
    private final ReservationRepositoryPort reservationRepositoryPort;
    private final ParkingSpotRepositoryPort parkingSpotRepositoryPort;
    private final PasswordHasherPort passwordHasherPort;

    public ManagementApplicationService(
            UserRepositoryPort userRepositoryPort,
            ReservationRepositoryPort reservationRepositoryPort,
            ParkingSpotRepositoryPort parkingSpotRepositoryPort,
            PasswordHasherPort passwordHasherPort
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.reservationRepositoryPort = reservationRepositoryPort;
        this.parkingSpotRepositoryPort = parkingSpotRepositoryPort;
        this.passwordHasherPort = passwordHasherPort;
    }

    public List<User> getAllUsers(User currentUser) {
        requireSecretary(currentUser);
        return userRepositoryPort.findAll();
    }

    public User createUser(User currentUser, String email, String password, UserRole role) {
        requireSecretary(currentUser);

        if (userRepositoryPort.existsByEmail(email)) {
            throw new BusinessException("Email already exists");
        }

        User user = new User(
                null,
                email,
                passwordHasherPort.hash(password),
                role,
                true
        );

        return userRepositoryPort.save(user);
    }

    public User toggleUserActive(User currentUser, Long userId, boolean active) {
        requireSecretary(currentUser);

        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setActive(active);
        return userRepositoryPort.save(user);
    }

    public DashboardMetrics getDashboard(User currentUser) {
        requireManagerOrSecretary(currentUser);

        List<ParkingSpot> spots = parkingSpotRepositoryPort.findAll();
        List<Reservation> reservations = reservationRepositoryPort.findBetween(LocalDate.now().minusDays(29), LocalDate.now());

        long totalSpots = spots.size();
        long electricSpots = spots.stream().filter(ParkingSpot::isElectric).count();

        long totalReservations = reservations.size();
        long checkedIn = reservations.stream().filter(r -> r.getStatus() == ReservationStatus.CHECKED_IN).count();
        long noShow = reservations.stream().filter(r -> r.getStatus() == ReservationStatus.RELEASED).count();
        long distinctUsers = reservations.stream().map(Reservation::getUserId).distinct().count();

        double averageOccupancy = totalReservations == 0
                ? 0.0
                : Math.min(100.0, (totalReservations / (double) (30 * 60 * 2)) * 100.0);

        double noShowRate = totalReservations == 0
                ? 0.0
                : (noShow / (double) totalReservations) * 100.0;

        double electricRatio = totalSpots == 0
                ? 0.0
                : (electricSpots / (double) totalSpots) * 100.0;

        return new DashboardMetrics(
                totalSpots,
                electricSpots,
                distinctUsers,
                totalReservations,
                checkedIn,
                noShow,
                round(averageOccupancy),
                round(noShowRate),
                round(electricRatio)
        );
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private void requireSecretary(User currentUser) {
        if (currentUser == null || !currentUser.isActive() || currentUser.getRole() != UserRole.SECRETARY) {
            throw new ForbiddenException("Secretary role required");
        }
    }

    private void requireManagerOrSecretary(User currentUser) {
        if (currentUser == null || !currentUser.isActive()) {
            throw new ForbiddenException("Active user required");
        }
        if (currentUser.getRole() != UserRole.MANAGER && currentUser.getRole() != UserRole.SECRETARY) {
            throw new ForbiddenException("Manager or secretary role required");
        }
    }
}