package edu.backend.infrastructure.out.persistence;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ParkingSpotJpaRepository parkingSpotJpaRepository;
    private final ReservationJpaRepository reservationJpaRepository;

    public DataInitializer(
            ParkingSpotJpaRepository parkingSpotJpaRepository,
            ReservationJpaRepository reservationJpaRepository
    ) {
        this.parkingSpotJpaRepository = parkingSpotJpaRepository;
        this.reservationJpaRepository = reservationJpaRepository;
    }

    @Override
    public void run(String... args) {
        seedParkingSpots();
        seedReservations();
    }

    private void seedParkingSpots() {
        if (parkingSpotJpaRepository.count() > 0) {
            return;
        }

        for (char row = 'A'; row <= 'F'; row++) {
            for (int number = 1; number <= 10; number++) {
                ParkingSpotJpaEntity entity = new ParkingSpotJpaEntity();
                entity.setCode(row + String.format("%02d", number));
                entity.setRowLabel(String.valueOf(row));
                entity.setSpotNumber(number);
                entity.setElectric(row == 'A' || row == 'F');

                parkingSpotJpaRepository.save(entity);
            }
        }
    }

    private void seedReservations() {
        if (reservationJpaRepository.count() > 0) {
            return;
        }

        LocalDate today = LocalDate.now();

        ReservationJpaEntity reservedAfternoon = new ReservationJpaEntity();
        reservedAfternoon.setEmployeeEmail("alice@company.com");
        reservedAfternoon.setReservationDate(today);
        reservedAfternoon.setSlot("AFTERNOON");
        reservedAfternoon.setStatus("RESERVED");
        reservedAfternoon.setParkingSpotCode("A01");
        reservedAfternoon.setCheckInTime(null);

        ReservationJpaEntity checkedInMorning = new ReservationJpaEntity();
        checkedInMorning.setEmployeeEmail("bob@company.com");
        checkedInMorning.setReservationDate(today);
        checkedInMorning.setSlot("MORNING");
        checkedInMorning.setStatus("CHECKED_IN");
        checkedInMorning.setParkingSpotCode("B03");
        checkedInMorning.setCheckInTime(LocalDateTime.now().minusHours(1));

        ReservationJpaEntity fullDayReserved = new ReservationJpaEntity();
        fullDayReserved.setEmployeeEmail("manager@company.com");
        fullDayReserved.setReservationDate(today);
        fullDayReserved.setSlot("FULL_DAY");
        fullDayReserved.setStatus("RESERVED");
        fullDayReserved.setParkingSpotCode("F02");
        fullDayReserved.setCheckInTime(null);

        reservationJpaRepository.save(reservedAfternoon);
        reservationJpaRepository.save(checkedInMorning);
        reservationJpaRepository.save(fullDayReserved);
    }
}