package edu.backend.infrastructure.in.web.mapper;

import edu.backend.domain.model.ParkingSpotView;
import edu.backend.infrastructure.in.web.dto.ParkingSpotStatusResponse;

public class WebMapper {

    private WebMapper() {}

    public static ParkingSpotStatusResponse toParkingSpotStatusResponse(ParkingSpotView view) {
        return new ParkingSpotStatusResponse(
                view.getCode(),
                view.getRowLabel(),
                view.getSpotNumber(),
                view.isElectric(),
                view.getStatus().name()
        );
    }
}