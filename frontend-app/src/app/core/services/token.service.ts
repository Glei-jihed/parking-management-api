import { Injectable } from '@angular/core';
import { UserRole } from '../models/api.models';

interface JwtPayload {
  sub: string;
  role: UserRole;
  userId: number;
  exp: number;
}

@Injectable({
  providedIn: 'root'
})
export class TokenService {
  private readonly storageKey = 'parking_token';

  setToken(token: string): void {
    localStorage.setItem(this.storageKey, token);
  }

  getToken(): string | null {
    return localStorage.getItem(this.storageKey);
  }

  clear(): void {
    localStorage.removeItem(this.storageKey);
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }

    const payload = this.decode(token);
    if (!payload?.exp) {
      return false;
    }

    return payload.exp * 1000 > Date.now();
  }

  getEmail(): string | null {
    const payload = this.readPayload();
    return payload?.sub ?? null;
  }

  getRole(): UserRole | null {
    const payload = this.readPayload();
    return payload?.role ?? null;
  }

  getUserId(): number | null {
    const payload = this.readPayload();
    return payload?.userId ?? null;
  }

  private readPayload(): JwtPayload | null {
    const token = this.getToken();
    if (!token) {
      return null;
    }
    return this.decode(token);
  }

  private decode(token: string): JwtPayload | null {
    try {
      const payload = token.split('.')[1];
      const normalized = payload.replace(/-/g, '+').replace(/_/g, '/');
      const decoded = JSON.parse(atob(normalized));
      return decoded as JwtPayload;
    } catch {
      return null;
    }
  }
}
