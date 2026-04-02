package edu.backend.infrastructure.in.web;

import edu.backend.application.service.ReservationApplicationService;
import edu.backend.domain.model.ParkingSpotView;
import edu.backend.domain.model.Reservation;
import edu.backend.domain.model.ReservationSlot;
import edu.backend.infrastructure.in.web.dto.CheckInResponse;
import edu.backend.infrastructure.in.web.dto.CreateReservationRequest;
import edu.backend.infrastructure.in.web.dto.ParkingSpotStatusResponse;
import edu.backend.infrastructure.in.web.dto.ReservationResponse;
import edu.backend.infrastructure.out.security.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ReservationController {

    private final ReservationApplicationService reservationApplicationService;

    public ReservationController(ReservationApplicationService reservationApplicationService) {
        this.reservationApplicationService = reservationApplicationService;
    }

    @GetMapping("/parking-spots")
    public List<ParkingSpotStatusResponse> getParkingSpots(
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) ReservationSlot slot
    ) {
        LocalDate requestedDate = date != null ? date : LocalDate.now();
        ReservationSlot requestedSlot = slot != null ? slot : ReservationSlot.MORNING;

        List<ParkingSpotView> views = reservationApplicationService.getParkingSpotsWithStatus(requestedDate, requestedSlot);

        return views.stream()
                .map(view -> new ParkingSpotStatusResponse(
                        view.getCode(),
                        view.getRowLabel(),
                        view.getSpotNumber(),
                        view.isElectric(),
                        view.getStatus().name()
                ))
                .toList();
    }

    @PostMapping("/reservations")
    public List<ReservationResponse> createReservations(
            @AuthenticationPrincipal CurrentUser currentUser,
            @Valid @RequestBody CreateReservationRequest request
    ) {
        return reservationApplicationService.createReservations(
                        currentUser.toDomainUser(),
                        request.startDate(),
                        request.endDate(),
                        request.slot(),
                        request.needsElectric()
                ).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @GetMapping("/reservations/me")
    public List<ReservationResponse> getMyReservations(@AuthenticationPrincipal CurrentUser currentUser) {
        return reservationApplicationService.getMyReservations(currentUser.toDomainUser()).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @PostMapping("/reservations/{id}/checkin")
    public CheckInResponse checkIn(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long id
    ) {
        Reservation reservation = reservationApplicationService.checkIn(currentUser.toDomainUser(), id);
        return new CheckInResponse(reservation.getId(), reservation.getStatus().name(), reservation.getCheckInTime());
    }

    @DeleteMapping("/reservations/{id}")
    public ReservationResponse cancelReservation(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long id
    ) {
        return ReservationResponse.from(
                reservationApplicationService.cancelReservation(currentUser.toDomainUser(), id)
        );
    }
}