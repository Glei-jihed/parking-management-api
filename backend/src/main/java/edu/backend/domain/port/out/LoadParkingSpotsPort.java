package edu.backend.domain.port.out;

import edu.backend.domain.model.ParkingSpot;

import java.util.List;

public interface LoadParkingSpotsPort {
    List<ParkingSpot> findAll();
}