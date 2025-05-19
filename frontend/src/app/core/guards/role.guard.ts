import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { ToastrService } from 'ngx-toastr';

export const RoleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const toastr = inject(ToastrService);
  
  // Get required roles from route data
  const requiredRoles = route.data['roles'] as string[];
  
  if (authService.isAuthenticated() && requiredRoles) {
    // Check if user has any of the required roles
    if (authService.hasRole(requiredRoles)) {
      return true;
    }
    
    // User doesn't have the required role
    toastr.error('You do not have permission to access this page', 'Access Denied');
    router.navigate(['/dashboard']);
    return false;
  }
  
  // Not logged in, redirect to login page
  router.navigate(['/auth/login'], { queryParams: { returnUrl: state.url } });
  return false;
};
