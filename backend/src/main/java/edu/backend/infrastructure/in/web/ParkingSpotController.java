package edu.backend.infrastructure.in.web;

import edu.backend.domain.model.ReservationSlot;
import edu.backend.domain.port.in.GetParkingSpotStatusesUseCase;
import edu.backend.infrastructure.in.web.dto.ParkingSpotStatusResponse;
import edu.backend.infrastructure.in.web.mapper.WebMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/parking-spots")
public class ParkingSpotController {

    private final GetParkingSpotStatusesUseCase getParkingSpotStatusesUseCase;

    public ParkingSpotController(GetParkingSpotStatusesUseCase getParkingSpotStatusesUseCase) {
        this.getParkingSpotStatusesUseCase = getParkingSpotStatusesUseCase;
    }

    @GetMapping
    public List<ParkingSpotStatusResponse> getAll(
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) ReservationSlot slot
    ) {
        LocalDate requestedDate = date != null ? date : LocalDate.now();
        ReservationSlot requestedSlot = slot != null ? slot : ReservationSlot.AFTERNOON;

        return getParkingSpotStatusesUseCase.getParkingSpotsWithStatus(requestedDate, requestedSlot)
                .stream()
                .map(WebMapper::toParkingSpotStatusResponse)
                .toList();
    }
}