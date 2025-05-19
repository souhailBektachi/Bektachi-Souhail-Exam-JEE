import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const authService = inject(AuthService);
  
  return next(req).pipe(
    catchError(error => {
      if (error.status === 401) {
        // Auto logout if 401 response returned from API
        authService.logout();
        console.error('Your session has expired. Please log in again.');
        router.navigate(['/auth/login']);
      } else if (error.status === 403) {
        console.error('You do not have permission to perform this action');
        router.navigate(['/dashboard']);
      } else if (error.status === 500) {
        console.error('An unexpected error occurred. Please try again later.');
      } else {
        // Show error message
        const errorMessage = error.error?.message || error.message || 'An error occurred';
        console.error(errorMessage);
      }
      
      return throwError(() => error);
    })
  );
};
