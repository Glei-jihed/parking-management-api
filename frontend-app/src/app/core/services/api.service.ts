import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import {
  AuthResponse,
  DashboardMetrics,
  LoginRequest,
  ParkingSpot,
  Reservation,
  ReservationSlot,
  ReservationStatus,
  User,
  UserRole
} from '../models/api.models';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private readonly baseUrl = '/api';

  constructor(private http: HttpClient) {}

  getHealth() {
    return this.http.get<{ status: string }>(`${this.baseUrl}/health`);
  }

  login(payload: LoginRequest) {
    return this.http.post<AuthResponse>(`${this.baseUrl}/auth/login`, payload);
  }

  getParkingSpots(date: string, slot: ReservationSlot) {
    const params = new HttpParams().set('date', date).set('slot', slot);
    return this.http.get<ParkingSpot[]>(`${this.baseUrl}/parking-spots`, { params });
  }

  createReservation(payload: {
    startDate: string;
    endDate: string;
    slot: ReservationSlot;
    needsElectric: boolean;
  }) {
    return this.http.post<Reservation[]>(`${this.baseUrl}/reservations`, payload);
  }

  getMyReservations() {
    return this.http.get<Reservation[]>(`${this.baseUrl}/reservations/me`);
  }

  checkIn(reservationId: number) {
    return this.http.post(`${this.baseUrl}/reservations/${reservationId}/checkin`, {});
  }

  cancelMyReservation(reservationId: number) {
    return this.http.delete<Reservation>(`${this.baseUrl}/reservations/${reservationId}`);
  }

  getAdminReservations() {
    return this.http.get<Reservation[]>(`${this.baseUrl}/admin/reservations`);
  }

  createAdminReservation(userId: number, payload: {
    startDate: string;
    endDate: string;
    slot: ReservationSlot;
    needsElectric: boolean;
  }) {
    const params = new HttpParams().set('userId', userId);
    return this.http.post<Reservation>(`${this.baseUrl}/admin/reservations`, payload, { params });
  }

  updateAdminReservation(reservationId: number, payload: {
    reservationDate?: string;
    slot?: ReservationSlot;
    status?: ReservationStatus;
    electricRequired?: boolean;
  }) {
    return this.http.put<Reservation>(`${this.baseUrl}/admin/reservations/${reservationId}`, payload);
  }

  cancelAdminReservation(reservationId: number) {
    return this.http.delete<Reservation>(`${this.baseUrl}/admin/reservations/${reservationId}`);
  }

  getUsers() {
    return this.http.get<User[]>(`${this.baseUrl}/admin/users`);
  }

  createUser(payload: { email: string; password: string; role: UserRole }) {
    return this.http.post<User>(`${this.baseUrl}/admin/users`, payload);
  }

  toggleUserActive(userId: number, active: boolean) {
    const params = new HttpParams().set('active', active);
    return this.http.patch<User>(`${this.baseUrl}/admin/users/${userId}/active`, {}, { params });
  }

  getManagerDashboard() {
    return this.http.get<DashboardMetrics>(`${this.baseUrl}/manager/dashboard`);
  }
}
