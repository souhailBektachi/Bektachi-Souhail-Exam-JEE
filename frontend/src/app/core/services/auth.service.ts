import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of, tap, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { Router } from '@angular/router';
import { JwtHelperService } from '@auth0/angular-jwt';
import { environment } from '../../../environments/environment';
import { LoginRequest, AuthResponse } from '../models/auth.model'; // Adjust path as needed

export interface User {
  id?: number;
  username: string;
  role: string;
  token?: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  email: string;
  fullName: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/api/auth`;
  private currentUserSubject: BehaviorSubject<AuthResponse | null>; // For storing current user info
  public currentUser$ = this.currentUserSubject.asObservable();
  private jwtHelper = new JwtHelperService();
  private loggedInStatus = false; // Simple flag for logged-in state
  private authTokenKey = 'authToken';

  constructor(private http: HttpClient, private router: Router) {
    // Check for existing token on service initialization
    this.loggedInStatus = !!localStorage.getItem(this.authTokenKey);
    const token = localStorage.getItem(this.authTokenKey);
    
    this.currentUserSubject = new BehaviorSubject<AuthResponse | null>(null);
    if (token) {
        this.currentUserSubject.next({ token } as AuthResponse); // Partial, needs more user info
    }
  }

  login(credentials: LoginRequest): Observable<User> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      map(response => {
        const user: User = {
          username: response.username,
          role: response.role,
          token: response.token
        };
        
        localStorage.setItem('auth_token', response.token);
        localStorage.setItem('current_user', JSON.stringify(user));
        this.currentUserSubject.next(user);
        
        return user;
      }),
      catchError(error => {
        return throwError(() => error);
      })
    );
  }

  register(userData: RegisterRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, userData);
  }

  logout(): void {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('current_user');
    this.currentUserSubject.next(null);
    this.router.navigate(['/auth/login']);
  }

  checkAuthState(): void {
    const token = localStorage.getItem('auth_token');
    const userJson = localStorage.getItem('current_user');
    
    if (token && userJson && !this.jwtHelper.isTokenExpired(token)) {
      const user = JSON.parse(userJson);
      this.currentUserSubject.next(user);
    } else {
      this.logout();
    }
  }

  isAuthenticated(): boolean {
    const token = localStorage.getItem('auth_token');
    return token !== null && !this.jwtHelper.isTokenExpired(token);
  }

  hasRole(role: string | string[]): boolean {
    const currentUser = this.currentUserSubject.value;
    if (!currentUser) return false;
    
    if (Array.isArray(role)) {
      return role.includes(currentUser.role);
    }
    
    return currentUser.role === role;
  }

  isLoggedIn(): boolean {
    // Check if token exists and is not expired (add expiry check if applicable)
    const token = localStorage.getItem(this.authTokenKey);
    return !!token; // More robust check: return !!this.currentUserSubject.value && !!this.currentUserSubject.value.token;
  }

  getToken(): string | null {
    return localStorage.getItem('auth_token');
  }

  getCurrentUser(): Observable<AuthResponse | null> {
    return this.currentUserSubject.asObservable();
  }
}
