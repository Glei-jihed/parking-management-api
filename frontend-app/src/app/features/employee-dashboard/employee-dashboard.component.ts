import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { ParkingSpot, Reservation, ReservationSlot } from '../../core/models/api.models';

@Component({
  selector: 'app-employee-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './employee-dashboard.component.html',
  styleUrl: './employee-dashboard.component.scss'
})
export class EmployeeDashboardComponent implements OnInit {
  backendStatus = signal('Loading...');
  selectedDate = signal(this.todayAsString());
  selectedSlot = signal<ReservationSlot>('MORNING');
  needsElectric = signal(false);

  loadingSpots = signal(false);
  loadingReservations = signal(false);
  creatingReservation = signal(false);

  parkingSpots = signal<ParkingSpot[]>([]);
  myReservations = signal<Reservation[]>([]);

  success = signal('');
  error = signal('');

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadHealth();
    this.loadParkingSpots();
    this.loadMyReservations();
  }

  onDateChange(event: Event): void {
    this.selectedDate.set((event.target as HTMLInputElement).value);
    this.loadParkingSpots();
  }

  onSlotChange(event: Event): void {
    this.selectedSlot.set((event.target as HTMLSelectElement).value as ReservationSlot);
    this.loadParkingSpots();
  }

  onElectricChange(event: Event): void {
    this.needsElectric.set((event.target as HTMLInputElement).checked);
    this.loadParkingSpots();
  }

  reserve(): void {
    this.clearMessages();
    this.creatingReservation.set(true);

    this.api.createReservation({
      startDate: this.selectedDate(),
      endDate: this.selectedDate(),
      slot: this.selectedSlot(),
      needsElectric: this.needsElectric()
    }).subscribe({
      next: () => {
        this.success.set('Reservation created successfully.');
        this.creatingReservation.set(false);
        this.loadParkingSpots();
        this.loadMyReservations();
      },
      error: (err) => {
        this.error.set(err?.error?.message || 'Reservation failed.');
        this.creatingReservation.set(false);
      }
    });
  }

  checkIn(reservationId: number): void {
    this.clearMessages();

    this.api.checkIn(reservationId).subscribe({
      next: () => {
        this.success.set('Check-in completed.');
        this.loadMyReservations();
        this.loadParkingSpots();
      },
      error: (err) => {
        this.error.set(err?.error?.message || 'Check-in failed.');
      }
    });
  }

  cancelReservation(reservationId: number): void {
    this.clearMessages();

    this.api.cancelMyReservation(reservationId).subscribe({
      next: () => {
        this.success.set('Reservation cancelled.');
        this.loadMyReservations();
        this.loadParkingSpots();
      },
      error: (err) => {
        this.error.set(err?.error?.message || 'Cancellation failed.');
      }
    });
  }

  getAvailableCount(): number {
    return this.filteredParkingSpots().filter(s => s.status === 'AVAILABLE').length;
  }

  getReservedCount(): number {
    return this.filteredParkingSpots().filter(s => s.status === 'RESERVED').length;
  }

  getCheckedInCount(): number {
    return this.filteredParkingSpots().filter(s => s.status === 'CHECKED_IN').length;
  }

  getElectricCount(): number {
    return this.filteredParkingSpots().filter(s => s.electric).length;
  }

  getAvailableElectricCount(): number {
    return this.filteredParkingSpots().filter(s => s.electric && s.status === 'AVAILABLE').length;
  }

  getVisibleSpotCount(): number {
    return this.filteredParkingSpots().length;
  }

  filteredParkingSpots(): ParkingSpot[] {
    if (this.needsElectric()) {
      return this.parkingSpots().filter(spot => spot.electric);
    }
    return this.parkingSpots();
  }

  statusClass(status: string): string {
    switch (status) {
      case 'AVAILABLE':
        return 'badge badge-green';
      case 'RESERVED':
        return 'badge badge-orange';
      case 'CHECKED_IN':
        return 'badge badge-blue';
      case 'RELEASED':
        return 'badge badge-red';
      case 'CANCELLED':
        return 'badge badge-red';
      default:
        return 'badge';
    }
  }

  canCheckIn(reservation: Reservation): boolean {
    return reservation.status === 'RESERVED';
  }

  private loadHealth(): void {
    this.api.getHealth().subscribe({
      next: (res) => this.backendStatus.set(res.status),
      error: () => this.backendStatus.set('DOWN')
    });
  }

  private loadParkingSpots(): void {
    this.loadingSpots.set(true);

    this.api.getParkingSpots(this.selectedDate(), this.selectedSlot()).subscribe({
      next: (spots) => {
        this.parkingSpots.set(
          [...spots].sort((a, b) => a.code.localeCompare(b.code))
        );
        this.loadingSpots.set(false);
      },
      error: () => {
        this.loadingSpots.set(false);
        this.error.set('Unable to load parking spots.');
      }
    });
  }

  private loadMyReservations(): void {
    this.loadingReservations.set(true);

    this.api.getMyReservations().subscribe({
      next: (reservations) => {
        this.myReservations.set(
          [...reservations].sort((a, b) =>
            `${a.reservationDate}-${a.slot}`.localeCompare(`${b.reservationDate}-${b.slot}`)
          )
        );
        this.loadingReservations.set(false);
      },
      error: () => {
        this.loadingReservations.set(false);
        this.error.set('Unable to load reservations.');
      }
    });
  }

  private clearMessages(): void {
    this.success.set('');
    this.error.set('');
  }

  private todayAsString(): string {
    return new Date().toISOString().split('T')[0];
  }
}
