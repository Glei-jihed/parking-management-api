package edu.backend.domain.model;

public class DashboardMetrics {

    private final long totalSpots;
    private final long electricSpots;
    private final long distinctUsersLast30Days;
    private final long totalReservationsLast30Days;
    private final long checkedInReservationsLast30Days;
    private final long noShowReservationsLast30Days;
    private final double averageOccupancyRateLast30Days;
    private final double noShowRateLast30Days;
    private final double electricSpotRatio;

    public DashboardMetrics(
            long totalSpots,
            long electricSpots,
            long distinctUsersLast30Days,
            long totalReservationsLast30Days,
            long checkedInReservationsLast30Days,
            long noShowReservationsLast30Days,
            double averageOccupancyRateLast30Days,
            double noShowRateLast30Days,
            double electricSpotRatio
    ) {
        this.totalSpots = totalSpots;
        this.electricSpots = electricSpots;
        this.distinctUsersLast30Days = distinctUsersLast30Days;
        this.totalReservationsLast30Days = totalReservationsLast30Days;
        this.checkedInReservationsLast30Days = checkedInReservationsLast30Days;
        this.noShowReservationsLast30Days = noShowReservationsLast30Days;
        this.averageOccupancyRateLast30Days = averageOccupancyRateLast30Days;
        this.noShowRateLast30Days = noShowRateLast30Days;
        this.electricSpotRatio = electricSpotRatio;
    }

    public long getTotalSpots() {
        return totalSpots;
    }

    public long getElectricSpots() {
        return electricSpots;
    }

    public long getDistinctUsersLast30Days() {
        return distinctUsersLast30Days;
    }

    public long getTotalReservationsLast30Days() {
        return totalReservationsLast30Days;
    }

    public long getCheckedInReservationsLast30Days() {
        return checkedInReservationsLast30Days;
    }

    public long getNoShowReservationsLast30Days() {
        return noShowReservationsLast30Days;
    }

    public double getAverageOccupancyRateLast30Days() {
        return averageOccupancyRateLast30Days;
    }

    public double getNoShowRateLast30Days() {
        return noShowRateLast30Days;
    }

    public double getElectricSpotRatio() {
        return electricSpotRatio;
    }
}