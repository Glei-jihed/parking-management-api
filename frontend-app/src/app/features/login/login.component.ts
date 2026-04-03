import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {

  email = 'employee@parking.local';
  password = 'password';

  loading = signal(false);
  error = signal('');

  constructor(
    private auth: AuthService,
    private router: Router
  ) {}

  submit(): void {
    if (!this.email || !this.password) {
      this.error.set('Please fill all fields');
      return;
    }

    this.loading.set(true);
    this.error.set('');

    this.auth.login({
      email: this.email.trim(),
      password: this.password
    }).subscribe({
      next: () => {
        this.loading.set(false);
        this.router.navigate(['/']);
      },
      error: (err) => {
        this.loading.set(false);
        this.error.set(err?.error?.message || 'Invalid credentials');
      }
    });
  }
}
