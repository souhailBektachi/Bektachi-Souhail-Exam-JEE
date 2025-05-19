export interface LoginRequest {
  username: string; // Changed to non-optional
  password: string; // Changed to non-optional
}

export interface AuthResponse {
  token: string;
  username?: string; // Add if backend returns username
  role?: string;     // Add if backend returns role
  // Add other response fields like user details
}
