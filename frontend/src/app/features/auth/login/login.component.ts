import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';
import { LoginRequest } from '../../../core/models/auth.model';

@Component({
  selector: 'app-login',
  template: `
    <div class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div class="max-w-md w-full space-y-8">
        <div>
          <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900">Sign in to your account</h2>
          <p class="mt-2 text-center text-sm text-gray-600">
            Manage your credits and loans efficiently
          </p>
        </div>
        <form class="mt-8 space-y-6" (ngSubmit)="login()">
          <div class="rounded-md shadow-sm -space-y-px">
            <div>
              <label for="username" class="sr-only">Username</label>
              <input id="username" name="username" type="text" required 
                class="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 
                placeholder-gray-500 text-gray-900 rounded-t-md focus:outline-none focus:ring-indigo-500 
                focus:border-indigo-500 focus:z-10 sm:text-sm" 
                placeholder="Username" 
                [(ngModel)]="credentials.username">
            </div>
            <div>
              <label for="password" class="sr-only">Password</label>
              <input id="password" name="password" type="password" required 
                class="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 
                placeholder-gray-500 text-gray-900 rounded-b-md focus:outline-none focus:ring-indigo-500 
                focus:border-indigo-500 focus:z-10 sm:text-sm" 
                placeholder="Password" 
                [(ngModel)]="credentials.password">
            </div>
          </div>

          <div>
            <button type="submit" 
              class="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm 
              font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none 
              focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">
              <span class="absolute left-0 inset-y-0 flex items-center pl-3">
                <!-- Heroicon name: solid/lock-closed -->
                <svg class="h-5 w-5 text-indigo-500 group-hover:text-indigo-400" xmlns="http://www.w3.org/2000/svg" 
                  viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                  <path fill-rule="evenodd" 
                    d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" 
                    clip-rule="evenodd" />
                </svg>
              </span>
              Sign in
            </button>
          </div>
        </form>
        
        <div *ngIf="message" 
          [ngClass]="{'bg-red-100 text-red-700': isError, 'bg-green-100 text-green-700': !isError}"
          class="p-4 rounded-md mt-4">
          {{ message }}
        </div>
        
        <div class="text-sm text-center mt-4">
          <p>Don't have an account? 
            <a routerLink="/auth/register" class="font-medium text-indigo-600 hover:text-indigo-500">
              Register here
            </a>
          </p>
        </div>
      </div>
    </div>
  `,
  standalone: true,
  imports: [
    FormsModule,
    CommonModule,
    RouterLink
  ]
})
export class LoginComponent {
  credentials: LoginRequest = {
    username: '',
    password: ''
  };
  message: string = '';
  isError: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}
  login() {
    this.message = 'Signing in...';
    this.isError = false;
    
    if (!this.credentials.username || !this.credentials.password) {
      this.message = 'Username and password are required.';
      this.isError = true;
      return;
    }
    
    this.authService.login(this.credentials).subscribe({
      next: (response) => {
        this.message = 'Login successful!';
        console.log('Login successful', response);
        
        // Navigate to dashboard immediately after successful login
        this.router.navigate(['/dashboard']).then(
          navigated => {
            if (!navigated) {
              console.error('Navigation to dashboard failed');
              this.message = 'Error navigating to dashboard. Please try refreshing the page.';
            }
          }
        );
      },
      error: (error) => {
        this.isError = true;
        this.message = error.error?.message || 'Login failed. Please check your credentials or try again later.';
        console.error('Login failed', error);
      }
    });
  }
}
