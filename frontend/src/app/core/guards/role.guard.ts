import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const RoleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  
  // Get required roles from route data
  const requiredRoles = route.data['roles'] as string[];
  
  if (authService.isAuthenticated() && requiredRoles) {
    // Check if user has any of the required roles
    if (authService.hasRole(requiredRoles)) {
      return true;
    }
    
    // User doesn't have the required role
    console.error('Access denied: Required roles not met');
    router.navigate(['/dashboard']);
    return false;
  }
  
  // Not logged in, redirect to login page
  router.navigate(['/auth/login'], { queryParams: { returnUrl: state.url } });
  return false;
};
