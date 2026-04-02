import { Routes } from '@angular/router';
import { LoginComponent } from './features/login/login.component';
import { EmployeeDashboardComponent } from './features/employee-dashboard/employee-dashboard.component';
import { AdminComponent } from './features/admin/admin.component';
import { ManagerDashboardComponent } from './features/manager-dashboard/manager-dashboard.component';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: '',
    component: EmployeeDashboardComponent,
    canActivate: [authGuard]
  },
  {
    path: 'admin',
    component: AdminComponent,
    canActivate: [authGuard, roleGuard(['SECRETARY'])]
  },
  {
    path: 'manager',
    component: ManagerDashboardComponent,
    canActivate: [authGuard, roleGuard(['MANAGER', 'SECRETARY'])]
  },
  { path: '**', redirectTo: '' }
];
