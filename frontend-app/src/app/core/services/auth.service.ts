import { Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';
import { tap } from 'rxjs';
import { ApiService } from './api.service';
import { TokenService } from './token.service';
import { LoginRequest, UserRole } from '../models/api.models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  isAuthenticated = signal(false);
  email = signal<string | null>(null);
  role = signal<UserRole | null>(null);

  constructor(
    private api: ApiService,
    private tokenService: TokenService,
    private router: Router
  ) {
    this.refreshState();
  }

  login(payload: LoginRequest) {
    return this.api.login(payload).pipe(
      tap((response) => {
        this.tokenService.setToken(response.token);
        this.refreshState();
      })
    );
  }

  logout(): void {
    this.tokenService.clear();
    this.refreshState();
    this.router.navigate(['/login']);
  }

  refreshState(): void {
    this.isAuthenticated.set(this.tokenService.isLoggedIn());
    this.email.set(this.tokenService.getEmail());
    this.role.set(this.tokenService.getRole());
  }

  hasRole(roles: UserRole[]): boolean {
    const currentRole = this.role();
    return !!currentRole && roles.includes(currentRole);
  }
}
