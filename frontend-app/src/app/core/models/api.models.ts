export type UserRole = 'EMPLOYEE' | 'SECRETARY' | 'MANAGER';
export type ReservationSlot = 'MORNING' | 'AFTERNOON' | 'FULL_DAY';
export type ReservationStatus = 'RESERVED' | 'CHECKED_IN' | 'RELEASED' | 'CANCELLED';
export type ParkingSpotStatus = 'AVAILABLE' | 'RESERVED' | 'CHECKED_IN';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
}

export interface ParkingSpot {
  code: string;
  rowLabel: string;
  spotNumber: number;
  electric: boolean;
  status: ParkingSpotStatus;
}

export interface Reservation {
  id: number;
  userId: number;
  userEmail: string;
  reservationDate: string;
  slot: ReservationSlot;
  status: ReservationStatus;
  parkingSpotCode: string;
  electricRequired: boolean;
  createdAt: string;
  checkInTime: string | null;
}

export interface User {
  id: number;
  email: string;
  role: UserRole;
  active: boolean;
}

export interface DashboardMetrics {
  totalSpots: number;
  electricSpots: number;
  distinctUsersLast30Days: number;
  totalReservationsLast30Days: number;
  checkedInReservationsLast30Days: number;
  noShowReservationsLast30Days: number;
  averageOccupancyRateLast30Days: number;
  noShowRateLast30Days: number;
  electricSpotRatio: number;
}
