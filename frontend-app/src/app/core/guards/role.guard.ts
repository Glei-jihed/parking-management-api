import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { TokenService } from '../services/token.service';
import { UserRole } from '../models/api.models';

export function roleGuard(roles: UserRole[]): CanActivateFn {
  return () => {
    const tokenService = inject(TokenService);
    const router = inject(Router);

    const role = tokenService.getRole();

    if (role && roles.includes(role)) {
      return true;
    }

    router.navigate(['/']);
    return false;
  };
}
