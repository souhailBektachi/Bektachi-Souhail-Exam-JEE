import { StatutCredit } from "./enums/statut-credit.enum";

export interface CreditSummary {
  id: number;
  dateDemande: string; // Or Date
  statut: StatutCredit;
  montant: number;
  dureeRemboursement: number;
  type: string; // "PERSONNEL", "IMMOBILIER", "PROFESSIONNEL"
  motif?: string; // Optional, present for personnel and professionnel
  typeBienFinance?: string; // Optional, present for immobilier
  raisonSocialeEntreprise?: string; // Optional, present for professionnel
}
