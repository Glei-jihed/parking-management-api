package edu.backend.infrastructure.in.web.dto;

import edu.backend.domain.model.DashboardMetrics;

public record DashboardResponse(
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
    public static DashboardResponse from(DashboardMetrics metrics) {
        return new DashboardResponse(
                metrics.getTotalSpots(),
                metrics.getElectricSpots(),
                metrics.getDistinctUsersLast30Days(),
                metrics.getTotalReservationsLast30Days(),
                metrics.getCheckedInReservationsLast30Days(),
                metrics.getNoShowReservationsLast30Days(),
                metrics.getAverageOccupancyRateLast30Days(),
                metrics.getNoShowRateLast30Days(),
                metrics.getElectricSpotRatio()
        );
    }
}