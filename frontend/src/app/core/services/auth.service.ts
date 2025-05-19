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
  token: string; // Changed from token?: string
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
export class AuthService {  private apiUrl = `${environment.apiUrl}/api/auth`;
  private currentUserSubject: BehaviorSubject<AuthResponse | null>; // For storing current user info
  public currentUser$: Observable<AuthResponse | null>; // Added type annotation
  private jwtHelper = new JwtHelperService();
  private loggedInStatus = false; // Simple flag for logged-in state
  private authTokenKey = 'auth_token';  constructor(private http: HttpClient, private router: Router) {
    // Check for existing token on service initialization
    const token = localStorage.getItem(this.authTokenKey);
    this.loggedInStatus = !!token;
    
    this.currentUserSubject = new BehaviorSubject<AuthResponse | null>(null);
    if (token) {
        // Attempt to parse user from localStorage if available, otherwise just use token
        const userJson = localStorage.getItem('current_user');
        if (userJson) {
            try {
                const user: User = JSON.parse(userJson);
                // Ensure the stored user has a token, or use the one from authTokenKey
                const userToken = user.token || token;
                if (userToken) {
                    this.currentUserSubject.next({ 
                        token: userToken,
                        username: user.username,
                        role: user.role,
                        // Add other properties from AuthResponse if they exist in User or are derivable
                    } as AuthResponse);
                } else {
                    this.currentUserSubject.next({ token } as AuthResponse); // Fallback if no user.token
                }
            } catch (e) {
                this.currentUserSubject.next({ token } as AuthResponse); // Fallback on parse error
            }
        } else {
            this.currentUserSubject.next({ token } as AuthResponse); // Fallback if no current_user
        }
    }
    this.currentUser$ = this.currentUserSubject.asObservable(); // Initialize here
  }  login(credentials: LoginRequest): Observable<User> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      map(response => {
        if (!response.username || !response.role || !response.token) {
            throw new Error('Incomplete auth response from server');
        }
        const user: User = {
          username: response.username,
          role: response.role,
          token: response.token
        };
        
        // Store token exactly as received - the interceptor will add the Bearer prefix
        localStorage.setItem(this.authTokenKey, response.token);
        localStorage.setItem('current_user', JSON.stringify(user));
        this.currentUserSubject.next(response); // Pass the whole AuthResponse
        this.loggedInStatus = true; // Update logged in status
        
        console.log('Token stored in localStorage');
        
        return user;
      }),
      catchError(error => {
        console.error('Login error:', error);
        return throwError(() => error);
      })
    );
  }

  register(userData: RegisterRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, userData);
  }
  logout(): void {
    localStorage.removeItem(this.authTokenKey);
    localStorage.removeItem('current_user');
    this.currentUserSubject.next(null);
    this.loggedInStatus = false;
    this.router.navigate(['/auth/login']);
  }
  checkAuthState(): void {
    const token = localStorage.getItem(this.authTokenKey);
    const userJson = localStorage.getItem('current_user');
    
    if (token && userJson) {
      try {
        if (!this.jwtHelper.isTokenExpired(token)) {
          const user: AuthResponse = JSON.parse(userJson); // Expect AuthResponse structure
          this.currentUserSubject.next(user);
          this.loggedInStatus = true;
        } else {
          this.logout(); // Token expired
        }
      } catch (error) {
        console.error('Error checking token:', error);
        this.logout();
      }
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
    if (!currentUser || !currentUser.role) return false; // Add null check for currentUser.role
    
    if (Array.isArray(role)) {
      return role.includes(currentUser.role);
    }
    
    return currentUser.role === role;
  }
  isLoggedIn(): boolean {
    // Check if token exists and is not expired
    const token = localStorage.getItem(this.authTokenKey);
    if (!token) {
      return false;
    }
    
    // Check if token is expired
    try {
      return !this.jwtHelper.isTokenExpired(token);
    } catch (error) {
      console.error('Error checking token expiry:', error);
      return false;
    }
  }  getToken(): string | null {
    // Just retrieve the raw token
    const token = localStorage.getItem(this.authTokenKey);
    if (token) {
      console.log('Retrieved token from localStorage');
      return token.startsWith('Bearer ') ? token.substring(7) : token;
    }
    return null;
  }

  getCurrentUser(): Observable<AuthResponse | null> {
    return this.currentUserSubject.asObservable();
  }
}
