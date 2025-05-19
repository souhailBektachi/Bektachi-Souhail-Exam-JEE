import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { JwtHelperService } from '@auth0/angular-jwt';
import { environment } from '../../../environments/environment';

export interface User {
  id?: number;
  username: string;
  role: string;
  token?: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  email: string;
  fullName: string;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  username: string;
  role: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/api/auth`;
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();
  private jwtHelper = new JwtHelperService();

  constructor(private http: HttpClient, private router: Router) {}

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

  getToken(): string | null {
    return localStorage.getItem('auth_token');
  }
}
