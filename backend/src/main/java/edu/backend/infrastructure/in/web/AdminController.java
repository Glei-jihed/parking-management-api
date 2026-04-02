package edu.backend.infrastructure.in.web;

import edu.backend.application.service.ManagementApplicationService;
import edu.backend.application.service.ReservationApplicationService;
import edu.backend.infrastructure.in.web.dto.AdminCreateUserRequest;
import edu.backend.infrastructure.in.web.dto.CreateReservationRequest;
import edu.backend.infrastructure.in.web.dto.ReservationResponse;
import edu.backend.infrastructure.in.web.dto.UpdateReservationAdminRequest;
import edu.backend.infrastructure.in.web.dto.UserResponse;
import edu.backend.infrastructure.out.security.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ReservationApplicationService reservationApplicationService;
    private final ManagementApplicationService managementApplicationService;

    public AdminController(
            ReservationApplicationService reservationApplicationService,
            ManagementApplicationService managementApplicationService
    ) {
        this.reservationApplicationService = reservationApplicationService;
        this.managementApplicationService = managementApplicationService;
    }

    @GetMapping("/reservations")
    public List<ReservationResponse> getAllReservations(@AuthenticationPrincipal CurrentUser currentUser) {
        return reservationApplicationService.getAllReservationsForAdmin(currentUser.toDomainUser()).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @PostMapping("/reservations")
    public ReservationResponse createReservationAsAdmin(
            @AuthenticationPrincipal CurrentUser currentUser,
            @RequestParam Long userId,
            @Valid @RequestBody CreateReservationRequest request
    ) {
        if (!request.startDate().equals(request.endDate())) {
            throw new IllegalArgumentException("Admin creation endpoint accepts one day at a time");
        }

        return ReservationResponse.from(
                reservationApplicationService.createReservationAsAdmin(
                        currentUser.toDomainUser(),
                        userId,
                        request.startDate(),
                        request.slot(),
                        request.needsElectric()
                )
        );
    }

    @PutMapping("/reservations/{id}")
    public ReservationResponse updateReservationAsAdmin(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long id,
            @RequestBody UpdateReservationAdminRequest request
    ) {
        return ReservationResponse.from(
                reservationApplicationService.updateReservationAsAdmin(
                        currentUser.toDomainUser(),
                        id,
                        request.reservationDate(),
                        request.slot(),
                        request.status(),
                        request.electricRequired()
                )
        );
    }

    @DeleteMapping("/reservations/{id}")
    public ReservationResponse cancelReservationAsAdmin(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long id
    ) {
        return ReservationResponse.from(
                reservationApplicationService.cancelReservation(currentUser.toDomainUser(), id)
        );
    }

    @GetMapping("/users")
    public List<UserResponse> getUsers(@AuthenticationPrincipal CurrentUser currentUser) {
        return managementApplicationService.getAllUsers(currentUser.toDomainUser()).stream()
                .map(UserResponse::from)
                .toList();
    }

    @PostMapping("/users")
    public UserResponse createUser(
            @AuthenticationPrincipal CurrentUser currentUser,
            @Valid @RequestBody AdminCreateUserRequest request
    ) {
        return UserResponse.from(
                managementApplicationService.createUser(
                        currentUser.toDomainUser(),
                        request.email(),
                        request.password(),
                        request.role()
                )
        );
    }

    @PatchMapping("/users/{id}/active")
    public UserResponse toggleUserActive(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long id,
            @RequestParam boolean active
    ) {
        return UserResponse.from(
                managementApplicationService.toggleUserActive(currentUser.toDomainUser(), id, active)
        );
    }
}