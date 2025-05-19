import { Role } from "./enums/role.enum";

export interface User {
  id: number;
  username: string;
  email: string;
  fullName: string;
  role: Role;
  enabled: boolean;
}
