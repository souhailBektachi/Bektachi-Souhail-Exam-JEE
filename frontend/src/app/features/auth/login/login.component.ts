import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms'; // Import FormsModule for ngModel
import { RouterLink } from '@angular/router'; // Import RouterLink
import { CommonModule } from '@angular/common'; // Import CommonModule for *ngIf
import { AuthService } from '../../../core/services/auth.service'; // Adjust path as needed
import { LoginRequest } from '../../../core/models/auth.model'; // Adjust path as needed

@Component({
  selector: 'app-login',
  template: `
    <h2>Login</h2>
    <form (ngSubmit)="login()">
      <div>
        <label for="username">Username:</label>
        <input type="text" id="username" name="username" [(ngModel)]="credentials.username" required>
      </div>
      <div>
        <label for="password">Password:</label>
        <input type="password" id="password" name="password" [(ngModel)]="credentials.password" required>
      </div>
      <button type="submit">Login</button>
    </form>
    <p *ngIf="message">{{ message }}</p>
    <p>Don't have an account? <a routerLink="/auth/register">Register here</a></p>
  `,
  standalone: true,
  imports: [
    FormsModule,
    CommonModule, // Add CommonModule
    RouterLink
    // HttpClientModule is removed as AuthService will handle HTTP calls
  ]
})
export class LoginComponent {
  credentials: LoginRequest = { // Use LoginRequest type
    username: '',
    password: ''
  };
  message: string = '';

  constructor(private authService: AuthService) {} // Inject AuthService

  login() {
    this.message = 'Attempting to log in...';
    // Ensure credentials match the LoginRequest type expected by authService.login
    if (!this.credentials.username || !this.credentials.password) {
        this.message = 'Username and password are required.';
        return;
    }
    this.authService.login(this.credentials as Required<LoginRequest>).subscribe({ // Use type assertion if needed after validation
      next: (response) => {
        this.message = 'Login successful!';
        console.log('Login successful', response);
        // Handle successful login (e.g., store token, navigate)
      },
      error: (error) => {
        this.message = 'Login failed. Please check your credentials or try again later.';
        console.error('Login failed', error);
        // Handle login error
      }
    });
  }
}
