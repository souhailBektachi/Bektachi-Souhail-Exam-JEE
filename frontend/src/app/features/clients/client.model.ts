export interface Client {
  id: number; // Changed from Long to number for TypeScript
  nom: string;
  email: string;
  nombreCredits: number;
}
