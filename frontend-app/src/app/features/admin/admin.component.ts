import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { Reservation, ReservationSlot, ReservationStatus, User, UserRole } from '../../core/models/api.models';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.scss'
})
export class AdminComponent implements OnInit {
  users = signal<User[]>([]);
  reservations = signal<Reservation[]>([]);
  loading = signal(false);
  error = signal('');
  success = signal('');

  newUser = {
    email: '',
    password: 'password',
    role: 'EMPLOYEE' as UserRole
  };

  adminReservation = {
    userId: 0,
    startDate: new Date().toISOString().split('T')[0],
    endDate: new Date().toISOString().split('T')[0],
    slot: 'MORNING' as ReservationSlot,
    needsElectric: false
  };

  editReservationId: number | null = null;
  editReservationDate = '';
  editReservationSlot: ReservationSlot = 'MORNING';
  editReservationStatus: ReservationStatus = 'RESERVED';
  editReservationElectric = false;

  roles: UserRole[] = ['EMPLOYEE', 'SECRETARY', 'MANAGER'];
  slots: ReservationSlot[] = ['MORNING', 'AFTERNOON', 'FULL_DAY'];
  statuses: ReservationStatus[] = ['RESERVED', 'CHECKED_IN', 'RELEASED', 'CANCELLED'];

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.reload();
  }

  reload(): void {
    this.clearMessages();
    this.loading.set(true);

    this.api.getUsers().subscribe({
      next: (users) => this.users.set(users),
      error: (err) => this.error.set(err?.error?.message || 'Unable to load users')
    });

    this.api.getAdminReservations().subscribe({
      next: (reservations) => {
        this.reservations.set(reservations);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.message || 'Unable to load reservations');
        this.loading.set(false);
      }
    });
  }

  createUser(): void {
    this.clearMessages();

    this.api.createUser(this.newUser).subscribe({
      next: () => {
        this.success.set('User created');
        this.newUser = { email: '', password: 'password', role: 'EMPLOYEE' };
        this.reload();
      },
      error: (err) => this.error.set(err?.error?.message || 'User creation failed')
    });
  }

  toggleUser(user: User): void {
    this.clearMessages();

    this.api.toggleUserActive(user.id, !user.active).subscribe({
      next: () => {
        this.success.set('User updated');
        this.reload();
      },
      error: (err) => this.error.set(err?.error?.message || 'User update failed')
    });
  }

  createAdminReservation(): void {
    this.clearMessages();

    this.api.createAdminReservation(this.adminReservation.userId, {
      startDate: this.adminReservation.startDate,
      endDate: this.adminReservation.endDate,
      slot: this.adminReservation.slot,
      needsElectric: this.adminReservation.needsElectric
    }).subscribe({
      next: () => {
        this.success.set('Admin reservation created');
        this.reload();
      },
      error: (err) => this.error.set(err?.error?.message || 'Reservation creation failed')
    });
  }

  startEdit(reservation: Reservation): void {
    this.editReservationId = reservation.id;
    this.editReservationDate = reservation.reservationDate;
    this.editReservationSlot = reservation.slot;
    this.editReservationStatus = reservation.status;
    this.editReservationElectric = reservation.electricRequired;
  }

  saveEdit(): void {
    if (!this.editReservationId) {
      return;
    }

    this.clearMessages();

    this.api.updateAdminReservation(this.editReservationId, {
      reservationDate: this.editReservationDate,
      slot: this.editReservationSlot,
      status: this.editReservationStatus,
      electricRequired: this.editReservationElectric
    }).subscribe({
      next: () => {
        this.success.set('Reservation updated');
        this.editReservationId = null;
        this.reload();
      },
      error: (err) => this.error.set(err?.error?.message || 'Reservation update failed')
    });
  }

  cancelAdminReservation(id: number): void {
    this.clearMessages();

    this.api.cancelAdminReservation(id).subscribe({
      next: () => {
        this.success.set('Reservation cancelled');
        this.reload();
      },
      error: (err) => this.error.set(err?.error?.message || 'Cancellation failed')
    });
  }

  statusClass(status: string): string {
    switch (status) {
      case 'RESERVED': return 'badge badge-orange';
      case 'CHECKED_IN': return 'badge badge-blue';
      case 'RELEASED': return 'badge badge-red';
      case 'CANCELLED': return 'badge badge-red';
      default: return 'badge';
    }
  }

  private clearMessages(): void {
    this.error.set('');
    this.success.set('');
  }
}
