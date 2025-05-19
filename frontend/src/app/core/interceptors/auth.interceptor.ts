import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { HttpMethod } from '../models/enums/http-method';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Skip the auth header for OPTIONS requests to avoid CORS preflight issues
  if (req.method.toUpperCase() === HttpMethod.OPTIONS) {
    return next(req);
  }
  
  const authService = inject(AuthService);
  const token = authService.getToken();
  
  if (token) {
    // Backend expects: Authorization: Bearer <token>
    const authHeader = `Bearer ${token}`;
    
    console.log(`Adding authorization header for ${req.url}`);
    
    const authReq = req.clone({
      headers: req.headers.set('Authorization', authHeader)
    });
    return next(authReq);
  }
  
  return next(req);
};
