import { Component, OnInit, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ApiService, ParkingSpot, ReservationSlot } from './core/services/api.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App implements OnInit {
  title = signal('Parking Management');
  backendStatus = signal('Loading...');
  parkingSpots = signal<ParkingSpot[]>([]);

  selectedDate = signal(this.getTodayAsString());
  selectedSlot = signal<ReservationSlot>('AFTERNOON');

  loadingParkingSpots = signal(false);
  errorMessage = signal('');

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.loadHealth();
    this.loadParkingSpots();
  }

  onDateChange(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.selectedDate.set(value);
    this.loadParkingSpots();
  }

  onSlotChange(event: Event): void {
    const value = (event.target as HTMLSelectElement).value as ReservationSlot;
    this.selectedSlot.set(value);
    this.loadParkingSpots();
  }

  getAvailableCount(): number {
    return this.parkingSpots().filter(s => s.status === 'AVAILABLE').length;
  }

  getReservedCount(): number {
    return this.parkingSpots().filter(s => s.status === 'RESERVED').length;
  }

  getCheckedInCount(): number {
    return this.parkingSpots().filter(s => s.status === 'CHECKED_IN').length;
  }

  getElectricCount(): number {
    return this.parkingSpots().filter(s => s.electric).length;
  }

  getStatusLabel(status: string): string {
    switch (status) {
      case 'AVAILABLE': return 'Available';
      case 'RESERVED': return 'Reserved';
      case 'CHECKED_IN': return 'Checked-in';
      default: return status;
    }
  }

  private loadHealth(): void {
    this.apiService.getHealth().subscribe({
      next: (response) => this.backendStatus.set(response.status),
      error: (error) => {
        console.error('Health error:', error);
        this.backendStatus.set('DOWN');
      }
    });
  }

  private loadParkingSpots(): void {
    this.loadingParkingSpots.set(true);
    this.errorMessage.set('');

    this.apiService.getParkingSpots(this.selectedDate(), this.selectedSlot()).subscribe({
      next: (spots) => {
        this.parkingSpots.set(spots);
        this.loadingParkingSpots.set(false);
      },
      error: (error) => {
        console.error('Parking spots error:', error);
        this.parkingSpots.set([]);
        this.loadingParkingSpots.set(false);
        this.errorMessage.set('Unable to load parking spots.');
      }
    });
  }

  private getTodayAsString(): string {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}
