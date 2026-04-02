package edu.backend.infrastructure.out.persistence;

import edu.backend.domain.model.*;

public final class PersistenceMapper {

    private PersistenceMapper() {
    }

    public static User toDomain(UserJpaEntity entity) {
        return new User(
                entity.getId(),
                entity.getEmail(),
                entity.getPassword(),
                UserRole.valueOf(entity.getRole()),
                entity.isActive()
        );
    }

    public static UserJpaEntity toJpa(User user) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(user.getId());
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPassword());
        entity.setRole(user.getRole().name());
        entity.setActive(user.isActive());
        return entity;
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
                entity.getUserId(),
                entity.getUserEmail(),
                entity.getReservationDate(),
                ReservationSlot.valueOf(entity.getSlot()),
                ReservationStatus.valueOf(entity.getStatus()),
                entity.getParkingSpotCode(),
                entity.isElectricRequired(),
                entity.getCreatedAt(),
                entity.getCheckInTime()
        );
    }

    public static ReservationJpaEntity toJpa(Reservation reservation) {
        ReservationJpaEntity entity = new ReservationJpaEntity();
        entity.setId(reservation.getId());
        entity.setUserId(reservation.getUserId());
        entity.setUserEmail(reservation.getUserEmail());
        entity.setReservationDate(reservation.getReservationDate());
        entity.setSlot(reservation.getSlot().name());
        entity.setStatus(reservation.getStatus().name());
        entity.setParkingSpotCode(reservation.getParkingSpotCode());
        entity.setElectricRequired(reservation.isElectricRequired());
        entity.setCreatedAt(reservation.getCreatedAt());
        entity.setCheckInTime(reservation.getCheckInTime());
        return entity;
    }
}