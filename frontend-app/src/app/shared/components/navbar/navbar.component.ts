import { Component, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent {

  constructor(public auth: AuthService) {}

  isAuthenticated = computed(() => this.auth.isAuthenticated());
  email = computed(() => this.auth.email());
  role = computed(() => this.auth.role());

  canSeeAdmin = computed(() => this.role() === 'SECRETARY');
  canSeeManager = computed(() => this.role() === 'MANAGER' || this.role() === 'SECRETARY');

  logout(): void {
    this.auth.logout();
  }
}
