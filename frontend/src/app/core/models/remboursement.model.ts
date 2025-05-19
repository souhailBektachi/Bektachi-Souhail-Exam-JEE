import { TypeRemboursement } from "./enums/type-remboursement.enum";

export interface Remboursement {
  id: number;
  date: string; // Or Date
  montant: number;
  type: TypeRemboursement;
  creditId: number;
}
