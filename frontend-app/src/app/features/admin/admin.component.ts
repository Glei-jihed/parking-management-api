import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import {
  Reservation,
  ReservationSlot,
  ReservationStatus,
  User,
  UserRole
} from '../../core/models/api.models';

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

  roles: UserRole[] = ['EMPLOYEE', 'SECRETARY', 'MANAGER'];
  slots: ReservationSlot[] = ['MORNING', 'AFTERNOON', 'FULL_DAY'];
  statuses: ReservationStatus[] = ['RESERVED', 'CHECKED_IN', 'RELEASED', 'CANCELLED'];

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

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.reload();
  }

  reload(): void {
    this.loading.set(true);
    this.clearMessages();

    this.api.getUsers().subscribe({
      next: (users) => this.users.set(users),
      error: () => this.error.set('Unable to load users')
    });

    this.api.getAdminReservations().subscribe({
      next: (res) => {
        this.reservations.set(res);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Unable to load reservations');
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
      error: () => this.error.set('User creation failed')
    });
  }

  toggleUser(user: User): void {
    this.api.toggleUserActive(user.id, !user.active).subscribe({
      next: () => this.reload(),
      error: () => this.error.set('User update failed')
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
        this.success.set('Reservation created');
        this.reload();
      },
      error: () => this.error.set('Reservation failed')
    });
  }

  startEdit(r: Reservation): void {
    this.editReservationId = r.id;
    this.editReservationDate = r.reservationDate;
    this.editReservationSlot = r.slot;
    this.editReservationStatus = r.status;
    this.editReservationElectric = r.electricRequired;
  }

  saveEdit(): void {
    if (!this.editReservationId) return;

    this.api.updateAdminReservation(this.editReservationId, {
      reservationDate: this.editReservationDate,
      slot: this.editReservationSlot,
      status: this.editReservationStatus,
      electricRequired: this.editReservationElectric
    }).subscribe({
      next: () => {
        this.success.set('Updated');
        this.editReservationId = null;
        this.reload();
      },
      error: () => this.error.set('Update failed')
    });
  }

  cancelAdminReservation(id: number): void {
    this.api.cancelAdminReservation(id).subscribe({
      next: () => this.reload(),
      error: () => this.error.set('Cancel failed')
    });
  }

  statusClass(status: string): string {
    switch (status) {
      case 'RESERVED': return 'badge badge-orange';
      case 'CHECKED_IN': return 'badge badge-blue';
      case 'CANCELLED': return 'badge badge-red';
      default: return 'badge';
    }
  }

  private clearMessages(): void {
    this.error.set('');
    this.success.set('');
  }
}
