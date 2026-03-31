package edu.backend.domain.model;

public class ParkingSpot {

    private final Long id;
    private final String code;
    private final String rowLabel;
    private final int spotNumber;
    private final boolean electric;

    public ParkingSpot(Long id, String code, String rowLabel, int spotNumber, boolean electric) {
        this.id = id;
        this.code = code;
        this.rowLabel = rowLabel;
        this.spotNumber = spotNumber;
        this.electric = electric;
    }

    public Long getId() {
        return id;
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
}