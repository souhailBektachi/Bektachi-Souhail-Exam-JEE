export class AuthTokenResponse {
  token: string;
  type: string;
  id?: number;
  username: string;
  email?: string;
  role: string;
  expiresIn?: number;
}
