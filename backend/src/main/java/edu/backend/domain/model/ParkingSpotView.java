package edu.backend.domain.model;

public class ParkingSpotView {

    private final String code;
    private final String rowLabel;
    private final int spotNumber;
    private final boolean electric;
    private final ParkingSpotStatus status;

    public ParkingSpotView(String code, String rowLabel, int spotNumber, boolean electric, ParkingSpotStatus status) {
        this.code = code;
        this.rowLabel = rowLabel;
        this.spotNumber = spotNumber;
        this.electric = electric;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public String getRowLabel() {
        return rowLabel;
    }

    public int getSpotNumber() {
        return spotNumber;
    }

    public boolean isElectric() {
        return electric;
    }

    public ParkingSpotStatus getStatus() {
        return status;
    }
}