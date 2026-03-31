package edu.backend.infrastructure.out.persistence;

import edu.backend.domain.model.ParkingSpot;
import edu.backend.domain.model.Reservation;
import edu.backend.domain.model.ReservationSlot;
import edu.backend.domain.model.ReservationStatus;

public class PersistenceMapper {

    private PersistenceMapper() {
    }

    public static ParkingSpot toDomain(ParkingSpotJpaEntity entity) {
        return new ParkingSpot(
                entity.getId(),
                entity.getCode(),
                entity.getRowLabel(),
                entity.getSpotNumber(),
                entity.isElectric()
        );
    }

    public static Reservation toDomain(ReservationJpaEntity entity) {
        return new Reservation(
                entity.getId(),
                entity.getEmployeeEmail(),
                entity.getReservationDate(),
                ReservationSlot.valueOf(entity.getSlot()),
                ReservationStatus.valueOf(entity.getStatus()),
                entity.getParkingSpotCode(),
                entity.getCheckInTime()
        );
    }
}