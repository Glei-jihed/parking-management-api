import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export type ReservationSlot = 'MORNING' | 'AFTERNOON' | 'FULL_DAY';
export type ParkingSpotStatus = 'AVAILABLE' | 'RESERVED' | 'CHECKED_IN';

export interface ParkingSpot {
  code: string;
  rowLabel: string;
  spotNumber: number;
  electric: boolean;
  status: ParkingSpotStatus;
}

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private readonly baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  getHealth(): Observable<{ status: string }> {
    return this.http.get<{ status: string }>(`${this.baseUrl}/health`);
  }

  getParkingSpots(date: string, slot: ReservationSlot): Observable<ParkingSpot[]> {
    const params = new HttpParams()
      .set('date', date)
      .set('slot', slot);

    return this.http.get<ParkingSpot[]>(`${this.baseUrl}/parking-spots`, { params });
  }
}
